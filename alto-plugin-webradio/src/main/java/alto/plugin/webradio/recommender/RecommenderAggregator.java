package alto.plugin.webradio.recommender;

import java.util.ArrayList;
import java.util.List;

import alto.plugin.webradio.entity.SongInfo;

public class RecommenderAggregator implements Recommender{

	private static RecommenderAggregator instance = null;
	
	private LastFMDataRecommender lastFMRecommender;
	private DataBasedRecommender dataRecommender;
	
	
	private RecommenderAggregator(){
		lastFMRecommender = LastFMDataRecommender.getInstance();
		dataRecommender = DataBasedRecommender.getInstance();
	}
	
	public static RecommenderAggregator getInstance(){
		if(instance==null){
			instance = new RecommenderAggregator();
		}
		return instance;
	}
	

	@Override
	public List<SongInfo> recommend(int songId, int nbSongs) {
		List<SongInfo> result = new ArrayList<SongInfo>();
		result.addAll(lastFMRecommender.recommend(songId, nbSongs));
		//result.addAll(dataRecommender.recommend(songId, nbSongs/2));
		return result;
	}
	
}
