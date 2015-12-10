package alto.plugin.webradio.recommender;


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alto.plugin.webradio.entity.SongInfo;
import alto.plugin.webradio.manager.DataManager;

public class LastFMDataRecommender implements Recommender{
	
	private static final Logger log = LoggerFactory.getLogger(LastFMDataRecommender.class); 
	
	//private final String fileName ="/home/ubuntu/private/alto-plugin-webradio/data/dataLastFmRemodeled.data";
    private final String fileName ="./data/dataLastFmRemodeled.data";
	private static LastFMDataRecommender instance = null;
	
	DataModel dataModel;
	
	GenericItemBasedRecommender recommender;
	
	private LastFMDataRecommender(){
		try {
			log.info("Starting up LastFMDataRecommender");
			log.info("** Loading DataModel");
			dataModel = new FileDataModel(new File(fileName));
			log.info("** Preparing ItemSimilarity");
			
			ItemSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
					//new CityBlockSimilarity(dataModel);
					//new LogLikelihoodSimilarity(dataModel);
					//new TanimotoCoefficientSimilarity(dataModel);
			log.info("** Creating Recommender");
			recommender = new GenericItemBasedRecommender(dataModel, similarity);
			log.info("LastFMDataRecommender initialized");
			
		} catch (IOException e) {
			log.info("Unable to load file : "+fileName);
		}
	}
	
	public static LastFMDataRecommender getInstance(){
		if(instance == null){
			instance = new LastFMDataRecommender();
		}
		return instance;
	}

	public List<SongInfo> recommend(int songId, int nbResult) {

		// List<String> result = new ArrayList<String>();
		// int songId = songNameId.get(songName);

		List<SongInfo> result = new ArrayList<SongInfo>();
		DataManager dm = null;
		try {
			dm = new DataManager();

			List<RecommendedItem> res = recommender.mostSimilarItems(songId,nbResult);
			for (RecommendedItem song : res) {
				SongInfo tmp = dm.getSongInfo(song.getItemID());
				result.add(tmp);
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (TasteException e) {
			// e.printStackTrace();
			System.out.println("Didn't find the song ");
			log.info(e.getMessage());
			result.add(new SongInfo());
		}
		return result;
	}
}
