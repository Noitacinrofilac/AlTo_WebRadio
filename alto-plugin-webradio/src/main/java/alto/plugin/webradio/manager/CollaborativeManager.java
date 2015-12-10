package alto.plugin.webradio.manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class CollaborativeManager {

	MongoClient mongo;
	
	public CollaborativeManager() throws UnknownHostException{
        //mongo = new MongoClient( "ec2-54-76-175-105.eu-west-1.compute.amazonaws.com" , 27017 );
        mongo = new MongoClient( "localhost" , 27017 );
	}
	
	public void createDataModel(){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userSongRatesDB = usrDB.getCollection("userSongRatesDB");
		
		DBCursor cursor= userSongRatesDB.find();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("./data/AltoCollaborative.data"));
			while(cursor.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursor.next();
				String str = ""+obj.getLong("userId")+","+obj.getLong("songId")+","+obj.getLong("rate")+"\n";
				bw.write(str);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
