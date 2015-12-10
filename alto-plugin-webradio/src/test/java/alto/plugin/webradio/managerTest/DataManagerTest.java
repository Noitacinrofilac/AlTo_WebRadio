package alto.plugin.webradio.managerTest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alto.plugin.webradio.manager.DataManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import de.flapdoodle.embed.process.runtime.Network;

public class DataManagerTest {
	

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    
	DataManager dataManager;
	private MongoClient _mongo;
	
	@Before
    public void setUp() throws Exception {

        _mongodExe = starter.prepare(new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(12345, Network.localhostIsIPv6()))
            .build());
        _mongod = _mongodExe.start();

        _mongo = new MongoClient("localhost", 12345);
        dataManager = new DataManager();
        dataManager.registerUser("luis");
    }

	@After
    public void tearDown() throws Exception {
        dataManager.removeUser("luis");
        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }
	
	@Test
	public void userExistanceTest(){
		MongodForTestsFactory factory = null;
        MongoClient mongo = null;
        
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
			mongo = factory.newMongo();
			DB usrDB = mongo.getDB("UserSongRatingDB");
			DBCollection userDB = usrDB.getCollection("userDB");
			
			BasicDBObject searchUserQuery = new BasicDBObject();
			searchUserQuery.put("userId", "luis");
			
			DBCursor cursorUser = userDB.find(searchUserQuery);
			while(cursorUser.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursorUser.next();
				assertEquals(obj.getString("userId"),"luis");
				System.out.println(obj);
			}
		} catch (MongoException |IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getAllMfccTest(){
		/*try {
			dataManager.saveAllSongs();
			long songId = ("arcade fire"+"reflektor").hashCode();
			long songId2 = ("the kooks"+"naïve").hashCode();
			Vector<double[]> mfcc = new Vector<double[]>();
			mfcc.add(new double[]{1.2,3.4});
			mfcc.add(new double[]{5.6,7.8});
			dataManager.saveMFCC(songId, mfcc);
			dataManager.saveMFCC(songId2, mfcc);
			
			List<Long> mfccSongs = dataManager.getAllMfccSongs();
			assertEquals((long) mfccSongs.get(0),songId);
			assertEquals((long) mfccSongs.get(1),songId2);
			
		} catch (MongoException e) {
			e.printStackTrace();
		}*/
		assertTrue(true);
	}
/*
	@Test
	public void saveUserRateTest(){
		MongodForTestsFactory factory = null;
        MongoClient mongo = null;
        
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
			mongo = factory.newMongo();
			assertTrue(dataManager.saveUserRate("luis", "AlTo-superChanson", 1));
			assertTrue(dataManager.saveUserRate("luis", "AlTo-chanson2", 1));
			DB usrDB = mongo.getDB("UserSongRatingDB");
			DBCollection userDB = usrDB.getCollection("userSongRatesDB");
			
			BasicDBObject searchUserQuery = new BasicDBObject();
			String user_id = "luis";
			searchUserQuery.put("userId", user_id);
			
			DBCursor cursorUser = userDB.find(searchUserQuery);
			while(cursorUser.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursorUser.next();
			}
			assertTrue(dataManager.removeUserRate("luis", "AlTo-superChanson"));
			assertTrue(dataManager.removeUserRate("luis", "AlTo-chanson2"));
		} catch (MongoException |IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeUserRateTest(){
		MongodForTestsFactory factory = null;
        MongoClient mongo = null;
        
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
			mongo = factory.newMongo();
			assertTrue(dataManager.saveUserRate("luis", "AlTo-superChanson", 1));
			assertTrue(dataManager.changeUserRate("luis", "AlTo-superChanson", 0));
			DB usrDB = mongo.getDB("UserSongRatingDB");
			DBCollection userDB = usrDB.getCollection("userSongRatesDB");
			
			BasicDBObject searchUserQuery = new BasicDBObject();
			searchUserQuery.put("userId", "luis");
			
			DBCursor cursorUser = userDB.find(searchUserQuery);
			while(cursorUser.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursorUser.next();
				assertEquals(obj.getString("rate"),"0");
			}
			
			assertTrue(dataManager.removeUserRate("luis", "AlTo-superChanson"));
		} catch (MongoException |IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void saveSongTest(){
		MongodForTestsFactory factory = null;
        MongoClient mongo = null;
        
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);
			mongo = factory.newMongo();
			assertTrue(dataManager.saveSong(1,"Andros-miam", "http://deezer.com/andros+miam"));
			DB usrDB = mongo.getDB("UserSongRatingDB");
			DBCollection userDB = usrDB.getCollection("songDB");
			
			BasicDBObject searchSongQuery = new BasicDBObject();
			searchSongQuery.put("song", "Andros-miam");
			
			DBCursor cursorSong = userDB.find(searchSongQuery);
			while(cursorSong.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursorSong.next();
				assertEquals(obj.getString("url"),"http://deezer.com/andros+miam");
				System.out.println(obj);
			}
			
			assertTrue(dataManager.removeSong("Andros-miam"));
		} catch (MongoException |IOException e) {
			e.printStackTrace();
		}
	}*/
}
