/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Support_Classes;

import GUI.LoginFrame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Sony
 */
    public class MediaCompanionSettings {
    
    public static String DefaultDownloadPath="C:\\Users\\Downloads\\"; 
    public static String DefaultTorrentPath="C:\\Users\\Torrents\\";
    public static int defaultPollingInterval=10;
    public static String DownloadPath;
    public static String TorrentPath;
    public static String BackgroundColor="WHITE";
    public static int pollingInterval;
    
    
    public static String[] getCompanionSettings(){
        try {
            String settings[];
            
                    String username = LoginFrame.username;
                    InputStream inStream;  
                    DataInputStream dataStream;
                    String uri = "", response="";
                    String restURL = "http://localhost/MediaCompanion/SLIM_API/index.php/usersettings/"+username;
                    URL url = new URL(restURL);
                    
                    inStream = url.openStream(); 
                    dataStream  = new DataInputStream(inStream);            
                    
                    response=dataStream.readLine();
                    
                    String[] array = response.replace("},{", ":").replace("[\"", "").replace("\"]", "").split("\",\"");
                    
                    array[1]=array[1].replace("\\ ", "\\");
                    array[2]=array[2].replace("\\ ", "\\");
                    
                    inStream.close();
                    dataStream.close();
                                     
                    settings=array;
                    
            return settings;
        } catch (MalformedURLException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void setAvatar(String gender, final JLabel avatar){
        try {
            String path1=new File("src/Images/Male.jpg").getAbsolutePath();
            String path2=new File("src/Images/Female.jpg").getAbsolutePath();
            BufferedImage img1 = ImageIO.read(new File(path1));
            BufferedImage img2 = ImageIO.read(new File(path2));
            ImageIcon icon1 = new ImageIcon((Image)img1);
            ImageIcon icon2 = new ImageIcon((Image)img2);
            
            if(gender.equals("male")){
                avatar.setIcon(icon1);
                avatar.repaint();
            }else{
                avatar.setIcon(icon2);
                avatar.repaint();
            }
        } catch (IOException ex) {
            Logger.getLogger(MediaCompanionSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    }
    
    
        public static void main(String[] args) {
            getCompanionSettings();
        }
        
        
   public static String toTitleCase(String input) {
       
        input=input.toLowerCase();
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }
        String replaced=titleCase.toString();

        if(replaced.contains("Of")){
             replaced = replaced.replace("Of", "of");
        }else if(replaced.contains(" A ")){
             replaced=replaced.replace(" A ", " a ");
        }else if(replaced.contains("Is")){
             replaced=replaced.replace("Is", "is");
        }

        return replaced;
    }
}
