/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Support_Classes;

import GUI.DownloadClientFrame;
import GUI.LoginFrame;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.swing.SwingWorker;
import org.json.simple.JSONObject;

/**
 *
 * @author Sony
 */
public class AutomaticDownloadClient extends SwingWorker<Void, Void>{
    
    private static float completionLevel=0;
    private static int errorFlag=0;
    private String filename;
    private String downloadlink;
    private String tvshowimage;
    private String tvshow;
    private String lastepisode;
    Client client;
    
    public AutomaticDownloadClient(String tvshow, String filename, String lastepisode, String downloadlink, String tvshowimage) {
        
        this.filename=filename;
        this.downloadlink=downloadlink; 
        this.tvshowimage=tvshowimage;
        this.tvshow=tvshow;
        this.lastepisode=lastepisode;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        
        try {   
              
            URL url = new URL(downloadlink);
                try (
                    InputStream is = new GZIPInputStream(url.openStream())) {
                    Files.copy(is, Paths.get(MediaCompanionSettings.TorrentPath+filename+".torrent"));
                }
            Thread.sleep(10000);
                    
            client = new Client(
            // This is the interface the client will listen on (you might need something
            // else than localhost here).
            InetAddress.getLocalHost(),

            // Load the torrent from the torrent file and use the given
            // output directory. Partials downloads are automatically recovered.
            SharedTorrent.fromFile(
              new File(MediaCompanionSettings.TorrentPath+"\\"+filename+".torrent"),
              new File(MediaCompanionSettings.DownloadPath)));

          
            client.download();
            startDownload();
          // Downloading and seeding is done in background threads.
          // To wait for this process to finish, call:
        } catch (UnknownHostException ex) {
            Logger.getLogger(DownloadClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DownloadClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void done(){
        
        
    }
    //stop the download
    public void stopDownload(){
        client.stop();
    }
    // start the download
    public void startDownload(){        
        while(!(completionLevel==100)){
            try {
                completionLevel= client.completion();
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
    }
    
    public float getCompletionLevel(){
        completionLevel= client.completion();            
        return completionLevel;
    }
    
    // calls for client.share(...) with a seed time in seconds:
    // client.share(3600);
    // Which would seed the torrent for an hour after the download is complete.
    public void setSeedTime(int time){
        client.share(time);
    }
    
    public float getCompletion(){
        return completionLevel;
    }
    
    public String getImageURI(){
        return tvshowimage;
    }
    
    
    // You can optionally set download/upload rate limits
    // in kB/second. Setting a limit to 0.0 disables rate
    // limits.
    public void setDownloadSpeed(float drate){
        client.setMaxDownloadRate(drate);
    }
    
    public void setUploadSpeed(float urate){
        client.setMaxUploadRate(urate);
    }
    
    
    public String getFileName(){
        return filename;
    }
    
    public String getDownloadLink(){
        return downloadlink;
    }   
    public String getTVshow(){
        return tvshow;
    }
    public String getLastEpisode(){
        return lastepisode;
    }
}
