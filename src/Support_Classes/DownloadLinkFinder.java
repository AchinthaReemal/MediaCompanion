/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Support_Classes;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sony
 */
public class DownloadLinkFinder {
    
        FileReader Freader;
        BufferedReader Breader;
        String extract;
        static int arraySize;
        private static boolean available;
        static ArrayList<String[]> downloadList = new ArrayList<>();

    
    // This method is used to get the download file name of the desired 
    // TV series which would be used in the DownloadClientFrame
        
    public static String[] getDownloadFilenames(String searchString){
            try {
                FileReader Freader;
                BufferedReader Breader;
                String extract;
                int counter=0;
                int arraySizeCounter=0;
                String storageArray[]=null;
                String titleCaseString;
                

                     
             Freader = new FileReader(MediaCompanionSettings.DownloadPath+"hourlydump.txt");
             Breader = new BufferedReader(Freader);
             
             while((extract=Breader.readLine()) != null){             
                 if(extract.contains(searchString)){
                     downloadList.add(storageArray=extract.replace("|", "ffff").split("ffff"));
                 }             
             }
             arraySize=downloadList.size();
             
             String returnArray[] = new String[arraySize];
             
             if(arraySize>0){   
                for (Iterator<String[]> it = downloadList.iterator(); it.hasNext();) {
                    String[] count = it.next();
                    counter++;
                    returnArray[counter-1]= count[1];                
                }
             }
            return returnArray;
            
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
       
    }
    
    // This method is used to get the download links of the desired 
    // TV series which would be used in the DownloadClientFrame.
    // These would be obtained using the hourlydump file which is downloaded
    // at the begining and at regular intervals
    
    public static String[] getDownloadLinks(String searchString){
           
                int counter=0;                
            
            String returnArray[] = new String[arraySize];
             
            if(arraySize>0){
                for (Iterator<String[]> it = downloadList.iterator(); it.hasNext();) {
                    String[] count = it.next();
                    counter++;
                    returnArray[counter-1]= count[4];                
                }   
            }
            
            arraySize=0;
            return returnArray;           
            
            
    }
    
    // this method is used to get the TV series image
    // which is used in the DownloadClientFrame 
    
    public static String getTVshowImage(String tvshow){
            try {
                tvshow=tvshow.replace(" ", "%20");
                InputStream inStream;  
                DataInputStream dataStream;
                String uri = "", response="";
                String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/downloadsimage/"+tvshow;
                URL url = new URL(restURL);
                
                inStream = url.openStream(); 
                dataStream  = new DataInputStream(inStream);            
                response=dataStream.readLine();
                String[] array = response.replace("\":\"", ":").split("\",\"");
                
               if(response=="[]"){               
                   uri="";               
                   
               }else{
                  uri=array[5].substring(6).replace("\"}]", ""); 
               }
               
                return uri;
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
    }
    
    public void clearArray(){
        downloadList.clear();        
    }
    
    // this method is used to identify the availability 
    // of a certain TV show, which is retrieved from the
    //central server where the user infomation are stored
    
    public static boolean getAvailability(String searchString){
             
        
            try {
                FileReader Freader = new FileReader(MediaCompanionSettings.DownloadPath+"hourlydump.txt");
                BufferedReader Breader = new BufferedReader(Freader);
                String extract;
                
                    while((extract=Breader.readLine()) != null){             
                         if(extract.contains(searchString)){
                             available=true;
                         }             
                    }
                System.out.println(searchString+"------>"+available);
                return available;
            } catch (IOException ex) {
                Logger.getLogger(DownloadLinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
    }
    
    public void setAvailabilityFalse(){
        available=false;
    }
    //this method modifies the search string used in searching for 
    //TV seires. This modifies the string in to the "S01xE08" format
   
    public static String stringModifier(String initialString){
        
        String modifiedString="";
        String splitArray[] = initialString.split("x");
        
        if(splitArray[0].length()==1){
            splitArray[0]="S0"+splitArray[0];
        }
        else{
            splitArray[0]="S"+splitArray[0];
        }
        if(Integer.parseInt(splitArray[1])<10){
            splitArray[1]="E0"+(Integer.parseInt(splitArray[1])+1);
        }
        else{
            splitArray[1]="E"+(Integer.parseInt(splitArray[1])+1);
        }
            
        modifiedString=splitArray[0]+splitArray[1];
        return modifiedString;
    }
    
    //this method increments the serach string to poin to the
    // next episode of the TV series, for future download use
    
    public static String episodeIncrementer(String lastEpisode){
        String newEpisode="";
        
        String splitArray[] = lastEpisode.split("x");
        
        splitArray[1]=(Integer.parseInt(splitArray[1])+1)+" ";
        newEpisode=splitArray[0]+"x"+splitArray[1];
        
        return newEpisode;
    }
    
    
   
}
