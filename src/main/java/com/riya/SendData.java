package com.riya;

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;

import org.bson.Document;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.photos.PhotoList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;

/*
 *
 * 現行
 * 取得し適切な型にしたデータをDBに保存する
 * URL, Geo, PhotoId, Accuracyを追加
 * 実行の際はmongod.exeを実行しておく
 * photoListからライセンス、元のURL、画像バッファなどがたどれる
 *
 */

public class SendData extends Thread{
		public void main(String[] args){

	}

	public static void run(Flickr flickr) throws Exception {

		//DBへの接続
		MongoClient mongoClient;
		mongoClient = new MongoClient("localhost",27017);

		//Dataname:mydb
		MongoDatabase database = mongoClient.getDatabase("photodata01");
		DB db = mongoClient.getDB("photodata01");
		//Collectionname:test
		MongoCollection<Document> collection = database.getCollection("data02");
		DBCollection coll = db.getCollection("data02");

		//collectionの中身を消去
		//collection.drop();

		//Flickrへの接続、データの取得、スレッド管理
		SearchData sd = new SearchData();
		GetURI gu = new GetURI();
		GetId gi = new GetId();
		GetGeo gg = new GetGeo();
		GetPhoto gp = new GetPhoto();
		
		PhotoList photolist = sd.searchPhoto(flickr);
		ArrayList<String> urilist = gu.run(photolist);
		ArrayList<String> idlist = gi.run(photolist);
		float[][] geodata = gg.run(photolist,flickr);
		ArrayList<String> photopath = gp.run(urilist,idlist);

		try{
			sd.join();
			System.out.println("finish: Search Photo");
			gu.join();
			System.out.println("finish: Get URI");
			gi.join();
			System.out.println("finish: Get Id");
			gg.join();
			System.out.println("finish: Get Geo information");
			gp.join();
			System.out.println("finish: Download Photo");
		}catch (InterruptedException e){
			System.out.println(e);
		}


		/*
		 * 50件ずつデータを加えていくとする時のデータの重複処理
		 */

		//コレクションの数
		long num = collection.count();

		Document box = collection.find(eq("Path",photopath.get(photolist.size()-1).toString())).first();
		
		if(box != null){
			System.out.println("Overlap");
			//重複該当箇所の表示
			System.out.println("box:" + box.toString());
			//コレクションの総数
			System.out.println("num" + num);

		}else{
			System.out.println("Ok");
		}

		BasicDBObject query  = new BasicDBObject();
		query.put("photolist",photolist.get(photolist.size()-1).toString());
		DBCursor cursor = coll.find(query);
		int countnum = 0;

		//コレクションの総数
		int allnum = (int) coll.count();
		int endline = urilist.size();

		if(cursor.hasNext()){

			BasicDBObject obj = (BasicDBObject) cursor.next();
			countnum = obj.getInt("count");
			//countの表示
			System.out.println("count: " + obj.getInt("count"));
			endline -= (allnum-countnum);

			if(endline<0){
				
				endline = 1;
			}
		}

		long count = 0;

		//insert data
		for(int i=0; i<endline; i++){

			if(i==0){
				count = collection.count();
			}else{
				count++;
			}

			coll.createIndex(new BasicDBObject("loc", "2dsphere"));
			BasicDBList coordinates = new BasicDBList();

			coordinates.put(0, geodata[i][0]);
			coordinates.put(1, geodata[i][1]);

			coll.insert(new BasicDBObject("count", count).append("Path", photopath.get(i)).append("photolist",photolist.get(i).toString()).append("loc", new BasicDBObject("type","Point").append("coordinates", coordinates)));

	}
		
		System.out.println("The number of documents: "+collection.count() + "\n");

		//全件表示
		/*
		MongoCursor<Document> cursor2 = collection.find().iterator();
		try{
			while (cursor2.hasNext()){
				System.out.println(cursor2.next().toJson());
			}
		}finally{
			cursor2.close();
		}
		*/
		box = collection.find(eq("Path",photopath.get(photolist.size()-1).toString())).first();
		System.out.println("Last doc:" + box.toString());
		
		

		//Find whats within 500m of the location
		/*
		BasicDBList loc1 = new BasicDBList();
		loc1.put(0, -73.98);F
		loc1.put(1, 40.758);
		DBObject myDoc = coll.findOne(
			new BasicDBObject("loc",
				new BasicDBObject("$near",
					new BasicDBObject("$geometry", 
						new BasicDBObject("type", "Point")
							.append("coordinates", loc1))
						.append("$maxDistance", 500)
					)
				)
			);
			
		System.out.println("----URL----\n"+myDoc.get("URL").toString());
		*/

        mongoClient.close();
	}

}
