package alto.plugin.webradio.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Centroid;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.neighborhood.BruteSearch;
import org.apache.mahout.math.neighborhood.UpdatableSearcher;
import org.apache.mahout.math.random.WeightedThing;


/* Songs histogram of cluster repartition 
 * are put in an updatable searcher in a Centroid(key = songId, Vector = histogram)
 * We can then fin which song is the closest to an other.
 */

public class SongSpace {
	
	private static SongSpace instance =null;
	
	private DistanceMeasure distanceMeasure;
	private UpdatableSearcher songFinder;
	
	private KMeanClustering cluster;
	
	private SongSpace(){
		distanceMeasure = new EuclideanDistanceMeasure();
		songFinder = new BruteSearch(distanceMeasure);
		cluster = KMeanClustering.getInstance();
		
	}
	
	// #Singleton
	public static SongSpace getInstance(){
		if(instance ==null){
			instance = new SongSpace();
			
			return instance;
		}
		return instance;
	}
	
	/* Adds a songs histogram to the searcher
	 * 
	 */
	public void addSong(int songId,Vector song){
		songFinder.add(new Centroid(songId, song));
	}
	
	public List<Integer> findSimilar(int songId,int nbSong){
		List<Integer> res = new ArrayList<Integer>();
		Centroid histo = cluster.getSongHistogram(songId);
		List<WeightedThing<org.apache.mahout.math.Vector>> result = songFinder.search(histo,nbSong);
    	for(WeightedThing<org.apache.mahout.math.Vector> vec: result){
    		res.add(((Centroid)vec.getValue()).getIndex());
    	}
		return res;
	}
	
	/* Resets the songFinder
	 * occurs when a new/ X new songs are added in the cluster 
	 */
	public void reset(){
		songFinder = new BruteSearch(distanceMeasure);
	}
}
