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

    public static void createLocIndex(MongoCollection<Document> col, String field) {
        String resultCreateIndex = col.createIndex(Indexes.ascending(field));
        System.out.println(String.format("Index created: %s", resultCreateIndex));
    }
}
