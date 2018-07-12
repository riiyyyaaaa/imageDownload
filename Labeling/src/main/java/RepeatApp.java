import java.util.*;

import java.io.*;
import java.security.GeneralSecurityException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.DBCursor;

public class RepeatApp {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        RepeatApp app1App = new RepeatApp();
        app1App.app(args);
    }

    public void app(String args[]) throws GeneralSecurityException, IOException {

        long count = 0l;
        File file = new File("startPosition.txt");

        if (file.exists()) {
            // タグ付け開始位置を読み取り

            FileReader filereader = new FileReader(file);
            int ch;
            StringBuffer sb = new StringBuffer();

            ch = filereader.read();
            while (ch != -1) {
                sb.delete(0, sb.length());

                while (((char) ch != ',') && (ch != -1)) {
                    sb.append((char) ch);
                    ch = filereader.read();

                }
                ch = filereader.read();
                System.out.print("count: " + sb + "\n");

            }
            filereader.close();

            int i = 0;
            double num = 0.0;

            for (i = 0; i < sb.length(); i++) {
                num += (double) (sb.codePointAt(i) - 48) * Math.pow(10.0, (double) (sb.length() - 1 - i));
            }

            System.out.println("uni" + num);
            count = (long) num;

        } else {
            // make file
            File newfile = new File("startPosition.txt");

            // DBでタグ付けされた位置を検索

            MongoClient mongoClient = new MongoClient("localhost", 27017);

            // search data

            DB db = mongoClient.getDB("photodata01");
            DBCollection coll = db.getCollection("data02");

            BasicDBObject que = new BasicDBObject("tag1", new BasicDBObject("$ne", null));
            DBCursor cur = coll.find(que);

            try {

                while (cur.hasNext()) {
                    count = (long) cur.next().get("count");
                    System.out.println("count of exiting tag1: " + count);
                }

            } finally {
                cur.close();
            }
            mongoClient.close();
        }
        // タグ付けを開始 countListにタグ付け済のcount番号を挿入
        boolean ju = true;
        StoreLabel sl = new StoreLabel();
        ArrayList<Long> countList = new ArrayList();

        while (ju) {
            countList = sl.getCountList(count);

            // 以前のファイルを削除とともにタグ付けをファイルにストア
            FileWriter filewriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter pw = new PrintWriter(bw);

            int i = 0;
            System.out.println("make cahce...");
            System.out.println("List's size" + countList.size());

            for (i = 0; i < countList.size(); i++) {
                pw.print(countList.get(i) + ",");
            }

            pw.close();
            count += 10l;
        }
    }
}