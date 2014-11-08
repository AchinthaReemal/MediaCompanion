/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Support_Classes.AutomaticDownloadClient;
import Support_Classes.MediaCompanionSettings;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.json.simple.JSONObject;

/**
 *
 * @author Sony
 */
public class AutomaticDownloadClientFrame extends javax.swing.JFrame {

    DownloadClient dclient = new DownloadClient();
    AutomaticDownloadClient adclient;
    boolean stopped=false;
    float completion=0;
    String formedURI;
    int positionX,positionY;
    private float completionLevel=0;
    private int errorFlag=0;
    private String filename;
    private String downloadlink;
    private String tvshowimage;
    private String tvshow;
    private String lastepisode;
    Client client ;
    
    public AutomaticDownloadClientFrame(String tvshow, String filename, String lastepisode, String downloadlink, String tvshowimage) { 
        
        this.getContentPane().setBackground(Color.DARK_GRAY);
        
        this.filename=filename;
        this.downloadlink=downloadlink; 
        this.tvshowimage=tvshowimage;
        this.tvshow=tvshow;
        this.lastepisode=lastepisode;
        
        try {        
            initComponents();
            tvshowLabel.setText(filename);
            dclient.execute();            
            
            formedURI=tvshowimage.replace("\\", "");
            
            URL url = new URL(formedURI);
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", 
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uc.connect();
            InputStream istream = uc.getInputStream();
            BufferedImage image = ImageIO.read(istream);
            
             
            //Image image = ImageIO.read(url);
            Image newimg = image.getScaledInstance(160, 200,  java.awt.Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon((Image)newimg);
            imageLabel.setIcon(icon);
            imageLabel.repaint();
            
//            setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(DownloadClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
       
    public class DownloadClient extends SwingWorker<Void, Void>{
    
    
    
      
    @Override
    protected Void doInBackground() throws Exception {
        
        try {   
              
            URL url = new URL(downloadlink);
              
             if(!(new File(MediaCompanionSettings.TorrentPath+filename+".torrent").exists())){
                 
                    InputStream is = new GZIPInputStream(url.openStream());
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
            client.setCompletionLevel(completionLabel);
            setProgressBar(); 
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
        
         Thread checkCompletion =new Thread(){
            
            @Override
            public void run(){
                
                while(!(completionLevel==100)){
                    completionLevel=getCompletionLevel();
                } 
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
   
                String username = LoginFrame.username;
                String recentdownload = filename;
                String downloadtype = "Automatic";
                String downloaddate = dateFormat.format(date);
                
                 JSONObject obj=new JSONObject();
                 obj.put("username",username);
                 obj.put("recentdownload",recentdownload);
                 obj.put("downloadtype",downloadtype);
                 obj.put("date",downloaddate);
  
        try {
            URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/userhistory");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(obj.toJSONString());
            osw.flush();
            osw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(AutomaticDownloadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
                String tvshow = getTVshow();
                String lastepisode = getLastEpisode();
                
                 JSONObject obj2=new JSONObject();
                 obj.put("username",username);
                 obj.put("tvshow",tvshow);
                 obj.put("lastepisode",lastepisode);
  
        try {
            URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/updatelastepisode");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(obj.toJSONString());
            osw.flush();
            osw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(AutomaticDownloadClient.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
        
            }       
        };   
        checkCompletion.start();
        
    }
    //stop the download
       
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
    
}
    
     public void setProgressBar(){
        downloadProgress.setValue(0);
        
        Thread progressDisplay =new Thread(){
            
            @Override
            public void run(){
                while(!(completion==100)){
                    completion=client.completion();
                    downloadProgress.setValue((int) completion);
                }    
            }       
        };   
        progressDisplay.start();
    }
    
   public void startDownload(){
        client.share();
        stopped=false;
    }
    
    public void stopDownload(){
        client.stop();
        stopped=true;
    }
    
    public String getTVshow(){
        return tvshow;
    }
    public String getLastEpisode(){
        return lastepisode;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tvshowLabel = new javax.swing.JLabel();
        downloadProgress = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        completionLabel = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        imageLabel.setPreferredSize(new java.awt.Dimension(160, 200));

        jLabel7.setText("TV Show");

        tvshowLabel.setPreferredSize(new java.awt.Dimension(260, 14));

        downloadProgress.setPreferredSize(new java.awt.Dimension(260, 14));

        jLabel1.setText("Progress");

        jLabel3.setText("Completion");

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jMenuBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuBar1MousePressed(evt);
            }
        });
        jMenuBar1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jMenuBar1MouseDragged(evt);
            }
        });
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(tvshowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(downloadProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(completionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(30, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(tvshowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(downloadProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(completionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuBar1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MousePressed
        positionX=evt.getX();
        positionY=evt.getY();
    }//GEN-LAST:event_jMenuBar1MousePressed

    private void jMenuBar1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MouseDragged
        setLocation(getLocation().x+evt.getX()-positionX,getLocation().y+evt.getY()-positionY);
    }//GEN-LAST:event_jMenuBar1MouseDragged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setVisible(false);
        stopDownload();        
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(AutomaticDownloadClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(AutomaticDownloadClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(AutomaticDownloadClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(AutomaticDownloadClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new AutomaticDownloadClientFrame().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel completionLabel;
    private javax.swing.JProgressBar downloadProgress;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel tvshowLabel;
    // End of variables declaration//GEN-END:variables
}
