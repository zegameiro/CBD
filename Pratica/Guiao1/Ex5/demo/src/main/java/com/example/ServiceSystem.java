package com.example;

import redis.clients.jedis.Jedis;

import java.util.Scanner;
import java.util.List;

public class ServiceSystem {

    public Jedis jedis;

    private static int LIMIT = 2;
    private static int timeslot = 120000; // In seconds

    // Redis Data Structure used: Sorted Set
    // key -> username
    // score -> timestamp
    // value -> product

    public ServiceSystem() {
        this.jedis = new Jedis();
    }

    public void addRequest(String username, String product, int ts) {
        int time = (int) System.currentTimeMillis();
        List<String> requests = jedis.zrangeByScore(username, time - timeslot, time);

        if(requests.size() < LIMIT) {
            jedis.zadd(username, ts, product);
            System.out.println("  Request from " + username + " (product: " + product + ") accepted with success");
            System.out.println();
        } else 
            System.out.println("  The request from " + username + " with the product " + product + " has been rejected cause it passed the limit of products " + LIMIT);
            System.out.println();  
    }

    public static void main(String[] args) {
        ServiceSystem board = new ServiceSystem();
        Scanner sc = new Scanner(System.in);
        String username_answer, product_answer;

        System.out.println("Service System");
        System.out.println();

        while(true) {
            System.out.println("Add a new request");
            System.out.print("Input username: ");
            username_answer = sc.nextLine().strip();

            if(username_answer.length() == 0){
                System.out.println("Ending the program...");
                break;

            } else {
                System.out.print("Input product: ");
                product_answer = sc.nextLine().strip();

                if(product_answer.length() == 0) {

                    while(product_answer.length() == 0) {

                        System.out.println("ERROR: invalid product");
                        System.out.print("Input product: ");
                        product_answer = sc.nextLine().toLowerCase().strip();
                    }

                } else {
                
                    int time = (int) System.currentTimeMillis();
                    board.addRequest(username_answer, product_answer, time);
                }
            }
        }
        sc.close();
    }
}
