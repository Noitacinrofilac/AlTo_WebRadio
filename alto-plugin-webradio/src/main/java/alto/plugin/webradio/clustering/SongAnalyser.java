package alto.plugin.webradio.clustering;

import java.net.UnknownHostException;

import alto.plugin.webradio.manager.DataManager;

public class SongAnalyser {
	DataManager data;
	
	public SongAnalyser(){
			try {
				data = new DataManager();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void analyseSound(long id, String source){
		if(!isAlreadyAnalysed(id)){
			MFCCAnalyser analyser = new MFCCAnalyser(id, source);
			Thread analyse = new Thread(analyser);
			analyse.start();
		}
	}
	
	public boolean isAlreadyAnalysed(long id){
		if(data.getMFCC(id).isEmpty())
			return false;
		return true;
	}
}
