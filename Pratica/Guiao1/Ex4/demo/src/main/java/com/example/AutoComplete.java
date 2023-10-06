package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.Scanner;

public class AutoComplete {

    public Jedis jedis;
    public static String USERS_NAMES = "users names";

    public AutoComplete() {
        jedis = new Jedis();
    }

    public void saveName(String name) {
        jedis.zadd(USERS_NAMES, 1,name);
    }

    public void getAutoComplete(String answer) {
        ScanParams scanParams = new ScanParams();
        scanParams.match(answer + "*");
        String cursor = ScanParams.SCAN_POINTER_START;

        boolean aux = true;
        while (aux) {
            ScanResult<Tuple> scanResult = jedis.zscan(USERS_NAMES, cursor, scanParams);
            List<Tuple> listRes = scanResult.getResult();

            // do whatever with the key-value pairs in result
            if (listRes.size()!=0){
                System.out.println(listRes.get(0).getElement());
            }
            cursor = scanResult.getCursor();
            if (cursor.equals("0")) {
                aux = false;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        AutoComplete board = new AutoComplete();
        File f = new File("../../Ex4/names.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));

        String line;

        while ((line = br.readLine()) != null)
            board.saveName(line);

        br.close();

        Scanner sc = new Scanner(System.in);

        System.out.print("Search for ('Enter' for quit):" );
        String answer = sc.nextLine().toLowerCase();

        while(true) {
            board.getAutoComplete(answer);
            System.out.print("Search for ('Enter' for quit):" );
            answer = sc.nextLine().toLowerCase();

            if(answer.equals(""))
                break;
        }

        sc.close();
    }
}