
package Support_Classes;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class RandomPosterGenerater {
    
    public static String[] bannerList = {"banner_1", "banner_2", "banner_3", "banner_4", "banner_5", "banner_6", "banner_7", "banner_8",
                                        "banner_9", "banner_10", "banner_11", "banner_12", "banner_13", "banner_14", "banner_15"};

    public static String[] posterList = {"poster_1", "poster_2", "poster_3", "poster_4", "poster_5", "poster_6", "poster_7", "poster_8",
                                        "poster_9", "poster_10", "poster_11", "poster_12", "poster_13", "poster_14", "poster_15", "poster_16"};
    
    static String path1;
    static String path2;
    static String path3;  
    static String uritest1;
    static String uritest2;
    static String uritest3;
    
    public static String posterURI(){
        String URI="";
        int randomValue=0;
        
        Random numberGenerator = new Random();
        randomValue=numberGenerator.nextInt(16);
        
        URI=posterList[randomValue]+".jpg";
        
        return URI;
    }
    
    public static String bannerURI(){
        String URI="";
        int randomValue=0;
        
        Random numberGenerator = new Random();
        randomValue=numberGenerator.nextInt(15);
        
        URI=bannerList[randomValue]+".jpg";
        
        return URI;
    }
     
     public static void setPosters(final JLabel poster1, final JLabel poster2, final JLabel poster3){
        
        Thread posterDisplay=new Thread(){
            
            @Override
            public void run(){
                
                int j=0;
                while(true){
                    try {
                        String path1,path2,path3;
                        path1=new File("src/Images/"+(uritest1=RandomPosterGenerater.bannerURI())).getAbsolutePath();
                        path2=new File("src/Images/"+(uritest2=RandomPosterGenerater.posterURI())).getAbsolutePath();
                        path3=new File("src/Images/"+(uritest3=RandomPosterGenerater.posterURI())).getAbsolutePath();
                        BufferedImage img1 = ImageIO.read(new File(path1));
                        BufferedImage img2 = ImageIO.read(new File(path2));
                        BufferedImage img3 = ImageIO.read(new File(path3));
                        ImageIcon icon1 = new ImageIcon((Image)img1);
                        ImageIcon icon2 = new ImageIcon((Image)img2);
                        ImageIcon icon3 = new ImageIcon((Image)img3);
                        poster1.setIcon(icon1);
                        poster1.repaint();
                        poster2.setIcon(icon2);
                        poster2.repaint();
                        poster3.setIcon(icon3);
                        poster3.repaint();
                        
                        Thread.sleep(5000);
                        
                        } catch (IOException ex) {
                            Logger.getLogger(RandomPosterGenerater.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println(path1+"  "+path2+"   "+path3);
                            System.out.println(uritest1+"   "+uritest2+"    "+uritest3);
                        } catch (InterruptedException ex) {                    
                        Logger.getLogger(RandomPosterGenerater.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                }            
            }       
        };        
        posterDisplay.start();
    }  
     
     public static void setRunningThread(final JLabel thread, String loadingthread){         
         thread.setText(loadingthread);
         thread.repaint();        
         
     }
     
}
