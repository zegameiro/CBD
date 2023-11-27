package com.example.Ex4.b;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AttendanceSystemQuant {

    private static int LIMIT = 15;
    private static int TIMESLOT = 12000; 

    public static void main(String[] args) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.0.1";
        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("sysatt");
        MongoCollection<Document> collection = database.getCollection("usersquant");

        Scanner sc = new Scanner(System.in);
        String username_answer, product_answer, quant_answer;

        System.out.println("Service System");
        System.out.println();

        while(true) {
            System.out.println("Add a new request (press 'Enter' to exit)");
            System.out.print("Insert username: ");
            username_answer = sc.nextLine().strip();

            if(username_answer.length() == 0){
                System.out.println("Ending the program...");
                break;

            } else {
                System.out.print("Insert product: ");
                product_answer = sc.nextLine().strip();

                if(product_answer.length() == 0) {

                    while(product_answer.length() == 0) {

                        System.out.println("ERROR: invalid product");
                        System.out.print("Insert a valid product: ");
                        product_answer = sc.nextLine().strip();
                    }

                } else {

                    System.out.print("Insert quantity of the product: ");
                    quant_answer = sc.nextLine().strip();

                    if(quant_answer.equals("0") || quant_answer.length() == 0) {

                        while(quant_answer.equals("0") || quant_answer.length() == 0) {

                            System.out.println("ERROR: invalid quantity");
                            System.out.print("Insert valid quantity: ");
                            quant_answer = sc.nextLine().strip();
                        }
                    }

                    int time = (int) System.currentTimeMillis();
                    addRequest(collection, username_answer, product_answer, Integer.parseInt(quant_answer), time);
                }
            }
        }
        sc.close();
    }

    public static void addRequest(MongoCollection<Document> col, String user, String product, int quant,int ts) {
        int time = (int) System.currentTimeMillis();
        List<Document> pipeline = Arrays.asList(
            new Document("$match", new Document(user + ".prods.timestamp", new Document("$gte", time - TIMESLOT).append("$lte", time))),
            new Document("$project", new Document("quantCount", new Document("$sum", "$" + user + ".prods.quantity")))
        );

        AggregateIterable<Document> result = col.aggregate(pipeline);
        Document d = result.first();

        Integer prodsQuantCount = quant;

        if (d != null) 
            prodsQuantCount += d.getInteger("quantCount");

        if (prodsQuantCount <= LIMIT) {
            Document userExists = col.find(new Document(user, new Document("$exists", true))).first();

            if (userExists != null) {
                col.updateOne(userExists, 
                new Document("$push", new Document(user + ".prods", new Document(
                    "prod", product
                    ).append("timestamp", ts)
                    .append("quantity", quant)
                )));
            } else {
                Document doc = new Document(
                    user, new Document(
                        "prods", Arrays.asList(
                            new Document("prod", product)
                                .append("timestamp", ts)
                                .append("quantity", quant)
                        )
                    )
                );
                col.insertOne(doc);
            }

            System.out.println("  Request from " + user + " (product: " + product + ", quantity: " + quant + ") accepted with success\n");
        } else
            System.out.println("  The request from " + user + " with the product " + product + " (quant: " + quant + ") has been rejected cause it reached the max quantity of products " + LIMIT + "\n");
    }
}