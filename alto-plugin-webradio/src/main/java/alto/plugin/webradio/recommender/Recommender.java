package alto.plugin.webradio.recommender;

import java.util.List;

import alto.plugin.webradio.entity.SongInfo;

public interface Recommender {
	public List<SongInfo> recommend(int songId,int nbSongs);
}
