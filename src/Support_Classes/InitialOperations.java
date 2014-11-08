/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Support_Classes;

import GUI.LoginFrame;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sony
 */
public class InitialOperations{
    
        
        public static ArrayList<String> checkManualDownloads(){
            FileReader Freader = null;
            String response;
            ArrayList<String> availableDownloads = new ArrayList<>();
            DownloadLinkFinder finder = new DownloadLinkFinder();
            
            try {
                String tvShowListPath= new File("src/File_Dependencies/testwriter.txt").getAbsolutePath();
                Freader = new FileReader(tvShowListPath);
                BufferedReader Breader = new BufferedReader(Freader);
                
                while((response=Breader.readLine())!= null){                    
                    if(DownloadLinkFinder.getAvailability(MediaCompanionSettings.toTitleCase(response.trim()))){
                        availableDownloads.add(response.trim());
                        finder.setAvailabilityFalse();
                    }                    
                }
                if(availableDownloads.isEmpty()){
                    return null;
                }else{
                    return availableDownloads;
                }
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InitialOperations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InitialOperations.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Freader.close();
                } catch (IOException ex) {
                    Logger.getLogger(InitialOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }    
        
        public void updateDownloadsDatabase(){
            
            int maximumLimit=5;
            Object tvShowsList[];
            String downloadNames[],downloadLinks[];
            DownloadLinkFinder finder = new DownloadLinkFinder();
            OutputStreamWriter writer = null;
            BufferedReader reader = null;
            
            tvShowsList=InitialOperations.checkManualDownloads().toArray();
            
            if(!(tvShowsList == null)){
                try {
                    InputStream inStream;  
                        DataInputStream dataStream;
                        String uri = "", response="";
                        String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/truncatetable";
                        URL truncation = new URL(restURL);
                        
                        inStream = truncation.openStream(); 
                        dataStream  = new DataInputStream(inStream);  
                        
                        inStream.close();
                        dataStream.close();
                    
                    for(int i=0;i<tvShowsList.length;i++){  
                        
                        downloadNames=DownloadLinkFinder.getDownloadFilenames(MediaCompanionSettings.toTitleCase(tvShowsList[i].toString()));
                        downloadLinks=DownloadLinkFinder.getDownloadLinks(MediaCompanionSettings.toTitleCase(tvShowsList[i].toString()));
                        finder.clearArray();
                        
                        if(downloadLinks.length>maximumLimit){
                            try {
                                    
                            for(int j=0;j<maximumLimit;j++){
                                URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/availabledownloads");
                                URLConnection conn = url.openConnection();
                                conn.setDoOutput(true);
                                writer = new OutputStreamWriter(conn.getOutputStream());
                                writer.write("username="+LoginFrame.username+"&tvshow="+downloadNames[j].replaceAll("[^A-Za-z0-9 ]", "")+"&downloadlink="+downloadLinks[j]);
                                writer.flush();
                                String line;
                                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                String[] infoArray = null;

                                
    //                            while ((line = reader.readLine()) != null){
    //                                System.out.println(line);              
    //                            }
                            }

                            writer.close();
                            reader.close();

                        } catch (MalformedURLException ex) {
                            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }  
                            
                        }
                        else{
                            try {
                             
                            //TorrentCenter center = TorrentCenter.getInstance();
                            for(int j=0;j<downloadLinks.length;j++){
                                URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/availabledownloads");
                                URLConnection conn = url.openConnection();
                                conn.setDoOutput(true);
                                writer = new OutputStreamWriter(conn.getOutputStream());
                                writer.write("username=achintha&tvshow="+downloadNames[j].replaceAll("[^A-Za-z0-9 ]", "")+"&downloadlink="+downloadLinks[j]);
                                writer.flush();
                                String line;
                                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                String[] infoArray = null;
                                                           
    //                            while ((line = reader.readLine()) != null){
    //                                System.out.println(line);              
    //                            }
                            }

                            writer.close();
                            reader.close();

                        } catch (MalformedURLException ex) {
                            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        }          

                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(InitialOperations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(InitialOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
        }
        
        public static String[] getUserHistory(){
            
            try {
            String history[];
            
                    String username = LoginFrame.username;
                    InputStream inStream;  
                    DataInputStream dataStream;
                    String uri = "", response="";
                    String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/userhistory/"+username;
                    URL url = new URL(restURL);
                    
                    inStream = url.openStream(); 
                    dataStream  = new DataInputStream(inStream);            
                    
                    response=dataStream.readLine();
                    
                    String[] array = response.replace("\"", "").replace("[", "").replace("]", "").split(",");
                    
                    
                    history=array;
                    
                    inStream.close();
                    dataStream.close();
                                     
                    return history;
        } catch (MalformedURLException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
            
            
        }
        
              
}
