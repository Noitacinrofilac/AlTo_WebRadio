package alto.plugin.webradio.clustering;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import comirva.audio.util.AudioPreProcessor;
import comirva.audio.util.MFCC;
import alto.plugin.webradio.manager.DataManager;


/* Used to calculate MFCC of a song if not already done.
 * Improvement : run it on AWS
 */

public class MFCCAnalyser implements Runnable{

	private static final Logger log = LoggerFactory.getLogger(MFCCAnalyser.class);
	
	// Size of the windowFrames to sample the data of a song
	private final int windowFrame = (int)Math.pow(2,12);
	
	
	DataManager data;
	//URL url;
	long songId;
	File file;
	
	public MFCCAnalyser(long songId,String file){
		
		// Get a reference to a dataManager
		try {
			data = new DataManager();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.songId = songId;
		this.file = new File(file);
		/* When the source is an URL
		 * try{
			this.url= new URL(url);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}*/
	}
	
	@Override
	public void run() {
		try {
    		AudioInputStream is = AudioSystem.getAudioInputStream(file);
			AudioFormat base = is.getFormat();
			AudioFormat targetFormat = new AudioFormat(base.getSampleRate(), 16, base.getChannels(), true, false);
			AudioInputStream pcm = AudioSystem.getAudioInputStream(targetFormat, is);
			
			//PreProcessor for MFCC calculation
			AudioPreProcessor preProc = new AudioPreProcessor(pcm);
			String[] s = data.getSongById(songId);

			log.info("Started mfcc calculation for song "+s[0]+ " - "+s[1]);
			MFCC mfcc = new comirva.audio.util.MFCC(preProc.getSampleRate(),windowFrame,13,true);
			Vector<double[]> result =  mfcc.process(preProc);
			
			// Saves de MFCC vectors in the DB
			data.saveMFCC(songId, result);
			
			// Put the analysed data in the cluster
			KMeanClustering cluster = KMeanClustering.getInstance();
			cluster.clusterSong(result);
			cluster.updateBallKMean();
			
			log.info("Ended mfcc calculation for song "+s[0]+ " - "+s[1]);
			
			is.close();
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
