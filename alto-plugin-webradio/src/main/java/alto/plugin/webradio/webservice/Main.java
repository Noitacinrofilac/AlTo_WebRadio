package alto.plugin.webradio.webservice;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import alto.plugin.webradio.clustering.KMeanClustering;
import alto.plugin.webradio.recommender.DataBasedRecommender;
import alto.plugin.webradio.recommender.LastFMDataRecommender;
import alto.plugin.webradio.recommender.RecommenderAggregator;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on

    //public static final String BASE_URI = "http://ec2-54-72-148-114.eu-west-1.compute.amazonaws.com:8080/webradio/";
    public static final String BASE_URI = "http://localhost:8080/webradio/";
    //Nounours : 192.168.43.22
    //Coloc : 192.168.1.25
    //Amazon : ec2-54-76-165-161.eu-west-1.compute.amazonaws.com
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("alto.plugin.webradio.webservice");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
    private static  void initRecommendingSystem(){
    	RecommenderAggregator.getInstance();
    	KMeanClustering.getInstance().initialize();
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	initRecommendingSystem();
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}

