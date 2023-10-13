package ua.deti.cbd;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

public class App 
{
    public static void main( String[] args ) {
        String uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.0.1";

        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("cbd");
        MongoCollection<Document> collection = database.getCollection("restaurants");

        System.out.println("a) ---------------------------- ");

        // Test insert a document in the collection
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

        // addElement(collection, doc1);

        // Test update a element from a document
        System.out.println("Update a value of a element in the collection restaurants");
        updateElement(collection, "nome","Rest Coffee", "Rest Cafe");

        // Test search for documents
        System.out.println("Search for a element in the collection restaurants");

        doc1.append("nome", "Rest Cafe");
        Document doc2 = new Document().append("restaurant_id", "15950");

        searchElement(collection, doc1);
        searchElement(collection, doc2);

        System.out.println("b) ----------------------------");

        System.out.println("Test index (ascending) for localidade");
        Document doc3 = new Document("localidade", "Bronx");

        // Test search time without index for localidade
        long start_time1 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time1 = System.nanoTime();
        long total_time1 = end_time1 - start_time1;
        System.out.println("    Execution time (without index for localidade): " + total_time1);

        createIndex(collection, "localidade");

        // Test search time with index for localidade
        long start_time2 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time2 = System.nanoTime();
        long total_time2 = end_time2 - start_time2;
        System.out.println("    Execution time (with index for localidade): " + total_time2);


        System.out.println("Test index (ascending) for gastronomia");
        Document doc4 = new Document().append("gastronomia", "Chinese");

         // Test search time without index for gastronomia
        long start_time3 = System.nanoTime();
        searchElement(collection, doc4);
        long end_time3 = System.nanoTime();
        long total_time3 = end_time3 - start_time3;
        System.out.println("    Execution time (without index for gastronomia): " + total_time3 + "\n");

        createIndex(collection, "gastronomia");

        // Test search time with index for gastronomia
        long start_time4 = System.nanoTime();
        searchElement(collection, doc3);
        long end_time4 = System.nanoTime();
        long total_time4 = end_time4 - start_time4;
        System.out.println("    Execution time (with index for gastronomia): " + total_time4 + "\n");


        System.out.println("Test index (text) for nome");
        Document doc5 = new Document().append("nome", "The Movable Feast");

         // Test search time without index for nome
        long start_time5 = System.nanoTime();
        searchElement(collection, doc5);
        long end_time5 = System.nanoTime();
        long total_time5 = end_time5 - start_time5;
        System.out.println("    Execution time (without index for nome): " + total_time5 + "\n");

        createNomeIndex(collection);

        // Test search time with index for nome
        long start_time6 = System.nanoTime();
        searchElement(collection, doc5);
        long end_time6 = System.nanoTime();
        long total_time6 = end_time6 - start_time6;
        System.out.println("    Execution time (with index for nome): " + total_time6 + "\n");
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
}
