package pt.ua.cbd.lab4.ex4;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import static org.neo4j.driver.Values.parameters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
    Entities:
        Game:
            - Rank
            - Name
            - Year
            - Genre
            - Global_Sales

        Publisher:
            - Name

        Platform:
            - Name

        Sales:
            - NA_Sales
            - EU_Sales
            - JP_Sales
            - Other_Sales
            

    Relationships:
        - (Game)-[:PUBLISHED_BY]->(Publisher)

        - (Game)-[:AVAILABLE_ON]->(Platform)

        - (Game)-[:HAS_SALES]->(Sales)
*/

public class Main implements AutoCloseable{

    private final Driver driver;

    public Main(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    
    }
    
    
    public void printGreeting(final String message) {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
                var result = tx.run(query);
                return result.single().get(0).asString();
            });
            System.out.println(greeting);
        }
    }

    private void loadData(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                System.out.println("LINE: " + line);
                String[] values = line.split(",");
                int rank = Integer.parseInt(values[0]);
                String name = values[1];
                String year = values[3];
                String genre = values[4];
                String publisher = values[5];
                String platform = values[2];
                float naSales = Float.parseFloat(values[6]);
                float euSales = Float.parseFloat(values[7]);
                float jpSales = Float.parseFloat(values[8]);
                float otherSales = Float.parseFloat(values[9]);
                float globalSales = Float.parseFloat(values[10]);

                try (Session session = driver.session()) {
                    session.executeWrite(
                        tx -> {
                            tx.run(
                                "CREATE (g:Game {rank: $rank, name: $name, year: $year, genre: $genre, global_sales:$globalSales})" + 
                                    "MERGE (plat:Platform {name: $platform})" +
                                    "MERGE (pub:Publisher {name: $publisher})" + 
                                    "MERGE (sales:Sales {NA_Sales: $naSales, EU_Sales: $euSales, JP_Sales: $jpSales, other_sales: $otherSales})" +
                                    "CREATE (g)-[:AVAILABLE_ON]->(plat)" + 
                                    "CREATE (g)-[:PUBLISHED_BY]->(pub)" +
                                    "CREATE (g)-[:HAS_SALES]->(sales)",
                                Values.parameters(
                                    "rank", rank,
                                    "name", name,
                                    "year", year,
                                    "genre", genre,
                                    "globalSales", globalSales,
                                    "publisher", publisher,
                                    "platform", platform,
                                    "naSales", naSales,
                                    "euSales", euSales,
                                    "jpSales", jpSales,
                                    "otherSales", otherSales

                                ));
                            return null;
                        }
                    );
                }
            }
        }
    }

    @Override
    public void close() throws RuntimeException {
        driver.close();
    }

    public static void main(String... args) throws IOException {
        try (var greeter = new Main("bolt://localhost:7687", "neo4j", "password")) {
            greeter.printGreeting("hello, world");

            String csvFile = "resources/vgsales.csv";

            greeter.loadData(csvFile);

            System.out.println("Data loaded successfully!");
        }
    }

}