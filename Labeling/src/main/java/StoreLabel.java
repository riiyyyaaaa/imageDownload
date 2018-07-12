import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

import com.flickr4java.flickr.photos.PhotoList;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 *
 * google cloud vision から取得した写真のタグをDBに追加
 *
 */
public class StoreLabel{

    private static final String APPLICATION_NAME = "StoreLabel";
    private static final int MAX_LABELS = 3;
    public static void main(String[] args) throws IOException, GeneralSecurityException{

    }

    public Vision getVision() throws IOException, GeneralSecurityException {
    GoogleCredential credential =
      GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
          .setApplicationName(APPLICATION_NAME)
          .build();
    }

    //cacheに渡すためのcountListを渡す
    public ArrayList<Long> getCountList(long count) throws IOException,GeneralSecurityException{
        StoreLabel sl = new StoreLabel();
        LabelApp app = new LabelApp(sl.getVision());
        OpenData od = new OpenData();
        //GetURI gu = new GetURI();

        ArrayList<Long> countList = new ArrayList<Long>();

        //gets image path
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache("cache01");

        //connect to mongo
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        //search data
        DB db = mongoClient.getDB("photodata01");
        DBCollection coll = db.getCollection("data02");

        //10回繰り返してリストを返す
        int x = 0;
        for(x=0;x<10;x++){

            String path = od.returnPath(count);
            Path imagePath = Paths.get(path);
            try{
                //show path and labels
                System.out.println("------path:" + path);
                List<EntityAnnotation> labels = app.labelImage(imagePath, MAX_LABELS);
                System.out.println("------label" + labels);
                for(EntityAnnotation label:labels){
                    System.out.printf(
                        "\t%s (score: %.3f)\n",
                        label.getDescription(),
                        label.getScore());
                }

                BasicDBObject newDocument = new BasicDBObject();
                BasicDBObject searchQuery = new BasicDBObject().append("count", count);

                //store labels
                int j=0;
                for(EntityAnnotation label : labels){
                    if(j==0){
                        newDocument.append("$set", new BasicDBObject().append("tag1", label.getDescription()));
                        coll.update(searchQuery, newDocument);

                        System.out.println(coll.count());
                    }else if(j==1){
                        newDocument.append("$set", new BasicDBObject().append("tag2", label.getDescription()));
                        coll.update(searchQuery, newDocument);

                        System.out.println(coll.count());
                    }else if(j==2){
                        newDocument.append("$set", new BasicDBObject().append("tag3", label.getDescription()));
                        coll.update(searchQuery, newDocument);

                        System.out.println(coll.count());

                    }
                j++;
            }
            count++;
            countList.add(count);

            }catch(FileNotFoundException e){
              //ファイルの取得に失敗したとき
                System.out.println(e);
                //deleate not found data (from DB)
                //photolistからURLの抽出、画像取得を試みる
                //成功→名前の付けなおし、失敗→DBからデータ削除

                BasicDBObject newDocument = new BasicDBObject();
                BasicDBObject searchQuery = new BasicDBObject().append("Path", count);
                //PhotoList photolist =
                //ArrayList<String> urilist = gu.run(photolist);
            }
            for(DBObject obj: coll.find()){
                System.out.println(obj.toString());
            }
        }

        mongoClient.close();
        //manager.shutdown();

        return countList;
    }





}
