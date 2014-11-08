/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Support_Classes.ContinuingOperations;
import Support_Classes.MD5Hashing;
import Support_Classes.MediaCompanionSettings;
import Support_Classes.RandomPosterGenerater;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 *
 * @author Sony
 */
public class LoginFrame extends javax.swing.JFrame {

    /**
     * Creates new form LoginFrame
     */
    private static LoginFrame login = new LoginFrame( );
    public static String username;
    public static String profilename;
    public static String gender;
    private LoginCheck usercheck;
    
    private LoginFrame() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("FrameLogo.png")).getImage());
        invalidMessage.setVisible(false);
        LoadingPanel.setVisible(false);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.WHITE);
        MainPanel.setBackground(Color.WHITE);
        LoadingPanel.setBackground(Color.WHITE);
    }
    
    public static LoginFrame getInstance( ) {
      return login;
    }
    
    class LoginCheck extends SwingWorker<Void, Void>{

       public boolean pass= false;   
        @Override
       public Void doInBackground(){
           
       
       if("".equals(usernameField.getText()) && "".equals(passwordField.getText())){
           invalidMessage.setVisible(true);
           usernameField.setText("");
           passwordField.setText("");
       }
       else{
           try {
               URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/login");
               URLConnection conn = url.openConnection();
               conn.setDoOutput(true);
               OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

               writer.write("username="+usernameField.getText().trim()+"&password="+ MD5Hashing.md5Hash(passwordField.getText().trim()));
               writer.flush();
               String line;
               BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
               String[] infoArray = null;
               
               while ((line = reader.readLine()) != null){
                   infoArray = line.replace("\":\"", ":").split("\",\"");                   
                   if(line.contains(MD5Hashing.md5Hash(passwordField.getText().trim()))){
                       pass=true;
                       profilename=infoArray[3].substring(12);
                       username=infoArray[1].substring(9);
                       gender=infoArray[4].substring(7);
                       
                       RandomPosterGenerater.setRunningThread(runningThreadLabel, "Initialising Settings");
                       String setting[] = MediaCompanionSettings.getCompanionSettings();
                       MediaCompanionSettings.pollingInterval= Integer.parseInt(setting[0]);
                       MediaCompanionSettings.DownloadPath= setting[1];
                       MediaCompanionSettings.TorrentPath= setting[2];
                       
                       RandomPosterGenerater.setRunningThread(runningThreadLabel, "Downloading Torrent Dump File");
                       ContinuingOperations.downloadDumpFile();
                       ContinuingOperations.updateAutomaticDownloadFile();
                       RandomPosterGenerater.setRunningThread(runningThreadLabel, "Checking for automatic download availability");
                       break;
                   }   
                   
               }
               
               writer.close();
               reader.close();
                              
           } catch (MalformedURLException ex) {
               Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
           }          

        }
            return null;
        }
        
        @Override
        public void done(){
            if(pass){
                
                    TorrentCenter start = TorrentCenter.getInstance();
                    start.setVisible(true);
                    setVisible(false);
                    
                    Thread continuingOperation =new Thread(){
            
                        @Override
                        public void run(){
                            try {
                                Thread.sleep(10000);
                                while(true){    
                                    ContinuingOperations.updateAutomaticDownloadFile();
                                    ContinuingOperations.initiateAutomaticDownloads();
                                    ContinuingOperations.remoteInitiations();
                                    Thread.sleep(MediaCompanionSettings.pollingInterval*1000);
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }       
                    };   
                    continuingOperation.start();
            }
            else{
                    MainPanel.setVisible(true);
                    LoadingPanel.setVisible(false);
                    invalidMessage.setVisible(true);
                    usernameField.setText("");
                    passwordField.setText("");
           }  
                        
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        MainPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        invalidMessage = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        LoadingPanel = new javax.swing.JPanel();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel();
        runningThreadLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel3.setText("Password");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel2.setText("Username");

        invalidMessage.setForeground(new java.awt.Color(255, 0, 0));
        invalidMessage.setText("Invalid username or password. Try again");

        usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));

        passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));

        loginButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(loginButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(passwordField))))
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(invalidMessage)
                .addContainerGap())
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(23, 23, 23)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(cancelButton))
                .addGap(18, 18, 18)
                .addComponent(invalidMessage)
                .addContainerGap())
        );

        MainPanel.setBounds(0, 0, 230, 160);
        jLayeredPane1.add(MainPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        busyLabel.setPreferredSize(new java.awt.Dimension(50, 50));

        runningThreadLabel.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        runningThreadLabel.setText("Initialising Media Companion Settings");

        javax.swing.GroupLayout LoadingPanelLayout = new javax.swing.GroupLayout(LoadingPanel);
        LoadingPanel.setLayout(LoadingPanelLayout);
        LoadingPanelLayout.setHorizontalGroup(
            LoadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoadingPanelLayout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
            .addComponent(runningThreadLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        LoadingPanelLayout.setVerticalGroup(
            LoadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoadingPanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(runningThreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        LoadingPanel.setBounds(0, 0, 220, 160);
        jLayeredPane1.add(LoadingPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLayeredPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
       MainPanel.setVisible(false);
       busyLabel.setBusy(true);
       LoadingPanel.setVisible(true);
       usercheck= new LoginCheck();
       usercheck.execute();
    }//GEN-LAST:event_loginButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LoadingPanel;
    private javax.swing.JPanel MainPanel;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel invalidMessage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel runningThreadLabel;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
