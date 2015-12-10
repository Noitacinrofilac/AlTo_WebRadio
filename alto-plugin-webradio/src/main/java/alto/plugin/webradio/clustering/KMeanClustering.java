package alto.plugin.webradio.clustering;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.apache.mahout.clustering.streaming.cluster.BallKMeans;
import org.apache.mahout.clustering.streaming.cluster.StreamingKMeans;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.WeightedEuclideanDistanceMeasure;
import org.apache.mahout.math.Centroid;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.neighborhood.BruteSearch;
import org.apache.mahout.math.neighborhood.UpdatableSearcher;
import org.apache.mahout.math.random.WeightedThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import alto.plugin.webradio.manager.DataManager;


public class KMeanClustering {
	
	private static final Logger log = LoggerFactory.getLogger(KMeanClustering.class);
	private final int INITIAL_NB_CLUSTERS = 15;
	private final int TARGET_STREAM_CLUSTERS= 500;

	private static KMeanClustering instance=null;
	
	private DistanceMeasure distanceMeasure;
	
	private UpdatableSearcher searcher;
	private StreamingKMeans stream;
	private BallKMeans cluster;
	
	private int numClusters;
	private int targetCluster;
	
	private int nbSongs;
	private int lastUpdate;
	
	private KMeanClustering(){
		/* Changeable params for algo adjustments */
		distanceMeasure = new WeightedEuclideanDistanceMeasure();
		searcher =  new BruteSearch(distanceMeasure);
		targetCluster =TARGET_STREAM_CLUSTERS;
		//initial value
		numClusters = INITIAL_NB_CLUSTERS;
		//////////////////////////////////////////////
		nbSongs=0;
		lastUpdate = 0;
		stream = new StreamingKMeans(new BruteSearch(distanceMeasure), targetCluster );
	}
	
	public static KMeanClustering getInstance(){
		if(instance == null){
			instance = new KMeanClustering();
		}
		return instance;
	}
	
	//Used to cluster one MFCC vector of a song
	public void clusterFeature(double[] mfcc){
		org.apache.mahout.math.Vector tmp = new DenseVector(mfcc);
		Centroid toCluster = new Centroid(0,tmp);
		synchronized(this){
			stream.cluster(toCluster);
		}
	}
	
	//Used to cluster a list of MFCC vectors of a song
	public void clusterSong(Vector<double[]> song){
		for(double[] mfcc : song){
			clusterFeature(mfcc);
		}
		stream.reindexCentroids();
		nbSongs++;
		evaluateNbClusters();
	}
	
	public void updateBallKMean(){
		if(nbSongs - lastUpdate >4){
			performBallKMean();
		}
	}
	
	private void performBallKMean(){
		searcher =  new BruteSearch(distanceMeasure);
		cluster = new BallKMeans(searcher, numClusters, 2);
		log.info("Starting BallKMean");
    	searcher = cluster.cluster(Lists.newArrayList(stream.iterator()));
    	//reset the SongSpace
		log.info("Refreshing the SongSpace.");
		SongSpace songSpace = SongSpace.getInstance();
		songSpace.reset();
		//get all the songs histograms
		/* Has to change, put all histograms in a tmp UpdatableSearcher
		 * and swap the searcher in SongSpace when done, run this job in an other thread 
		 * (Maybe even the whole BallKMean job aswell)
		 */
		DataManager data=null;
		try{
			data = new DataManager();
			for(long songId : data.getAllMfccSongs()){
				songSpace.addSong((int)songId,getSongHistogram((int)songId));
			}
			log.info("SongSpace and BallKMean refreshed.");
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
	}
	
	public UpdatableSearcher getSearcher(){
		return searcher;
	}
	
	private void evaluateNbClusters(){
		numClusters = (int)Math.round(Math.log((double)nbSongs))+INITIAL_NB_CLUSTERS;
	}
	
	
	public void initialize(){
		log.info("Initializing clusters");
		DataManager data=null;
		try {
			data = new DataManager();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// stream all the songs in the streamingcluster
		List<Long> songIds = data.getAllMfccSongs();
		if (songIds.size() > 1) {
			for (long songId : songIds) {
				clusterSong(data.getMFCC(songId));
			}
			performBallKMean();
			log.info("Initial Clustering done");
		} else {
			log.info("Not enaugh data to Cluster");
		}
	}
	
	public Centroid getSongHistogram(int songId){
		DataManager data=null;
		try {
			 data= new DataManager();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] histo = new double[numClusters];
		for(double[] feature : data.getMFCC(songId)){
			List<WeightedThing<org.apache.mahout.math.Vector>> res = searcher.search(new DenseVector(feature), 4);
        	for(int i= 0;i<res.size();i++){
        		WeightedThing<org.apache.mahout.math.Vector> vec = res.get(i);
        		int key=((Centroid)vec.getValue()).getIndex();
        		histo[key]+=1.0/(i+1);	
        	}
		}
		return new Centroid(songId,new DenseVector(histo));
	}
}
