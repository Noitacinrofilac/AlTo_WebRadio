package alto.plugin.webradio.clustering;

import java.net.UnknownHostException;

import alto.plugin.webradio.manager.DataManager;

public class App {

	public static void main(String[] args) {
		
		DataManager data=null;
		try {
			data = new DataManager();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		KMeanClustering cluster = KMeanClustering.getInstance();
		SongAnalyser analyser = new SongAnalyser();

	}
}
