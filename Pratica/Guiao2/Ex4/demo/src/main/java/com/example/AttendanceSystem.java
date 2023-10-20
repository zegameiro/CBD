package com.example;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AttendanceSystem {

    private static int LIMIT = 2;
    private static int TIMESLOT = 12000; 

    public static void main(String[] args) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.0.1";
        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("sysatt");
        MongoCollection<Document> collection = database.getCollection("users");

        Scanner sc = new Scanner(System.in);
        String user_answer, prod_answer;


        System.out.println("Service System\n");

        while(true) {
            System.out.println("Add a new request (Click on 'Enter' to exit)");
            System.out.print("Input username: ");
            user_answer = sc.nextLine().strip();

            if(user_answer.length() == 0){
                System.out.println("Ending the program...");
                break;
            } else {
                System.out.print("Input product: ");
                prod_answer = sc.nextLine().strip();

                if(prod_answer.length() == 0) {

                    while(prod_answer.length() == 0) {

                        System.out.println("ERROR: invalid product");
                        System.out.print("Input product: ");
                        prod_answer = sc.nextLine().toLowerCase().strip();
                    }

                } else {
                
                    int time = (int) System.currentTimeMillis();
                    addRequest(collection, user_answer, prod_answer, time);
                }
            }
        }
        sc.close();
    }

    public static void addRequest(MongoCollection<Document> col, String user, String product, int ts) {
        int time = (int) System.currentTimeMillis();
        List<Document> pipeline = Arrays.asList(
            new Document("$match", new Document(user + ".prods.timestamp", new Document("$gte", time - TIMESLOT).append("$lte", time))),
            new Document("$project", new Document("prodsCount", new Document("$size", "$" + user + ".prods")))
        );

        AggregateIterable<Document> result = col.aggregate(pipeline);
        Document d = result.first();

        Integer prodsCount = 0;

        if (d != null) 
            prodsCount = d.getInteger("prodsCount");

        if (prodsCount < LIMIT) {
            Document userExists = col.find(new Document(user, new Document("$exists", true))).first();

            if (userExists != null) {
                col.updateOne(userExists, 
                new Document("$push", new Document(user + ".prods", new Document(
                    "prod", product
                    ).append("timestamp", ts)
                )));
            } else {
                Document doc = new Document(
                    user, new Document(
                        "prods", Arrays.asList(
                            new Document("prod", product)
                                .append("timestamp", ts)
                        )
                    )
                );
                col.insertOne(doc);
            }

            System.out.println("  Request from " + user + " (product: " + product + ") accepted with success\n");
        } else
            System.out.println("  The request from " + user + " with the product " + product + " has been rejected cause it passed the limit of products " + LIMIT + "\n");
    }
}