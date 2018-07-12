package com.riya;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;


public class ConnectMongo {

	public static void main(String[] args) {
		MongoClient mongoClient;

		if(args.length == 0){
			mongoClient = new MongoClient();
		}else{
			mongoClient = new MongoClient(new MongoClientURI(args[0]));
		}
		MongoDatabase database = mongoClient.getDatabase("mydb");
		MongoCollection<Document> collection = database.getCollection("test");
		collection.drop();

		Document doc = new Document("name","MongoDB").append("type", "database").append("count", 1).append("info",new Document("x", 203).append("y",  102));

		collection.insertOne(doc);

		Document myDoc = collection.find().first();
		System.out.println(myDoc.toJson());

		List<Document> documents = new ArrayList<Document>();
		for(int i=0; i<100; i++){
			documents.add(new Document("i", i));
		}
		collection.insertMany(documents);
		System.out.println("total # of document after inserting 100 small ones (should be 101)" + collection.count());

		myDoc = collection.find().first();
		System.out.println(myDoc.toJson());

		MongoCursor<Document> cursor = collection.find().iterator();
		try{
			while(cursor.hasNext()){
				System.out.println(cursor.next().toJson());
			}
		}finally{
			cursor.close();
		}

		for(Document cur : collection.find()){
			System.out.println(cur.toJson());
		}

		myDoc = collection.find(eq("i",71)).first();//124
		System.out.println(myDoc.toJson());

		cursor = collection.find(gt("i",50)).iterator();

		try{
			while(cursor.hasNext()){
				System.out.println(cursor.next().toJson());
			}
		}finally{
			cursor.close();
		}
		cursor = collection.find(and(gt("i",50), lte("i",100))).iterator();

		try{
			while(cursor.hasNext()){
				System.out.println(cursor.next().toJson());
			}
		}finally{
			cursor.close();
		}

		 cursor = collection.find(and(gt("i", 50), lte("i", 100))).iterator();

	        try {
	            while (cursor.hasNext()) {
	                System.out.println(cursor.next().toJson());
	            }
	        } finally {
	            cursor.close();
	        }

	        // Query Filters
	        myDoc = collection.find(eq("i", 71)).first();
	        System.out.println(myDoc.toJson());

	        // now use a range query to get a larger subset
	        Block<Document> printBlock = new Block<Document>() {
	            public void apply(final Document document) {
	                System.out.println(document.toJson());
	            }
	        };
	        collection.find(gt("i", 50)).forEach(printBlock);

	        // filter where; 50 < i <= 100
	        collection.find(and(gt("i", 50), lte("i", 100))).forEach(printBlock);

	        // Sorting
	        myDoc = collection.find(exists("i")).sort(descending("i")).first();
	        System.out.println(myDoc.toJson());

	        // Projection
	        myDoc = collection.find().projection(excludeId()).first();
	        System.out.println(myDoc.toJson());

	        // Aggregation
	        collection.aggregate(asList(
	                match(gt("i", 0)),
	                project(Document.parse("{ITimes10: {$multiply: ['$i', 10]}}")))
	        ).forEach(printBlock);

	        myDoc = collection.aggregate(singletonList(group(null, sum("total", "$i")))).first();
	        System.out.println(myDoc.toJson());

	        // Update One
	        collection.updateOne(eq("i", 10), set("i", 110));

	        // Update Many
	        UpdateResult updateResult = collection.updateMany(lt("i", 100), inc("i", 100));
	        System.out.println(updateResult.getModifiedCount());

	        // Delete One
	        collection.deleteOne(eq("i", 110));

	        // Delete Many
	        DeleteResult deleteResult = collection.deleteMany(gte("i", 100));
	        System.out.println(deleteResult.getDeletedCount());

	        collection.drop();

	        // ordered bulk writes
	        List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
	        writes.add(new InsertOneModel<Document>(new Document("_id", 4)));
	        writes.add(new InsertOneModel<Document>(new Document("_id", 5)));
	        writes.add(new InsertOneModel<Document>(new Document("_id", 6)));
	        writes.add(new UpdateOneModel<Document>(new Document("_id", 1), new Document("$set", new Document("x", 2))));
	        writes.add(new DeleteOneModel<Document>(new Document("_id", 2)));
	        writes.add(new ReplaceOneModel<Document>(new Document("_id", 3), new Document("_id", 3).append("x", 4)));

	        collection.bulkWrite(writes);

	        collection.drop();

	        collection.bulkWrite(writes, new BulkWriteOptions().ordered(false));
	        //collection.find().forEach(printBlock);

	        // Clean up
	        database.drop();

	        // release resources
	        mongoClient.close();
	}

}
