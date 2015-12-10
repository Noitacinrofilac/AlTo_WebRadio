package alto.plugin.webradio.webservice;

import java.net.UnknownHostException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import alto.plugin.webradio.entity.SongInfo;
import alto.plugin.webradio.manager.DataManager;
import alto.plugin.webradio.recommender.RecommenderAggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/{song}")
public class SongResource {

    private static final Logger log = LoggerFactory.getLogger(SongResource.class);

    @GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response similarSongsJson(@PathParam("song") String song){
		RecommenderAggregator recommender = RecommenderAggregator.getInstance();
		JSONArray response = new JSONArray();
		JSONObject jsonSong = new JSONObject();
		DataManager dm = null;
		try {
			dm = new DataManager();
		} catch (UnknownHostException e1) {
			log.info("Error with DM in songRessource");
			e1.printStackTrace();
		}
		String[] values = song.split("_", -1);
		String artist = values[0].toLowerCase();
		String title = values[1].toLowerCase();
		List<SongInfo> similarSongs = recommender.recommend((int)dm.getSongByName(artist, title),30);
		for(int i = 0; i < similarSongs.size();i++){
			int likes = similarSongs.get(i).getLikes();
			try {
				jsonSong = new JSONObject();
				jsonSong.put("artist", similarSongs.get(i).getArtist());
				jsonSong.put("title",  similarSongs.get(i).getSongName());
				jsonSong.put("likes",  likes);
				response.put(jsonSong);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.ok(response.toString())
                .header("Access-Control-Allow-Origin","http://localhost:63342")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .type(MediaType.APPLICATION_JSON)
				.build();
	}
}
