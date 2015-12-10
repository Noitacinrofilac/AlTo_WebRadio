package alto.plugin.webradio.manager;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;

import alto.plugin.webradio.entity.SongInfo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class DataManager {

	MongoClient mongo;
	
	public DataManager() throws UnknownHostException{
		mongo = new MongoClient( "localhost", 27017 );
	}
	
	public static long encryptId(String id){
		return Math.abs(id.hashCode());
	}
	
	public boolean registerUser(String userId){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userDB = usrDB.getCollection("userDB");
		
		BasicDBObject searchUserQuery = new BasicDBObject();
		searchUserQuery.put("userId", encryptId(userId));
		
		DBCursor cursorUser = userDB.find(searchUserQuery);
		if(cursorUser.size() != 0){
			return false;
		}
		
		BasicDBObject document = new BasicDBObject();
		document.put("userId", encryptId(userId));
		userDB.insert(document);
		
		return true;
	}
	
	public boolean removeUser(String userId){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userDB = usrDB.getCollection("userDB");
		
		BasicDBObject searchUserQuery = new BasicDBObject();
		searchUserQuery.put("userId", encryptId(userId));
		
		DBCursor cursorUser = userDB.find(searchUserQuery);
		if(cursorUser.size() == 0){
			return false;
		}
		
		userDB.remove(searchUserQuery);
		
		return true;
	}
	
	public boolean saveSong(long songId,String artist,String song,String songURL){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("artist", artist.toLowerCase());
		searchSongQuery.put("song", song.toLowerCase());
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		if(cursorSong.size() != 0){
			return false;
		}
		
		BasicDBObject document = new BasicDBObject();
		String songString=artist.toLowerCase()+song.toLowerCase();
		document.put("songId",encryptId(songString));
		document.put("artist", artist);
		document.put("song", song);
		document.put("url", songURL);
		songDB.insert(document);
		
		return true;
	}
	
	public void saveAllSongs(){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		try {
			BufferedReader sid = new BufferedReader(new FileReader("./data/SongId.data"));
			BufferedReader br = new BufferedReader(new FileReader("./data/dataLastFm.data"));
			HashMap<String,Long> idSongMap = new HashMap<String,Long>();
			HashMap<Long,Integer> idLikedSongMap = new HashMap<Long,Integer>();
			
			String line;
        	long idSong = 0;
        	long i = 0;
        	while((line = br.readLine()) != null) {
	        	String[] values = line.split("\\t-\t", -1);
	        	String song = values[1]+"£"+values[2];
	        	String songId = values[1].toLowerCase()+values[2].toLowerCase();
	        	if(!idSongMap.containsKey(song)){
	        		idSongMap.put(song,encryptId(songId));
	        		idLikedSongMap.put(encryptId(songId),1);
	        		idSong++;
	        	}else{
	        		long idFound = idSongMap.get(song);
	        		idLikedSongMap.put(idFound, idLikedSongMap.get(idFound)+1);
	        	}
	        	System.out.println(i++);
        	}
        	br.close();
        	
			int j = 0;
			String line2;
        	while((line2 = sid.readLine()) != null) {
	        	String[] values = line2.split("£", -1);
	        	if(j%1000==0)
	        		System.out.println(j);
	        	BasicDBObject document = new BasicDBObject();
	        	String songId = values[1].toLowerCase()+values[2].toLowerCase();
	    		document.put("songId", encryptId(songId));
	    		document.put("artist", values[1].toLowerCase());
	    		document.put("song", values[2].toLowerCase());
	    		document.put("url", "none");
	    		document.put("like", idLikedSongMap.get(Long.parseLong(values[0])));
	    		songDB.insert(document);
	        	j++;
        	}
        	sid.close();
        	System.out.println("Done :: "+j);
		} catch (MongoException |IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addLikeValueAllSongs(){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/user/Documents/AlTo/testLibWebRadio/quick/dataLastFm.data"));
			HashMap<String,Long> idSongMap = new HashMap<String,Long>();
			HashMap<Long,Integer> idLikedSongMap = new HashMap<Long,Integer>();
			
			String line;
        	long idSong = 0;
        	long i = 0;
        	while((line = br.readLine()) != null) {
	        	String[] values = line.split("\\t-\t", -1);
	        	String song = values[1]+"£"+values[2];
	        	if(!idSongMap.containsKey(song)){
	        		idSongMap.put(song,idSong);
	        		idLikedSongMap.put(idSong,1);
	        		idSong++;
	        	}else{
	        		long idFound = idSongMap.get(song);
	        		idLikedSongMap.put(idFound, idLikedSongMap.get(idFound)+1);
	        	}
	        	System.out.println(i++);
        	}
        	br.close();
        	
			for(long j =0;j<songDB.count();j++){
				BasicDBObject searchSongQuery = new BasicDBObject();
				searchSongQuery.put("songId", i);
				BasicDBObject nblike = new BasicDBObject();
				nblike.put("like", idLikedSongMap.get(j));
				BasicDBObject updateObj = new BasicDBObject();
				updateObj.put("$set", nblike);
				songDB.update(searchSongQuery, updateObj);	
				System.out.println(j);
			}
		} catch (MongoException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeSong(String artist,String song){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("artist", artist);
		searchSongQuery.put("song", song);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		
		if(cursorSong.size() ==0){
			return false;
		}
		
		songDB.remove(searchSongQuery);
		
		return true;
	}
	
	public boolean removeSong(long songId){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		
		if(cursorSong.size() ==0){
			return false;
		}
		
		songDB.remove(searchSongQuery);
		
		return true;
	}
	
	public boolean saveUserRate(String userId, String artist,String song,int rate, String songURL){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userSongRatesDB = usrDB.getCollection("userSongRatesDB");
		DBCollection userDB = usrDB.getCollection("userDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchUserQuery = new BasicDBObject();
		searchUserQuery.put("userId", encryptId(userId));
		
		long songId = encryptId((artist+song));
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		DBCursor cursorExistingSong = songDB.find(searchSongQuery);
		if(cursorExistingSong.size() == 0){
			if(songURL == null)
				songURL = "none";
			this.saveSong(songId, artist, song, songURL);
		}
		
		BasicDBObject searchUserSongQuery = new BasicDBObject();
		searchUserSongQuery.put("userId", encryptId(userId));
		searchUserSongQuery.append("artist", artist);
		searchUserSongQuery.append("song", song);
		
		DBCursor cursorUser = userDB.find(searchUserQuery);
		DBCursor cursorSong = userSongRatesDB.find(searchUserSongQuery);
		if(cursorUser.size() == 0){
			return false;
		}
		
		if(cursorSong.size() !=0){
			this.changeUserRate(userId, artist, song, rate);
		}
		
		long userIdLong = userId.hashCode();
		
		BasicDBObject document = new BasicDBObject();
		document.put("userId", userIdLong);
		document.put("songId", songId);
		document.put("rate", rate);
		userSongRatesDB.insert(document);
		
		return true;
	}
	
	public boolean changeUserRate(String userId,String artist, String song,int rate){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userSongRatesDB = usrDB.getCollection("userSongRatesDB");
		
		BasicDBObject searchUserSongQuery = new BasicDBObject();
		searchUserSongQuery.put("userId", encryptId(userId));
		searchUserSongQuery.append("artist", artist);
		searchUserSongQuery.append("song", song);
		
		DBCursor cursor = userSongRatesDB.find(searchUserSongQuery);
		if(cursor.size() == 0){
			return false;
		}
		
		BasicDBObject newRate = new BasicDBObject();
		newRate.put("rate", rate);
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newRate);
	 
		userSongRatesDB.update(searchUserSongQuery, updateObj);

		return true;
	}
	
	public boolean removeUserRate(String userId,String artist, String song){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection userSongRatesDB = usrDB.getCollection("userSongRatesDB");
		
		BasicDBObject searchUserSongQuery = new BasicDBObject();
		searchUserSongQuery.put("userId", encryptId(userId));
		searchUserSongQuery.append("artist", artist);
		searchUserSongQuery.append("song", song);
		
		DBCursor cursorSong = userSongRatesDB.find(searchUserSongQuery);
		
		if(cursorSong.size() ==0){
			return false;
		}
		
		userSongRatesDB.remove(searchUserSongQuery);
		
		return true;
	}
	
	public String[] getSongById(long songId){
		String[] res = new String[2];
		res[0] = "default";
		res[1] = "default";
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			
			res[0]=obj.getString("artist");
			res[1]=obj.getString("song");
		}
		
		return res;
	}
	
	public long getSongByName(String artist, String song){
		long id = -1;
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("artist", artist);
		searchSongQuery.put("song", song);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		if(!cursorSong.hasNext()){
			return this.getSongByNameSimilar(artist, song);
		}
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			id=obj.getLong("songId");
		}
		
		return id;
	}
	
	public SongInfo getSongInfo(long songId){
		SongInfo songInfo = new SongInfo();
		Vector<double[]> mfcc = new Vector<double[]>() ;
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			songInfo.setArtist(obj.getString("artist"));
			songInfo.setSongName(obj.getString("song"));
			songInfo.setSongID(songId);
			songInfo.setLikes(obj.getInt("like"));
			BasicDBList resDBList =  (BasicDBList) obj.get("mfcc");
			if (resDBList != null) {
				for (Object val : resDBList) {
					List<Object> tmp = (BasicDBList) val;
					double[] value = new double[tmp.size()];
					for (int i =0;i<tmp.size();i++) {
						value[i] = (double)tmp.get(i);
					}
					mfcc.add(value);
				}
			}
			songInfo.setMfcc(mfcc);
		}
		
		return songInfo;
	}
	
	public int getLikes(long songId){
		int res = 0;
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			res = obj.getInt("like");
		}
		
		return res;
	}
	
	public int getLikes(String artist,String song){
		int res = 0;
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");

		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("artist", artist);
		searchSongQuery.put("song", song);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			res = obj.getInt("like");
		}
		return res;
	}
	
	public boolean saveMFCC(String artist, String song, Vector<double[]> mfcc){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("artist", artist);
		searchSongQuery.put("song", song);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		if(cursorSong.size() == 0){
			return false;
		}
		
		BasicDBObject newMFCC = new BasicDBObject();
		newMFCC.put("mfcc", mfcc);
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newMFCC);
	 
		songDB.update(searchSongQuery, updateObj);
		return true;
	}
	
	public boolean saveMFCC(long songId, Vector<double[]> mfcc){
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		if(cursorSong.size() == 0){
			return false;
		}
		
		BasicDBObject newMFCC = new BasicDBObject();
		newMFCC.put("mfcc", mfcc);
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newMFCC);
	 
		songDB.update(searchSongQuery, updateObj);
		return true;
	}

	
	public Vector<double[]> getMFCC(long songId){
		Vector<double[]> res = new Vector<double[]>() ;
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		BasicDBObject searchSongQuery = new BasicDBObject();
		searchSongQuery.put("songId", songId);
		
		DBCursor cursorSong = songDB.find(searchSongQuery);
		while(cursorSong.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursorSong.next();
			BasicDBList resDBList =  (BasicDBList) obj.get("mfcc");
			if (resDBList != null) {
				for (Object val : resDBList) {
					List<Object> tmp = (BasicDBList) val;
					double[] value = new double[tmp.size()];
					for (int i =0;i<tmp.size();i++) {
						value[i] = (double)tmp.get(i);
					}
					res.add(value);
				}
			}
		}
		
		return res;
	}
	
	public List<Long> getAllMfccSongs(){
		List<Long> songsMfccList = new ArrayList<Long>();
		DB usrDB = mongo.getDB("UserSongRatingDB");
		DBCollection songDB = usrDB.getCollection("songDB");
		
		DBCursor cursor = songDB.find();
		while(cursor.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursor.next();
			BasicDBList resDBList =  (BasicDBList) obj.get("mfcc");
			if(resDBList != null){
				songsMfccList.add(obj.getLong("songId"));
			}
		}
		return songsMfccList;
	}
	
	public long getSongByNameSimilar(String artist, String song){
		DB db = mongo.getDB("UserSongRatingDB") ;
		DBCollection col = db.getCollection("songDB") ;
		String collection = col.toString();
		DBObject searchCmd = new BasicDBObject();
		searchCmd.put("text", collection); 
		searchCmd.put("search", song);

		CommandResult commandResult = db.command(searchCmd);

		col.ensureIndex(new BasicDBObject("song","text"));
		BasicDBList results = (BasicDBList)commandResult.get("results");

		for (Iterator<Object> it = results.iterator(); it.hasNext();) {
			BasicDBObject result = (BasicDBObject) it.next();
			BasicDBObject dbo = (BasicDBObject) result.get("obj");
			if(dbo.getString("artist").equalsIgnoreCase(artist))
				return dbo.getLong("songId");
		}
		
		return -1;
	}
	
}
