
package Support_Classes;

import GUI.AutomaticDownloadClientFrame;
import GUI.DownloadClientFrame;
import GUI.LoginFrame;
import GUI.TorrentCenter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sony
 */
public class ContinuingOperations {
    
    
    
    // this class handles the continuing operations like polling 
    // for available downloads initiated via android devices, updating the 
    // text files which includes the list of TV series etc.
    
    //this method is used to update the list of automatic downloads
    //this method connects with the Media Comapanion Web server
    // and use its user information to update the files, which are required 
    // at runtime as well as during regular intervals
    
    public static void updateAutomaticDownloadFile(){
        
          try {
                
                FileWriter fwrite1 = null, fwrite2 = null;
                PrintWriter pwrite1= null, pwrite2= null;
                BufferedWriter bread1 = null, bread2 = null;
                String textFilePath1 = new File("src/File_Dependencies/automaticDownloadList.txt").getAbsolutePath();                
                File file1 = new File(textFilePath1);
                String textFilePath2 = new File("src/File_Dependencies/testwriter.txt").getAbsolutePath();
                File file2 = new File(textFilePath2);
                
                
                String username = LoginFrame.username;
                InputStream inStream;  
                DataInputStream dataStream;
                String uri = "", response="";
                String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/userpreferences/"+username;
                URL url = new URL(restURL);
                
                inStream = url.openStream(); 
                dataStream  = new DataInputStream(inStream);            
                
                response=dataStream.readLine();
                String[] array = response.replace("},{", ":").split("\",\"");
                
                
                fwrite1 = new FileWriter(file1.getAbsoluteFile(),true);
                pwrite1 = new PrintWriter(file1.getAbsoluteFile());
                bread1 = new BufferedWriter(fwrite1);
                
                fwrite2 = new FileWriter(file2.getAbsoluteFile(),true);
                pwrite2 = new PrintWriter(file2.getAbsoluteFile());
                bread2 = new BufferedWriter(fwrite2);
                
                pwrite1.write("");
                pwrite2.write("");
                
                for(int i=1;i<((array.length-1)/3)+1;i++){
                    bread2.write(array[3*i-2].substring(9));
                    bread2.write(System.lineSeparator()); 
                    if(array[3*i-1].substring(4).equals("auto")){
                        bread1.write(array[3*i-2].substring(9));
                        bread1.write(System.lineSeparator());
                    }    
                }
                
                bread1.close();
                bread2.close();
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    }
    
    public static void initiateAutomaticDownloads(){
        
        FileReader fread = null;
        BufferedReader bread;
        String extract,lastepisode,modifiedString;
        DownloadLinkFinder automatic = new DownloadLinkFinder();
        String downloadNames[],downloaLinks[], newlastepisode, tvshowname;
        
        try {
            fread = new FileReader("src\\File_Dependencies\\automaticDownloadList.txt");
            bread = new BufferedReader(fread);
                        
            while((extract=bread.readLine())!= null){  
               lastepisode = getLastEpisode(LoginFrame.username, extract);
               modifiedString=MediaCompanionSettings.toTitleCase(extract)+" "+DownloadLinkFinder.stringModifier(lastepisode);
               if(DownloadLinkFinder.getAvailability(modifiedString)){
                   automatic.setAvailabilityFalse();
                   if(!(TorrentCenter.checkCurrentRunning(extract))){
                       System.out.println("Downloading---------------->"+extract);   
                    TorrentCenter.addCurrentRunning(extract);
                    TorrentCenter.availableAutomatics.add(extract);
                     downloadNames = DownloadLinkFinder.getDownloadFilenames(modifiedString);
                     downloaLinks = DownloadLinkFinder.getDownloadLinks(modifiedString);
                     tvshowname = DownloadLinkFinder.getTVshowImage(extract);
                     automatic.clearArray();
                    newlastepisode=DownloadLinkFinder.episodeIncrementer(lastepisode);
                       System.out.println(downloadNames[0]+"    **   "+downloaLinks[0]);
                    AutomaticDownloadClientFrame clientFrame = new AutomaticDownloadClientFrame(extract,downloadNames[0], newlastepisode, downloaLinks[0], tvshowname);
                    clientFrame.setVisible(true);
                   }
               }
            }
           // TorrentCenter.availableAutomatics.clear();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fread.close();
            } catch (IOException ex) {
                Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static String getLastEpisode(String uname, String tvshow){
        try {
            String episode = null;
            tvshow=tvshow.replace(" ", "%20");
            
                    InputStream inStream;  
                    DataInputStream dataStream;
                    String uri = "", response="";
                    String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/lastepisode/"+uname+"/"+tvshow;
                    URL url = new URL(restURL);
                    
                    inStream = url.openStream(); 
                    dataStream  = new DataInputStream(inStream);            
                    
                    response=dataStream.readLine();
                    episode= response.replace("\"", "").replace("[", "").replace("]", "");
                    
            return episode;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
        
    }
    
    public static void downloadDumpFile(){
        long lastmodify;
        int difference;
        File file = new File(MediaCompanionSettings.DownloadPath+"hourlydump.txt");
        
        lastmodify = file.lastModified();
        Date modified = new Date(lastmodify);
        Date todayDate = new Date();
        System.out.println(modified+"   "+todayDate);
        
        difference= (int)(todayDate.getTime()-modified.getTime())/(1000*60);
        System.out.println("Time Gap------->"+ difference);
        if(difference>120){
            System.out.println("Should Download");
            if(file.exists()){
                file.delete();
            }
            try {
                URL url = new URL("https://kickass.to/hourlydump.txt.gz");
                     try (
                         InputStream is = new GZIPInputStream(url.openStream())) {
                         Files.copy(is, Paths.get(MediaCompanionSettings.DownloadPath+"hourlydump.txt"));
             } catch (IOException ex) {
                 Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
             }
            } catch (MalformedURLException ex) {
                Logger.getLogger(ContinuingOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{    
        System.out.println("Should Not Download");
        }
    }
    
    public static void remoteInitiations(){
        try {
                        
                    String username = LoginFrame.username;
                    InputStream inStream;  
                    DataInputStream dataStream;
                    String imageuri = "http://mediacompanion.net78.net/MediaCompanion/images/remotedownload.jpg";
                    String response="";
                    String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/remoteinitiation/"+username;
                    URL url = new URL(restURL);
                    
                    inStream = url.openStream(); 
                    dataStream  = new DataInputStream(inStream);            
                    
                    response=dataStream.readLine();
                    response="{ remote:"+response+"}";  
                    
                    JSONObject obj = new JSONObject(response);
                    
                    JSONArray arr = obj.getJSONArray("remote");
                    String indexes[] = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++)
                    {
                        indexes[i] = arr.getJSONObject(i).getString("selectedid");                        
                    }
                    
                    
                    for(int i=0;i<indexes.length;i++){
                        
                        if(!(TorrentCenter.checkCurrentRunningID(indexes[i]))){
                            TorrentCenter.addCurrentRunningID(indexes[i]);
                            String dataresponse="";
                            String rURL = "http://localhost/MediaCompanion/SLIM_API/index.php/downloadsbyid/"+indexes[i];
                            URL rurl = new URL(rURL);

                            inStream = rurl.openStream(); 
                            dataStream  = new DataInputStream(inStream);            

                            dataresponse=dataStream.readLine();
                            String dataarray[]=dataresponse.replace("\"", "").replace("[", "").replace("]", "").split(",");

                            DownloadClientFrame remotedownload = new DownloadClientFrame(dataarray[0], dataarray[1], imageuri);
                            remotedownload.setVisible(true);
                        
                        }
                          
                    }
                    
                    
                    
                    inStream.close();
                    dataStream.close();
                                     
                    
                    
        } catch (MalformedURLException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        remoteInitiations();
    }
    
}
