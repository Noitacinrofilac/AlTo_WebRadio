package alto.plugin.webradio.webservice;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import alto.plugin.webradio.clustering.KMeanClustering;
import alto.plugin.webradio.clustering.SongSpace;
import alto.plugin.webradio.recommender.LastFMDataRecommender;
import alto.plugin.webradio.recommender.RecommenderAggregator;



@ApplicationPath("/")
@WebListener
public class Webradio extends ResourceConfig implements ServletContextListener {
	public Webradio(){
		packages("alto.plugin.webradio.webservice");
	}
	
	private void initRecommendingSystem(){
    	RecommenderAggregator.getInstance();
    	LastFMDataRecommender.getInstance();
    	SongSpace.getInstance();
    	KMeanClustering.getInstance().initialize();
    }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		initRecommendingSystem();
		//arg0.getServletContext().setAttribute("recommender", RecommenderAggregator.getInstance());
	}
	
}
