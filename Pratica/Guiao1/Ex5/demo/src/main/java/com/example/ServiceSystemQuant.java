package com.example;

import java.util.List;
import java.util.Scanner;

import redis.clients.jedis.Jedis;

public class ServiceSystemQuant {
    public Jedis jedis;

    private static int LIMIT_QUANT = 15;
    private static int timeslot = 120000; // In seconds

    // Redis Data Structure used: Sorted Set
    // key -> username
    // score -> timestamp
    // value -> product_quantity

    public ServiceSystemQuant() {
        this.jedis = new Jedis();
    }

    public void addRequest(String username, String product_quant, int ts) {
        int time = (int) System.currentTimeMillis();
        List<String> requests = jedis.zrangeByScore(username, time - timeslot, time);
        int totalProducts = 0;
        String[] aux = product_quant.split("-");

        for(String pr : requests) {
            String[] words = pr.split("-");
            totalProducts += Integer.parseInt(words[1]);
        }

        if(totalProducts + Integer.parseInt(aux[1]) <= LIMIT_QUANT) {
            jedis.zadd(username, ts, product_quant);
            System.out.println("  Request from " + username + " (product: " + aux[0] + ", quantity: " + aux[1] + ") accepted with success");
            System.out.println();
        } else 
            System.out.println("  The request from " + username + " with the product " + aux[0] + " (quant: " + aux[1] + ") has been rejected cause it reached the max quantity of products " + LIMIT_QUANT);
            System.out.println();  
    }

    public static void main(String[] args) {
        ServiceSystemQuant board = new ServiceSystemQuant();
        Scanner sc = new Scanner(System.in);
        String username_answer, product_answer, quant_answer;

        System.out.println("Service System");
        System.out.println();

        while(true) {
            System.out.println("Add a new request");
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
                    board.addRequest(username_answer, product_answer + "-" + quant_answer, time);
                }
            }
        }
        sc.close();
    }
}
