package alto.plugin.webradio.entity;

import java.util.Vector;

public class SongInfo {

	private long songId;
	private String songName;
	private String artist;
	private Vector<double[]> mfcc;
	private int likes;
	private String url;

	public SongInfo() {
		super();
		this.songId = -1;
		this.songName = "default";
		this.artist = "default";
		mfcc = null;
		likes = 0;
		url = "none";
	}
	
	public SongInfo(long songID, String artist, String songName) {
		super();
		this.songId = songID;
		this.songName = songName;
		this.setArtist(artist);
		mfcc = null;
		likes = 0;
		url = "none";
	}

	public long getSongID() {
		return songId;
	}

	public void setSongID(long songID) {
		this.songId = songID;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Vector<double[]> getMfcc() {
		return mfcc;
	}

	public void setMfcc(Vector<double[]> mfcc) {
		this.mfcc = mfcc;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
