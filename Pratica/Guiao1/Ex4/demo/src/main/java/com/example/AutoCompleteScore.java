package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

public class AutoCompleteScore {

    public Jedis jedis;
    public static String USERS_NAMES_SCORE = "users_names_scores";

    public AutoCompleteScore() {
        this.jedis = new Jedis();
    }

    public void saveName(String name, Double score) {
        jedis.zadd(USERS_NAMES_SCORE, score, name);
    }

    public List<String> getAutoCompleteScore(String answer) {
        ScanParams scanParams = new ScanParams();
        scanParams.match(answer + "*");
        String cursor = ScanParams.SCAN_POINTER_START;
        Set<String> resultSet = new LinkedHashSet<>(); // Use a LinkedHashSet to avoid duplicated elements


        boolean aux = true;
        while (aux) {
            ScanResult<Tuple> scanResult = jedis.zscan(USERS_NAMES_SCORE, cursor, scanParams);
            List<Tuple> listRes = scanResult.getResult();

            for (Tuple tuple : listRes) {
                String name = tuple.getElement();
                resultSet.add(name);
            }

            cursor = scanResult.getCursor();

            if (cursor.equals("0"))
                aux = false;
        }

        // Sort the list 
        List<String> sortedResults = new ArrayList<>(resultSet);
        sortedResults.sort((name1, name2) -> {
            double score1 = jedis.zscore(USERS_NAMES_SCORE, name1);
            double score2 = jedis.zscore(USERS_NAMES_SCORE, name2);
            return Double.compare(score2, score1);
        });

        for (String name : sortedResults) {
                double score = jedis.zscore(USERS_NAMES_SCORE, name);
                System.out.println(name + " (Score: " + score + ")");
        }

        return sortedResults;
    }

    public static void main(String[] args) throws FileNotFoundException {
        AutoCompleteScore board = new AutoCompleteScore();

        File f = new File("../../Ex4/nomes-pt-2021.csv");
        Scanner sc = new Scanner(f);
        String value;

        while (sc.hasNext()) {
            value = sc.next();
            String[] name_score = value.split(";");
            board.saveName(name_score[0].toLowerCase(), Double.parseDouble(name_score[1]));
        }

        sc.close();

        Scanner sc1 = new Scanner(System.in);

        System.out.print("Search for ('Enter' for quit):");
        String answer = sc1.nextLine().toLowerCase();

        while (true) {
            board.getAutoCompleteScore(answer);
            System.out.print("Search for ('Enter' for quit):");
            answer = sc1.nextLine().toLowerCase();

            if (answer.equals(""))
                break;
        }

        sc1.close();
    }
}
