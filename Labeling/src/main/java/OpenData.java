import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;


/*
 *
 * DBから画像Pathを取得
 * 画像へのタグ付けがどこまで完了しているか（count）はcacheに残す予定
 * 
*/
public class OpenData{

    public static void main(String[] args){
    }

    public String returnPath(long count){
        //count: DBのどこからタグ付けを始めるか

        //DBへの接続
        MongoClient mongoClient;
        mongoClient = new MongoClient("localhost", 27017);

        System.out.println("----------------");
        //検索
        DB db = mongoClient.getDB("photodata01");
        DBCollection coll = db.getCollection("data02");
        BasicDBObject que = new BasicDBObject("count", count);
        DBCursor cur = coll.find(que);

        System.out.println("\n\ncur: " + cur.toString());
        System.out.println("\n\npath: "+ cur.getQuery());
        System.out.println("\n\nhasnext: " + cur.hasNext());


        String path = cur.next().get("Path").toString();
        //System.out.println("Path("+ count + "): " + path);

        mongoClient.close();

        return path;
    }
}