package com.example.Ex3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class App 
{
    public static void main( String[] args ) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.0.1";

        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");

        System.out.println("a) ---------------------------- ");

        // ---------------------------------- Test insert a document in the collection ---------------------------------- 
        System.out.println("Insert a document to collection restaurants");
        Document doc1 = new Document()
                .append("address", new Document()
                        .append("building", "10039")
                        .append("coord", Arrays.asList(-74.23165, 46.34))
                        .append("rua", "Blv. of Broken Dreams")
                        .append("zipcode", "10937")
                )
                .append("localidade","Wisconsin")
                .append("gastronomia", "Minhota")
                .append("grades", Arrays.asList( 
                        new Document()
                            .append("date", new Document()
                                    .append("$date", "1299715200000")
                                )
                            .append("grade", "B")
                            .append("score", 25)
                        ,new Document()
                            .append("date", new Document()
                                    .append("$date", "1378857600000")
                            )
                            .append("grade", "A")
                            .append("score", 14)
                    )
                )
                .append("nome", "Rest Coffee")
                .append("restaurant_id", "15950");

        addElement(collection, doc1);

        // -----------------------------------------------------------------------------------------------------------

        // ---------------------------------- Test update a element from a document ----------------------------------

        System.out.println("Update a value of a element in the collection restaurants");
        updateElement(collection, "nome","Rest Coffee", "Rest Cafe");

        // -----------------------------------------------------------------------------------------------------------

        // ---------------------------------------- Test search for documents ----------------------------------------
        System.out.println("Search for a element in the collection restaurants");

        doc1.append("nome", "Rest Cafe");
        Document doc2 = new Document().append("restaurant_id", "15950");

        searchElement(collection, doc1);
        searchElement(collection, doc2);

        // -----------------------------------------------------------------------------------------------------------

        System.out.println("b) ----------------------------");

        System.out.println("Test index (ascending) for localidade");
        Document doc3 = new Document("localidade", "Bronx");

        // -------------------------------------------------- Test search time without index for localidade --------------------------------------------------
        long start_time1 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time1 = System.nanoTime();
        long total_time1 = end_time1 - start_time1;
        System.out.println("    Execution time (without index for localidade): " + total_time1);

        createIndex(collection, "localidade");

        // -------------------------------------------------- Test search time with index for localidade -------------------------------------------------- 
        long start_time2 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time2 = System.nanoTime();
        long total_time2 = end_time2 - start_time2;
        System.out.println("    Execution time (with index for localidade): " + total_time2);


        System.out.println("Test index (ascending) for gastronomia");
        Document doc4 = new Document().append("gastronomia", "Chinese");

        // -------------------------------------------------- Test search time without index for gastronomia --------------------------------------------------

        long start_time3 = System.nanoTime();
        searchElement(collection, doc4);
        long end_time3 = System.nanoTime();
        long total_time3 = end_time3 - start_time3;
        System.out.println("    Execution time (without index for gastronomia): " + total_time3 + "\n");

        // -------------------------------------------------- Test search time with index for gastronomia --------------------------------------------------

        createIndex(collection, "gastronomia");

        long start_time4 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time4 = System.nanoTime();
        long total_time4 = end_time4 - start_time4;
        System.out.println("    Execution time (with index for gastronomia): " + total_time4 + "\n");


        System.out.println("Test index (text) for nome");
        Document doc5 = new Document().append("nome", "The Movable Feast");

        // -------------------------------------------------------------------------------------------------------------------------------------------------


         //  -------------------------------------------------- Test search time without index for nome --------------------------------------------------
        long start_time5 = System.nanoTime();
        searchElement(collection, doc5);
        long end_time5 = System.nanoTime();
        long total_time5 = end_time5 - start_time5;
        System.out.println("    Execution time (without index for nome): " + total_time5 + "\n");

        // -------------------------------------------------- Test search time with index for nome --------------------------------------------------

        createNomeIndex(collection);

        long start_time6 = System.nanoTime();
        searchElement(collection, doc5);
        long end_time6 = System.nanoTime();
        long total_time6 = end_time6 - start_time6;
        System.out.println("    Execution time (with index for nome): " + total_time6 + "\n");

        // ------------------------------------------------------------------------------------------------------------------------------------------


        System.out.println("c) ----------------------------\n");


        // --------------------------------------------------------------------- 1st QUERY ---------------------------------------------------------------------

        System.out.println("Query 4 from exercise 2.2");
        System.out.println("Get the total number of restaurants located in Bronx.");
        Bson findQuery = eq("localidade","Bronx");
        long totalDocuments = collection.countDocuments(findQuery);
        System.out.println("    Number of restaurants located in Bronx: " + totalDocuments + "\n");

        // -----------------------------------------------------------------------------------------------------------------------------------------------------


        // --------------------------------------------------------------------- 2nd QUERY ---------------------------------------------------------------------

        System.out.println("Query 11 from exercise 2.2");
        System.out.println("11. Liste o nome, a localidade e a gastronomia dos restaurantes que pertencem ao Bronx e cuja gastronomia é do tipo 'American' ou 'Chinese'.");
        Document query = new Document().append("localidade", "Bronx").append("gastronomia", new Document("$in", Arrays.asList("American", "Chinese")));
        FindIterable<Document> iterable = collection.find(query);

        for(Document d : iterable) {
            System.out.println("    Nome: " + d.getString("nome"));
            System.out.println("    Localidade: " + d.getString("localidade"));
            System.out.println("    Gastronomia: " + d.getString("gastronomia") + "\n");
        }
        System.out.println("Total number of documents: " + collection.countDocuments(query));

        // -----------------------------------------------------------------------------------------------------------------------------------------------------

        

        // --------------------------------------------------------------------- 3rd QUERY ---------------------------------------------------------------------

        System.out.println("Query 12 from exercise 2.2");
        System.out.println("12. Liste o restaurant_id, o nome, a localidade e a gastronomia dos restaurantes localizados em 'Staten Island', 'Queens', ou 'Brooklyn'.");
        Document queryex12 = new Document("localidade", new Document("$in", Arrays.asList("Staten Island", "Queens", "Brooklyn")));
        FindIterable<Document> queryResult12 = collection.find(queryex12);

        for(Document document : queryResult12) {
            System.out.println("    Restaurant_id: " + document.getString("restaurant_id"));
            System.out.println("    Nome: " + document.getString("nome"));
            System.out.println("    Localidade: " + document.getString("localidade"));
            System.out.println("    gastronomia: " + document.getString("gastronomia") + "\n");
        }
        System.out.println("Total number of documents: " + collection.countDocuments(queryex12));

        // ------------------------------------------------------------------------------------------------------------------------------------------------------


        // --------------------------------------------------------------------- 4th QUERY ----------------------------------------------------------------------

        System.out.println("Query 13 from exercise 2.2");
        System.out.println("13. Liste o nome, a localidade, o score e gastronomia dos restaurantes que alcançaram sempre pontuações inferiores ou igual a 3.");
        Document queryEx13 = new Document("grades.score", new Document("$not", new Document("$gt", 3)));
        FindIterable<Document> queryResult13 = collection.find(queryEx13);

        for (Document document : queryResult13) {
            System.out.println("    Nome: " + document.getString("nome"));
            System.out.println("    Localidade: " + document.getString("localidade"));
            System.out.println("    Gastronomia: " + document.getString("gastronomia"));
        
            List<Document> grades =  document.get("grades", List.class);
            System.out.print("    Scores: ");
            for (Document grade : grades) {
                System.out.print(grade.getInteger("score") + " ");
            }
            System.out.println("\n");
        }
        
        System.out.println("Total number of documents: " + collection.countDocuments(queryEx13));

        // ------------------------------------------------------------------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------- 5th QUERY ----------------------------------------------------------------------

        System.out.println("Query 19 from exercise 2.2");
        System.out.println("19. Indique o número total de avaliações (numGrades) na coleção.");

        List<Bson> pipeline = Arrays.asList(
                project(fields(
                        computed("numGrades", new Document("$size", "$grades")),
                        include("nome", "localidade", "gastronomia")
                )),
                group(null, Accumulators.sum("totalGrades", "$numGrades")),
                project(fields(
                        excludeId(),
                        include("totalGrades")
                ))
        );

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document document : result)
            System.out.println("    Total de Grades: " + document.getInteger("totalGrades") + "\n");

        // ------------------------------------------------------------------------------------------------------------------------------------------------------


        System.out.println("d) ----------------------------\n");


        // ------------------------------------------------- Function countlocalidades -------------------------------------------------

        System.out.println("Test function countLocalidades");
        System.out.println("    Total number of localidades: " + countLocalidades(collection) + "\n");

        // -----------------------------------------------------------------------------------------------------------------------------


        // ------------------------------------------------- Function countRestByLocalidade -------------------------------------------------

        System.out.println("Test function countRestByLocalidade");

        Map<String, Integer> res = countRestByLocalidade(collection);
        System.out.println("Numero de restaurantes por localidade:");

        for(Map.Entry<String, Integer> entry : res.entrySet()) 
            System.out.println(" -> " + entry.getKey() + " - " + entry.getValue());

        System.out.println();

        // ----------------------------------------------------------------------------------------------------------------------------------


        // ------------------------------------------------- Function getRestWithNameCloserTo -------------------------------------------------

        System.out.println("Test function getRestWithNameCloserTo");
        String name = "Park";
        List<String> names = getRestWithNameCloserTo(collection, name);
        System.out.println("Nome de restaurantes contendo '" +  name + "' no nome: ");
        for(String n : names) 
            System.out.println(" -> " + n);

    }

    public static void addElement(MongoCollection<Document> col, Document doc) {
        col.insertOne(doc);
        System.out.println(doc.toJson() + "\nAdded document with success\n");
    }

    public static void updateElement(MongoCollection<Document> col, String field, String oldStr, String newStr) {
        Document query = new Document().append(field, oldStr);

        Bson updates = Updates.combine(Updates.set(field, newStr));

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            UpdateResult result = col.updateOne(query, updates, options);

            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId() + "\n");
        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }

    }

    public static void searchElement(MongoCollection<Document> col, Document doc) {
        FindIterable<Document> cursor = col.find(doc);
        System.out.println("Found the following docs for document " + doc.toJson() + ":\n");
        try (final MongoCursor<Document> cursorIterator = cursor.cursor()) {
            while (cursorIterator.hasNext()) 
                System.out.println(cursorIterator.next().toJson() + "\n");
        }
    }

    public static void createIndex(MongoCollection<Document> col, String field) {
        String resultCreateIndex = col.createIndex(Indexes.ascending(field));
        System.out.println(String.format("Index created: %s \n", resultCreateIndex));
    }

    public static void createNomeIndex(MongoCollection<Document> col) {
        String resultCreateIndex = col.createIndex(Indexes.text("nome"));
        System.out.println(String.format("Index created: %s \n", resultCreateIndex));
    }

    public static int countLocalidades(MongoCollection<Document> col) {
        AggregateIterable<Document> query = col.aggregate(
            Arrays.asList(
                group("$localidade"),
                group(null, Accumulators.sum("totalLocalidades", 1))
            )
        );

        // Get the result of the query
        Document result = query.first();

        int totalLocalidades = result.getInteger("totalLocalidades");

        return totalLocalidades;
    }

    public static Map<String, Integer> countRestByLocalidade(MongoCollection<Document> col) {
        HashMap<String, Integer> result = new HashMap<String,Integer>();

        AggregateIterable<Document> query = col.aggregate(
            Arrays.asList(
                group("$localidade", Accumulators.sum("totalRes", 1))
            )
        );

        for(Document d : query) {
            String localidade = d.getString("_id");
            Integer totalR = d.getInteger("totalRes");
            result.put(localidade, totalR);
        }

        return result;
    }

    public static List<String> getRestWithNameCloserTo(MongoCollection<Document> col, String name) {
        ArrayList<String> names = new ArrayList<String>();
        Document query = new Document("nome", new Document("$regex", ".*" + name + ".*"));
        FindIterable<Document> results = col.find(query);

        for(Document doc : results) 
            names.add(doc.getString("nome"));
        
        return names;
    }
}

