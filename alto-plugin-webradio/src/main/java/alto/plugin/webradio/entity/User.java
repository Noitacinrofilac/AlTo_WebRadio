package alto.plugin.webradio.entity;

import java.util.HashMap;

public class User {

	private String userID;
	private HashMap<SongInfo,Integer> likedSongs;
	
	public User() {
		userID = "UndefinedUserId";
		likedSongs = new HashMap<SongInfo,Integer>();
	}

	public User(String userID, HashMap<SongInfo, Integer> likedSongs) {
		super();
		this.userID = userID;
		this.likedSongs = likedSongs;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public HashMap<SongInfo, Integer> getLikedSongs() {
		return likedSongs;
	}

	public void setLikedSongs(HashMap<SongInfo, Integer> likedSongs) {
		this.likedSongs = likedSongs;
	}
	
}