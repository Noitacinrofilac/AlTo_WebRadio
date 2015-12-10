package alto.plugin.webradio.alto_plugin_webradio;

import java.util.List;

import alto.plugin.webradio.entity.SongInfo;
import alto.plugin.webradio.manager.DataManager;
import alto.plugin.webradio.recommender.LastFMDataRecommender;
import alto.plugin.webradio.recommender.RecommenderAggregator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        try {
        	/*MongodStarter starter = MongodStarter.getDefaultInstance();

            int port = 27017;
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

            MongodExecutable mongodExecutable = null;
            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();*/
			DataManager dm = new DataManager();
			/*BufferedReader br = new BufferedReader(new FileReader("/home/user/Documents/AlTo/testLibWebRadio/quick/dataLastFm.data"));
        	BufferedWriter bw = new BufferedWriter(new FileWriter("./data/dataLastFmRemodeled.data"));
        	BufferedWriter sid = new BufferedWriter(new FileWriter("./data/SongId.data"));
        	
        	HashMap<String,Long> idSongMap = new HashMap<String,Long>();
        	String line;
        	long i = 0;
        	while((line = br.readLine()) != null) {
	        	String[] values = line.split("\\t-\t", -1);
	        	String song = values[1].toLowerCase()+"£"+values[2].toLowerCase();
	        	long songIdString = DataManager.encryptId(values[1].toLowerCase()+values[2].toLowerCase());
	        	if(!idSongMap.containsKey(song)){
	        		idSongMap.put(song,songIdString);
	        		bw.write(values[0] + "," + songIdString + "," + "1" + "\n");
	        		sid.write(songIdString + "£" + song + "\n");
	        	}else{
	        		long idFound = idSongMap.get(song);
	        		bw.write(values[0] + "," + idFound + "," + "1" + "\n");
	        	}
	        	System.out.println(i++);
        	}

        	br.close();
        	bw.close();
        	sid.close();*/
			//dm.saveAllSongs();
			long songId = dm.getSongByName("the kooks","ooh la");
			System.out.println("the kooks - naïve : "+songId+" likes :"+dm.getLikes(songId));
			String arcadeF = "arcade fire"+"reflektor";
			String [] tabArt = dm.getSongById(arcadeF.hashCode());
			System.out.println(tabArt[0]+" - "+tabArt[1]);
			String queen = "queen"+"bohemian rhapsody";
			String [] tabArt2 = dm.getSongById(queen.hashCode());
			System.out.println(tabArt2[0]+" - "+tabArt2[1]);
			
			SongInfo inf = dm.getSongInfo(958220807);
			System.out.println(inf.getArtist()+" "+inf.getSongName()+ " "+inf.getLikes());
			RecommenderAggregator recommender = RecommenderAggregator.getInstance();
			LastFMDataRecommender lfm = LastFMDataRecommender.getInstance();
			List<SongInfo> list = recommender.recommend((int)dm.getSongByName("the strokes", "is this it"),30);
			for(SongInfo s : list){
				System.out.println(""+s.getArtist()+" "+s.getSongName());
			}
			

/*			
			MongoClient mongo = new MongoClient( "localhost" , 27017 );
			DB db = mongo.getDB("UserSongRatingDB") ;
			DBCollection col = db.getCollection("songDB") ;
			String collection = col.toString();
			DBObject searchCmd = new BasicDBObject();
			searchCmd.put("text", collection); 
			searchCmd.put("artist", "arcade fire"); 
>>>>>>> 8a019ab20b4d3617b7fd58e071b1efc4e430aa9f

			System.out.println(dm.getSongByNameSimilar("phoenix", "countdown (live at)"));
			
			/*
>>>>>>> 519eeb99284a74078a163e3e2338b0d165f02a88
			Vector<double[]> mfccKooks = new Vector<double[]>();
			mfccKooks.add(new double[]{1.2,3.4});
			mfccKooks.add(new double[]{5.6,7.8});
			boolean b = true ;//dm.saveMFCC(3516, mfccKooks);

			if(b){
				Vector<double[]> foundMfccKooks = dm.getMFCC(dm.getSongByName("the kooks", "ooh la"));
				System.out.println("blablaba 1");
				if(foundMfccKooks != null){
					System.out.println("Gooood");
					for(int i = 0;i< foundMfccKooks.size();i++){
						System.out.println(foundMfccKooks.get(i)+" hehe ");
					}
				}else{
					System.out.println("Nooooooo");
				}
			}else{
				System.out.println("blablaba 2");
			}
<<<<<<< HEAD
			
        } catch (UnknownHostException e) {
=======
			*/
        } catch (Exception e) {
			e.printStackTrace();
        }
    }
}
