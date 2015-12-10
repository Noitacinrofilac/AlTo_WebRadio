package alto.plugin.webradio.recommender;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alto.plugin.webradio.clustering.SongSpace;
import alto.plugin.webradio.entity.SongInfo;
import alto.plugin.webradio.manager.DataManager;

public class DataBasedRecommender implements Recommender{
	
	private static final Logger log = LoggerFactory.getLogger(DataBasedRecommender.class);
	
	private static DataBasedRecommender instance =null;
	private SongSpace songSpace;
	private DataManager data;
	
	private DataBasedRecommender(){
		log.info("Starting up DataBasedRecommender");
		songSpace = SongSpace.getInstance();
		try {
			data = new DataManager();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DataBasedRecommender getInstance(){
		if(instance==null){
			instance = new DataBasedRecommender();
		}
		return instance;
	}
	
	public List<SongInfo> recommend(int songId,int nbSongs){
		List<SongInfo> result = new ArrayList<SongInfo>();
		for(int similarId : songSpace.findSimilar(songId, nbSongs)){
			result.add(data.getSongInfo(similarId));
		}
		return result;
	}
	
}
