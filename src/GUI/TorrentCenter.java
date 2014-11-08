/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Support_Classes.ContinuingOperations;
import Support_Classes.DownloadLinkFinder;
import Support_Classes.InitialOperations;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import Support_Classes.RandomPosterGenerater;
import Support_Classes.MediaCompanionSettings;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Sony
 */

public class TorrentCenter extends javax.swing.JFrame {

    /**
     * Creates new form TorrentCenter
     */
   
    private static TorrentCenter torrentcenter = new TorrentCenter( );
    private SearchMovies mSearch;
    boolean available=false;
    int positionX,positionY;
    private static String username;
    private static String profilename;
    public static ArrayList<String> availableAutomatics = new ArrayList<>();
    Object downloadList[];
    String[] downloadFilenames;
    String[] downloadLinks;
    ArrayList<String> retreive;
    public static ArrayList<String> currentlyRunning= new ArrayList<>();
    public static ArrayList<String> currentlyRunningIDs= new ArrayList<>();
    
    public TorrentCenter(){
        
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("FrameLogo.png")).getImage());
        menuBar.setOpaque(true);
        menuBar.setBackground(Color.WHITE);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.WHITE);
        Holder.setVisible(false);
        MovieDBPAne.setVisible(false);
        DownloadsPane.setVisible(false);
        HomePanel.setVisible(false);
        SettingsPanel.setVisible(false);
        MovieLoadingPane.setVisible(false);
        SearchField.setPrompt("  Search for a movie/TV show ");
        searchDefault.setOpaque(false);
        MovieDBPAne.setOpaque(false);
        DownloadsPane.setOpaque(false); 
        SettingsPanel.setOpaque(false);
        Holder.setOpaque(false);
        MovieLoadingPane.setOpaque(false);   
        HomePanel.setOpaque(false);
        menuBar.setBackground(Color.WHITE);
        
        MediaCompanionSettings.DownloadPath=MediaCompanionSettings.DefaultDownloadPath;
        MediaCompanionSettings.TorrentPath=MediaCompanionSettings.DefaultTorrentPath;
        
        fileDirectory.setText(MediaCompanionSettings.DefaultDownloadPath);
        torrentDirectory.setText(MediaCompanionSettings.DefaultTorrentPath);
        
        RandomPosterGenerater.setPosters(randomPoster2,randomPoster1,randomPoster3);
        
        retreive=InitialOperations.checkManualDownloads();
        if(retreive==null){
            downloadList=null;
        }else{
            downloadList=retreive.toArray();
        }
        
        availableListTextArea.append("\n");
        
              
        if(downloadList==null){
            availableListTextArea.append("No Downloads Available Today");
        }
        else{
            for(int i=0;i<downloadList.length;i++){
                availableListTextArea.append("\t"+downloadList[i].toString()+"\n");
            }
            
            InitialOperations starter= new InitialOperations();
            starter.updateDownloadsDatabase();
        }

        String history[]=InitialOperations.getUserHistory();
            seriesNameLabel.setText(history[1]);
            downloadTypeLabel.setText(history[2]);
            downloadDateLabel.setText(history[3]);
         
        twentyMin.setSelected(true);
         
         
    }   
    
    public static TorrentCenter getInstance( ) {
      return torrentcenter;
    }
    
    public void setBackgroundColor(String color){         
        if(color.equals("black")){
            TorrentCenter.getInstance().getContentPane().setBackground(Color.BLACK);
        }
        else if(color.equals("gray")){
            TorrentCenter.getInstance().getContentPane().setBackground(Color.GRAY);
        }
        else if(color.equals("darkgray")){
            TorrentCenter.getInstance().getContentPane().setBackground(Color.DARK_GRAY);
        }
        else if(color.equals("white")){
            TorrentCenter.getInstance().getContentPane().setBackground(Color.WHITE);
        }        
    }
    
    class SearchMovies extends SwingWorker<Void, Void>{

        @Override
        public Void doInBackground(){
            try {
            
            Holder.setVisible(false);
            String movieTitle;
            String apiURL = " http://www.imdbapi.com/";
            String totalURL;
            String response;
            String details[];
            InputStream inStream;  
            DataInputStream dataStream;
            
            //request, receive and process Movie details
            movieTitle= SearchField.getText().trim();
            movieTitle=movieTitle.replace(" ", "+");
            totalURL = apiURL+"?i=&t="+movieTitle;            
            URL url = new URL(totalURL);
            inStream = url.openStream(); 
            dataStream  = new DataInputStream(inStream);            
            response=dataStream.readLine();
            //end of processing
            
            //constructing information array
            String[] array = response.replace("\":\"", ":").split("\",\"");
            //end of construction

            //set image
            String imageURL=array[13].substring(7);
            BufferedImage img = ImageIO.read(new URL(imageURL));
            ImageIcon icon = new ImageIcon((Image)img);
            imageLabel.setIcon(icon);
            //end of image setting
            
            //label setting
            title.setText(array[0].substring(8));
            year.setText(array[1].substring(5,9));
            released.setText(array[3].substring(9));
            genre.setText(array[5].substring(6));
            runtime.setText(array[4].substring(8));
            director.setText(array[6].substring(9));
            if(array[8].substring(7).length()>60){
                actors.setText(array[8].substring(7).substring(0, 59)+"\n"+array[8].substring(7).substring(59));
            }else{
                actors.setText(array[8].substring(7));
            }
            actors.setOpaque(false);
            imdbrating.setText(array[15].substring(11));
            if(array[9].substring(5).length()>115){
                plot.setText(array[9].substring(5).substring(0, 59)+"\n"+array[9].substring(5).substring(59,118)+"\n"+array[9].substring(5).substring(118));
            }
            else{
                plot.setText(array[9].substring(5).substring(0, 59)+"\n"+array[9].substring(5).substring(59));
            }
            plot.setOpaque(false);
            Holder.setVisible(true);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
        }
        
        @Override
        public void done(){
            MovieLoadingPane.setVisible(false);
            Holder.setVisible(true);            
        }
    }
    
    
    public static boolean checkCurrentRunning(String tvshow){
        boolean status=false;
        
        for(int i=0;i<currentlyRunning.size();i++){
            if(currentlyRunning.get(i).equals(tvshow)){
                status=true;
                break;
            }
        }
        
        return status;
    }
    
    public static void addCurrentRunning(String tvshow){
        currentlyRunning.add(tvshow);     
        
    }
    
     public static boolean checkCurrentRunningID(String id){
        boolean status=false;
        
        for(int i=0;i<currentlyRunningIDs.size();i++){
            if(currentlyRunningIDs.get(i).equals(id)){
                status=true;
                break;
            }
        }
        
        return status;
    }
    
    public static void addCurrentRunningID(String id){
        currentlyRunningIDs.add(id);             
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        ButtonsPane = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        DownloadButton = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        runinBackground = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        LayeredPane = new javax.swing.JLayeredPane();
        searchDefault = new javax.swing.JPanel();
        randomPoster1 = new javax.swing.JLabel();
        randomPoster2 = new javax.swing.JLabel();
        randomPoster3 = new javax.swing.JLabel();
        randomPoster4 = new javax.swing.JLabel();
        MovieDBPAne = new javax.swing.JPanel();
        searchButton = new javax.swing.JButton();
        SearchField = new org.jdesktop.swingx.JXSearchField();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        Holder = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        genre = new javax.swing.JLabel();
        released = new javax.swing.JLabel();
        year = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        imdbrating = new javax.swing.JLabel();
        director = new javax.swing.JLabel();
        runtime = new javax.swing.JLabel();
        actors = new javax.swing.JTextArea();
        plot = new javax.swing.JTextArea();
        imageLabel = new javax.swing.JLabel();
        MovieLoadingPane = new javax.swing.JPanel();
        movieSearchBusy = new org.jdesktop.swingx.JXBusyLabel();
        DownloadsPane = new javax.swing.JPanel();
        AutomaticDownloadsPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        automaticTable = new javax.swing.JTable();
        ManualDownloadsPane = new javax.swing.JPanel();
        downloadSearchButton = new javax.swing.JButton();
        favoritesList = new javax.swing.JComboBox();
        MDResultsPane = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        downloadSelected = new javax.swing.JButton();
        HomePanel = new javax.swing.JPanel();
        welcomeLabel = new javax.swing.JLabel();
        Auto_manualPanel = new javax.swing.JLayeredPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        AutoActivityPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        automaticTextArea = new javax.swing.JTextArea();
        ManualAvailabilityPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        availableListTextArea = new javax.swing.JTextArea();
        UserHistoryPanel = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        seriesNameLabel = new javax.swing.JLabel();
        downloadDateLabel = new javax.swing.JLabel();
        userAvatar = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        downloadTypeLabel = new javax.swing.JLabel();
        logoLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        SettingsPanel = new javax.swing.JPanel();
        DownloadDirectories = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        fileDirectory = new javax.swing.JTextField();
        torrentDirectory = new javax.swing.JTextField();
        fileDirectoryChange = new javax.swing.JButton();
        torrentDirectoryChange = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        twentyMin = new javax.swing.JRadioButton();
        thirtyMin = new javax.swing.JRadioButton();
        fortyMin = new javax.swing.JRadioButton();
        oneHour = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        black = new javax.swing.JRadioButton();
        white = new javax.swing.JRadioButton();
        gray = new javax.swing.JRadioButton();
        darkgray = new javax.swing.JRadioButton();
        jButton9 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        setUndecorated(true);

        ButtonsPane.setBackground(new java.awt.Color(0, 0, 0));

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setText("TV/Movie DB");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Visit Media Companion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setText("Home");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        DownloadButton.setBackground(new java.awt.Color(0, 0, 0));
        DownloadButton.setText("Downloads");
        DownloadButton.setBorder(null);
        DownloadButton.setPreferredSize(new java.awt.Dimension(77, 23));
        DownloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DownloadButtonActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 0, 0));
        jButton7.setText("Settings");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        runinBackground.setBackground(new java.awt.Color(51, 51, 51));
        runinBackground.setText("Run in Background");
        runinBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runinBackgroundActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(51, 51, 51));
        jButton10.setText("Exit");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonsPaneLayout = new javax.swing.GroupLayout(ButtonsPane);
        ButtonsPane.setLayout(ButtonsPaneLayout);
        ButtonsPaneLayout.setHorizontalGroup(
            ButtonsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ButtonsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ButtonsPaneLayout.createSequentialGroup()
                        .addGroup(ButtonsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(runinBackground, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(DownloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ButtonsPaneLayout.setVerticalGroup(
            ButtonsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonsPaneLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(DownloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(runinBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        randomPoster1.setIcon(new javax.swing.ImageIcon("C:\\Users\\Sony\\Desktop\\SE Project\\Images\\Banners & Posters\\Resized Posters\\poster_5.jpg")); // NOI18N

        randomPoster2.setIcon(new javax.swing.ImageIcon("C:\\Users\\Sony\\Desktop\\SE Project\\Images\\Banners & Posters\\Resized Banners\\banner_8.jpg")); // NOI18N

        randomPoster3.setIcon(new javax.swing.ImageIcon("C:\\Users\\Sony\\Desktop\\SE Project\\Images\\Banners & Posters\\Resized Posters\\poster_15.jpg")); // NOI18N

        randomPoster4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/1.jpg"))); // NOI18N

        javax.swing.GroupLayout searchDefaultLayout = new javax.swing.GroupLayout(searchDefault);
        searchDefault.setLayout(searchDefaultLayout);
        searchDefaultLayout.setHorizontalGroup(
            searchDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchDefaultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(searchDefaultLayout.createSequentialGroup()
                        .addComponent(randomPoster1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(randomPoster3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(randomPoster4, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(randomPoster2, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        searchDefaultLayout.setVerticalGroup(
            searchDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchDefaultLayout.createSequentialGroup()
                .addComponent(randomPoster2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchDefaultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(randomPoster4, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(randomPoster3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(randomPoster1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        searchDefault.setBounds(0, 10, 850, 580);
        LayeredPane.add(searchDefault, javax.swing.JLayeredPane.DEFAULT_LAYER);

        searchButton.setBackground(new java.awt.Color(102, 102, 102));
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        SearchField.setBackground(new java.awt.Color(204, 204, 204));
        SearchField.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N

        Holder.setBackground(new java.awt.Color(153, 153, 153));
        Holder.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel2.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel2.setText("Title");

        jLabel3.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel3.setText("Genre");

        jLabel4.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel4.setText("Year");

        jLabel5.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel5.setText("Released");

        title.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        genre.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        released.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        year.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel6.setText("Run Time");

        jLabel7.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel7.setText("Director");

        jLabel8.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel8.setText("Actors");

        jLabel9.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel9.setText("Plot");

        jLabel10.setFont(new java.awt.Font("Microsoft YaHei", 1, 11)); // NOI18N
        jLabel10.setText("IMDB Rating");

        imdbrating.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        director.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        runtime.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N

        actors.setBackground(new java.awt.Color(153, 153, 153));
        actors.setColumns(20);
        actors.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        actors.setRows(5);

        plot.setBackground(new java.awt.Color(153, 153, 153));
        plot.setColumns(20);
        plot.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        plot.setRows(5);

        imageLabel.setText("                          Image");

        javax.swing.GroupLayout HolderLayout = new javax.swing.GroupLayout(Holder);
        Holder.setLayout(HolderLayout);
        HolderLayout.setHorizontalGroup(
            HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HolderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(plot))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imdbrating, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actors))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(director, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(runtime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(released, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(year, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(154, 154, 154))
        );
        HolderLayout.setVerticalGroup(
            HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HolderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(HolderLayout.createSequentialGroup()
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(29, 29, 29)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(genre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(29, 29, 29)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(released, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5))
                        .addGap(29, 29, 29)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(runtime, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(director, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(actors, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(imdbrating, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10))
                        .addGap(33, 33, 33)
                        .addGroup(HolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(plot, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        HolderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {director, genre, released, runtime, title});

        Holder.setBounds(10, 10, 810, 510);
        jLayeredPane1.add(Holder, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout MovieLoadingPaneLayout = new javax.swing.GroupLayout(MovieLoadingPane);
        MovieLoadingPane.setLayout(MovieLoadingPaneLayout);
        MovieLoadingPaneLayout.setHorizontalGroup(
            MovieLoadingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MovieLoadingPaneLayout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(movieSearchBusy, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(150, Short.MAX_VALUE))
        );
        MovieLoadingPaneLayout.setVerticalGroup(
            MovieLoadingPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MovieLoadingPaneLayout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addComponent(movieSearchBusy, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(163, Short.MAX_VALUE))
        );

        MovieLoadingPane.setBounds(233, 10, 330, 350);
        jLayeredPane1.add(MovieLoadingPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout MovieDBPAneLayout = new javax.swing.GroupLayout(MovieDBPAne);
        MovieDBPAne.setLayout(MovieDBPAneLayout);
        MovieDBPAneLayout.setHorizontalGroup(
            MovieDBPAneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MovieDBPAneLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 53, Short.MAX_VALUE))
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        MovieDBPAneLayout.setVerticalGroup(
            MovieDBPAneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MovieDBPAneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MovieDBPAneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SearchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                .addContainerGap())
        );

        MovieDBPAne.setBounds(0, 0, 830, 590);
        LayeredPane.add(MovieDBPAne, javax.swing.JLayeredPane.DEFAULT_LAYER);

        AutomaticDownloadsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Auto Download Configuration"));
        AutomaticDownloadsPane.setOpaque(false);

        automaticTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "TV Series", "Last Episode"
            }
        ));
        jScrollPane2.setViewportView(automaticTable);

        javax.swing.GroupLayout AutomaticDownloadsPaneLayout = new javax.swing.GroupLayout(AutomaticDownloadsPane);
        AutomaticDownloadsPane.setLayout(AutomaticDownloadsPaneLayout);
        AutomaticDownloadsPaneLayout.setHorizontalGroup(
            AutomaticDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AutomaticDownloadsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        AutomaticDownloadsPaneLayout.setVerticalGroup(
            AutomaticDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AutomaticDownloadsPaneLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ManualDownloadsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Manual Download Configuration"));
        ManualDownloadsPane.setOpaque(false);

        downloadSearchButton.setText("Search");
        downloadSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadSearchButtonActionPerformed(evt);
            }
        });

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Available Downloads", "Link"
            }
        ));
        jScrollPane1.setViewportView(resultsTable);

        downloadSelected.setText("Download");
        downloadSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MDResultsPaneLayout = new javax.swing.GroupLayout(MDResultsPane);
        MDResultsPane.setLayout(MDResultsPaneLayout);
        MDResultsPaneLayout.setHorizontalGroup(
            MDResultsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MDResultsPaneLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(MDResultsPaneLayout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(downloadSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MDResultsPaneLayout.setVerticalGroup(
            MDResultsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MDResultsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(downloadSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ManualDownloadsPaneLayout = new javax.swing.GroupLayout(ManualDownloadsPane);
        ManualDownloadsPane.setLayout(ManualDownloadsPaneLayout);
        ManualDownloadsPaneLayout.setHorizontalGroup(
            ManualDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ManualDownloadsPaneLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addGroup(ManualDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MDResultsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ManualDownloadsPaneLayout.createSequentialGroup()
                        .addComponent(favoritesList, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downloadSearchButton)))
                .addGap(29, 29, 29))
        );
        ManualDownloadsPaneLayout.setVerticalGroup(
            ManualDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManualDownloadsPaneLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(ManualDownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadSearchButton)
                    .addComponent(favoritesList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MDResultsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout DownloadsPaneLayout = new javax.swing.GroupLayout(DownloadsPane);
        DownloadsPane.setLayout(DownloadsPaneLayout);
        DownloadsPaneLayout.setHorizontalGroup(
            DownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DownloadsPaneLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(AutomaticDownloadsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ManualDownloadsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        DownloadsPaneLayout.setVerticalGroup(
            DownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DownloadsPaneLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(DownloadsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ManualDownloadsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AutomaticDownloadsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        DownloadsPane.setBounds(0, 0, 840, 590);
        LayeredPane.add(DownloadsPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        welcomeLabel.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N

        jTabbedPane1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo_Automaticl.png"))); // NOI18N

        automaticTextArea.setEditable(false);
        automaticTextArea.setColumns(20);
        automaticTextArea.setRows(5);
        automaticTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Automatic Downloads"));
        jScrollPane3.setViewportView(automaticTextArea);

        javax.swing.GroupLayout AutoActivityPanelLayout = new javax.swing.GroupLayout(AutoActivityPanel);
        AutoActivityPanel.setLayout(AutoActivityPanelLayout);
        AutoActivityPanelLayout.setHorizontalGroup(
            AutoActivityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AutoActivityPanelLayout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );
        AutoActivityPanelLayout.setVerticalGroup(
            AutoActivityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AutoActivityPanelLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(AutoActivityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        jTabbedPane1.addTab("Automatic Download Activity", AutoActivityPanel);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo_Manual.png"))); // NOI18N
        jLabel15.setText("   ");

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Downloads Available Today"));

        availableListTextArea.setEditable(false);
        availableListTextArea.setColumns(20);
        availableListTextArea.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        availableListTextArea.setRows(5);
        availableListTextArea.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane4.setViewportView(availableListTextArea);

        javax.swing.GroupLayout ManualAvailabilityPanelLayout = new javax.swing.GroupLayout(ManualAvailabilityPanel);
        ManualAvailabilityPanel.setLayout(ManualAvailabilityPanelLayout);
        ManualAvailabilityPanelLayout.setHorizontalGroup(
            ManualAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ManualAvailabilityPanelLayout.createSequentialGroup()
                .addContainerGap(108, Short.MAX_VALUE)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        ManualAvailabilityPanelLayout.setVerticalGroup(
            ManualAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManualAvailabilityPanelLayout.createSequentialGroup()
                .addGroup(ManualAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ManualAvailabilityPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel15))
                    .addGroup(ManualAvailabilityPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manual Download Availability", ManualAvailabilityPanel);

        jTabbedPane1.setBounds(5, 0, 810, 300);
        Auto_manualPanel.add(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        UserHistoryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "User History", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10)))); // NOI18N

        jLabel21.setText("Last Downloaded TV series:");

        jLabel23.setText("Last Download Date: ");

        jLabel31.setText("Download Type:");

        javax.swing.GroupLayout UserHistoryPanelLayout = new javax.swing.GroupLayout(UserHistoryPanel);
        UserHistoryPanel.setLayout(UserHistoryPanelLayout);
        UserHistoryPanelLayout.setHorizontalGroup(
            UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserHistoryPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(userAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserHistoryPanelLayout.createSequentialGroup()
                        .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE))
                    .addGroup(UserHistoryPanelLayout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(53, 53, 53)))
                .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seriesNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(downloadTypeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                        .addComponent(downloadDateLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        UserHistoryPanelLayout.setVerticalGroup(
            UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserHistoryPanelLayout.createSequentialGroup()
                .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserHistoryPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(userAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(UserHistoryPanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21)
                            .addComponent(seriesNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(downloadDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(UserHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(downloadTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Logo_small.png"))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        jLabel13.setText("Welcome To");

        javax.swing.GroupLayout HomePanelLayout = new javax.swing.GroupLayout(HomePanel);
        HomePanel.setLayout(HomePanelLayout);
        HomePanelLayout.setHorizontalGroup(
            HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomePanelLayout.createSequentialGroup()
                        .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47))
                    .addGroup(HomePanelLayout.createSequentialGroup()
                        .addComponent(Auto_manualPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 821, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HomePanelLayout.createSequentialGroup()
                        .addComponent(UserHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        HomePanelLayout.setVerticalGroup(
            HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(HomePanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel13)))
                .addGap(18, 18, 18)
                .addComponent(UserHistoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(35, 35, 35)
                .addComponent(Auto_manualPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        HomePanel.setBounds(0, 0, 850, 590);
        LayeredPane.add(HomePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        DownloadDirectories.setBorder(javax.swing.BorderFactory.createTitledBorder("Download Directories"));

        jLabel27.setText("File Download Directory");

        jLabel28.setText("Torrent File Directory");

        fileDirectoryChange.setText("Change Directory");
        fileDirectoryChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileDirectoryChangeActionPerformed(evt);
            }
        });

        torrentDirectoryChange.setText("Change Directory");
        torrentDirectoryChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                torrentDirectoryChangeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DownloadDirectoriesLayout = new javax.swing.GroupLayout(DownloadDirectories);
        DownloadDirectories.setLayout(DownloadDirectoriesLayout);
        DownloadDirectoriesLayout.setHorizontalGroup(
            DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DownloadDirectoriesLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fileDirectory)
                    .addComponent(torrentDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileDirectoryChange)
                    .addComponent(torrentDirectoryChange))
                .addGap(44, 44, 44))
        );
        DownloadDirectoriesLayout.setVerticalGroup(
            DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DownloadDirectoriesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(fileDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileDirectoryChange))
                .addGap(29, 29, 29)
                .addGroup(DownloadDirectoriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(torrentDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(torrentDirectoryChange))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Download Scheduling"));

        jLabel29.setText("Automatic Download Polling Interval");

        buttonGroup1.add(twentyMin);
        twentyMin.setText("20 min");

        buttonGroup1.add(thirtyMin);
        thirtyMin.setText("30 min");

        buttonGroup1.add(fortyMin);
        fortyMin.setText("40 min");

        buttonGroup1.add(oneHour);
        oneHour.setText("1 hr");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77)
                .addComponent(twentyMin)
                .addGap(18, 18, 18)
                .addComponent(thirtyMin)
                .addGap(32, 32, 32)
                .addComponent(fortyMin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(oneHour)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(twentyMin)
                    .addComponent(thirtyMin)
                    .addComponent(fortyMin)
                    .addComponent(oneHour))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Background Settings"));

        jLabel30.setText("Media Companion Background Color");

        buttonGroup2.add(black);
        black.setText("Black");

        buttonGroup2.add(white);
        white.setText("White");

        buttonGroup2.add(gray);
        gray.setText("Gray");

        buttonGroup2.add(darkgray);
        darkgray.setText("Dark Gray");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(black)
                .addGap(18, 18, 18)
                .addComponent(white)
                .addGap(18, 18, 18)
                .addComponent(gray)
                .addGap(18, 18, 18)
                .addComponent(darkgray)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(black)
                    .addComponent(white)
                    .addComponent(gray)
                    .addComponent(darkgray))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jButton9.setText("Save Changes");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SettingsPanelLayout = new javax.swing.GroupLayout(SettingsPanel);
        SettingsPanel.setLayout(SettingsPanelLayout);
        SettingsPanelLayout.setHorizontalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton9)
                    .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DownloadDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        SettingsPanelLayout.setVerticalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(DownloadDirectories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        SettingsPanel.setBounds(0, 0, 850, 590);
        LayeredPane.add(SettingsPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        menuBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                menuBarMousePressed(evt);
            }
        });
        menuBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                menuBarMouseDragged(evt);
            }
        });

        jMenu2.setBackground(new java.awt.Color(0, 0, 0));
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });
        menuBar.add(jMenu2);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ButtonsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 853, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
            .addComponent(ButtonsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DownloadsPane.setVisible(false);
        searchDefault.setVisible(false);
        MovieDBPAne.setVisible(true);
        HomePanel.setVisible(false);
        SettingsPanel.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        movieSearchBusy.setBusy(true);
        MovieLoadingPane.setVisible(true);
        mSearch = new SearchMovies();
        mSearch.execute();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void runinBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runinBackgroundActionPerformed
      
     TorrentCenter.getInstance().setVisible(false);
        
        if(!SystemTray.isSupported()){
        System.out.println("System tray is not supported !!! ");
        return ;
        }
    //get the systemTray of the system
    SystemTray systemTray = SystemTray.getSystemTray();

    //get default toolkit
    Image image = Toolkit.getDefaultToolkit().getImage("src/images/SystemTray.png");

    //popupmenu
    PopupMenu trayPopupMenu = new PopupMenu();

    //1t menuitem for popupmenu
    MenuItem action = new MenuItem("Open");
    action.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TorrentCenter.getInstance().setVisible(true);
        }
    });     
    trayPopupMenu.add(action);

    //2nd menuitem of popupmenu
    MenuItem close = new MenuItem("Exit");
    close.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(null,"Do You Really Want to Close Media Companion?", "Exiting Media Companion",  JOptionPane.YES_NO_OPTION);
                if(choice== JOptionPane.YES_OPTION){
                    System.exit(0);
                }            
        }
    });
    trayPopupMenu.add(close);

    //setting tray icon
    TrayIcon trayIcon = new TrayIcon(image, "Media Companion", trayPopupMenu);
    //adjust to default size as per system recommendation 
    trayIcon.setImageAutoSize(true);

    try{
        systemTray.add(trayIcon);
    }catch(AWTException awtException){
        awtException.printStackTrace();
    }
    }//GEN-LAST:event_runinBackgroundActionPerformed

    private void menuBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBarMousePressed
        positionX=evt.getX();
        positionY=evt.getY();
    }//GEN-LAST:event_menuBarMousePressed

    private void menuBarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBarMouseDragged
        setLocation(getLocation().x+evt.getX()-positionX,getLocation().y+evt.getY()-positionY);
    }//GEN-LAST:event_menuBarMouseDragged

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void DownloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DownloadButtonActionPerformed
        FileReader Freader = null;
        try {
            DownloadsPane.setVisible(true);
            HomePanel.setVisible(false);
            searchDefault.setVisible(false);
            MovieDBPAne.setVisible(false);
            MDResultsPane.setVisible(false);

            SettingsPanel.setVisible(false);
            if(downloadList==null){
                downloadSearchButton.setEnabled(false);
                favoritesList.addItem("No Downloads Found");
            }
            else{
                for(int i=0;i<downloadList.length;i++){
                    favoritesList.addItem(downloadList[i]);
                }
            }    
            DefaultTableModel table = (DefaultTableModel) automaticTable.getModel();
            int rowCount=table.getRowCount();
        
            if(rowCount>0){
                for(int i = rowCount - 1; i >= 0; i--){
                    table.removeRow(i);
                }
            }
            
            String response;
            String tvShowListPath= new File("src/File_Dependencies/automaticDownloadList.txt").getAbsolutePath();
            Freader = new FileReader(tvShowListPath);
            BufferedReader Breader = new BufferedReader(Freader);
            
                while((response=Breader.readLine())!= null){                    
                    Object[] ob = {response,ContinuingOperations.getLastEpisode(LoginFrame.username, response)};
                    table.addRow(ob);   
                }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Freader.close();
            } catch (IOException ex) {
                Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
        
         
    }//GEN-LAST:event_DownloadButtonActionPerformed

    private void downloadSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadSearchButtonActionPerformed
        DownloadLinkFinder finder = new DownloadLinkFinder();
        
        String searchString = favoritesList.getSelectedItem().toString();
        searchString=MediaCompanionSettings.toTitleCase(searchString)+" ";
        downloadFilenames = DownloadLinkFinder.getDownloadFilenames(searchString);
        downloadLinks = DownloadLinkFinder.getDownloadLinks(searchString);
        finder.clearArray();
        
        DefaultTableModel table = (DefaultTableModel) resultsTable.getModel();
        
        int rowCount=table.getRowCount();
        
        if(rowCount>0){
            for(int i = rowCount - 1; i >= 0; i--){
                table.removeRow(i);
            }
        } 
        
        if(downloadFilenames.length>0){
            for(int i=0;i<downloadFilenames.length;i++){
                Object[] ob = {downloadFilenames[i],downloadLinks[i]};
                table.addRow(ob);
            }
        }
        downloadFilenames=null;
        downloadLinks=null;
        
        MDResultsPane.setVisible(true);
        
        downloadSelected.setEnabled(false);

        ListSelectionModel listSelectionModel = resultsTable.getSelectionModel();
        
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) { 
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    downloadSelected.setEnabled(!lsm.isSelectionEmpty());
                }    
        });        
    }//GEN-LAST:event_downloadSearchButtonActionPerformed

    private void downloadSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadSelectedActionPerformed
       String downloadName=resultsTable.getValueAt(resultsTable.getSelectedRow(),0).toString();
       String downloadLink=resultsTable.getValueAt(resultsTable.getSelectedRow(),1).toString();
       String searchItem=favoritesList.getSelectedItem().toString();
       String tvshowname= DownloadLinkFinder.getTVshowImage(searchItem);
       
       DownloadClientFrame clientFrame = new DownloadClientFrame(downloadName, downloadLink, tvshowname);
       clientFrame.setVisible(true);
       
    }//GEN-LAST:event_downloadSelectedActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        HomePanel.setVisible(true);
        welcomeLabel.setText("WELCOME"+" "+LoginFrame.profilename.toUpperCase());
        DownloadsPane.setVisible(false);
        searchDefault.setVisible(false);
        MovieDBPAne.setVisible(false);
        SettingsPanel.setVisible(false);
        
        MediaCompanionSettings.setAvatar(LoginFrame.gender, userAvatar);
        
        automaticTextArea.append("\n");
        if(!(availableAutomatics==null)){
            for(int i=0;i<availableAutomatics.size();i++){
                automaticTextArea.append("\t"+availableAutomatics.get(i)+"\n");
            }
        }
        else{            
            automaticTextArea.append("No Downloads Available\n");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        HomePanel.setVisible(false);
        DownloadsPane.setVisible(false);
        searchDefault.setVisible(false);
        MovieDBPAne.setVisible(false);
        SettingsPanel.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        String downloadDirectory;
        String torrntDirectory;
        String pollingTime = null;
        
        if(black.isSelected()){
            TorrentCenter.getInstance().setBackgroundColor("black");
        }
        else if(white.isSelected()){
            TorrentCenter.getInstance().setBackgroundColor("white");
        }
        else if(gray.isSelected()){
            TorrentCenter.getInstance().setBackgroundColor("gray");
        }
        else if(darkgray.isSelected()){
            TorrentCenter.getInstance().setBackgroundColor("darkgray");
        }
        
        if(twentyMin.isSelected()){
            MediaCompanionSettings.pollingInterval=20;
            pollingTime="20";
        }
        else if(thirtyMin.isSelected()){
            MediaCompanionSettings.pollingInterval=30;
            pollingTime="30";
        }
        else if(fortyMin.isSelected()){
            MediaCompanionSettings.pollingInterval=40;
            pollingTime="40";
        }
        else if(oneHour.isSelected()){
            MediaCompanionSettings.pollingInterval=60;
            pollingTime="60";
        }
        
            
        MediaCompanionSettings.DownloadPath=fileDirectory.getText();
        downloadDirectory=fileDirectory.getText();
        MediaCompanionSettings.TorrentPath=torrentDirectory.getText();
        torrntDirectory=torrentDirectory.getText();
        
        System.out.println(fileDirectory.getText());
        
        try {
                            BufferedReader reader;
                            OutputStreamWriter writer;
                        
                            URL url = new URL("http://localhost/MediaCompanion/SLIM_API/index.php/settings");
                            URLConnection conn = url.openConnection();
                            conn.setDoOutput(true);
                            writer = new OutputStreamWriter(conn.getOutputStream());
                            writer.write("username="+LoginFrame.username+"&polling="+pollingTime+"&downloaddirectory="+downloadDirectory+"&torrentdirectory="+torrntDirectory);
                            writer.flush();
                            String line;
                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            
                            while ((line = reader.readLine()) != null){
                                System.out.println(line);              
                            }
                        

                        writer.close();
                        reader.close();

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } 
        
    }//GEN-LAST:event_jButton9ActionPerformed

    private void fileDirectoryChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileDirectoryChangeActionPerformed
         final JFileChooser directorySelect = new JFileChooser() {
            public void approveSelection() {
                if (getSelectedFile().isFile()) {
                    return;
                } else
                    super.approveSelection();
            }
            };
        directorySelect.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        directorySelect.showOpenDialog(null);
        File selected = directorySelect.getCurrentDirectory();
        fileDirectory.setText(selected.getAbsolutePath());
    }//GEN-LAST:event_fileDirectoryChangeActionPerformed

    private void torrentDirectoryChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_torrentDirectoryChangeActionPerformed
        final JFileChooser directorySelect = new JFileChooser() {
            public void approveSelection() {
                if (getSelectedFile().isFile()) {
                    return;
                } else
                    super.approveSelection();
            }
            };
        directorySelect.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        directorySelect.showOpenDialog(null);
        directorySelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File selected = directorySelect.getCurrentDirectory();
        torrentDirectory.setText(selected.getAbsolutePath());
    }//GEN-LAST:event_torrentDirectoryChangeActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://localhost/MediaCompanion/"));
        } catch (IOException ex) {
            Logger.getLogger(TorrentCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        int choice = JOptionPane.showConfirmDialog(null,"Do You Really Want to Close Media Companion?", "Exiting Media Companion",  JOptionPane.YES_NO_OPTION);
        if(choice== JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    public static void setUsername(String uname){
        username=uname;
    }
    
    public static void setProfilename(String pname){
        profilename=pname;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getProfilename(){
        return profilename;
    }
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
//           /* for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//            */
//           //UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TorrentCenter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TorrentCenter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TorrentCenter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TorrentCenter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                    new TorrentCenter().setVisible(true);                
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AutoActivityPanel;
    private javax.swing.JLayeredPane Auto_manualPanel;
    private javax.swing.JPanel AutomaticDownloadsPane;
    private javax.swing.JPanel ButtonsPane;
    private javax.swing.JButton DownloadButton;
    private javax.swing.JPanel DownloadDirectories;
    private javax.swing.JPanel DownloadsPane;
    private javax.swing.JPanel Holder;
    private javax.swing.JPanel HomePanel;
    private javax.swing.JLayeredPane LayeredPane;
    private javax.swing.JPanel MDResultsPane;
    private javax.swing.JPanel ManualAvailabilityPanel;
    private javax.swing.JPanel ManualDownloadsPane;
    private javax.swing.JPanel MovieDBPAne;
    private javax.swing.JPanel MovieLoadingPane;
    private org.jdesktop.swingx.JXSearchField SearchField;
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JPanel UserHistoryPanel;
    private javax.swing.JTextArea actors;
    private javax.swing.JTable automaticTable;
    private javax.swing.JTextArea automaticTextArea;
    private javax.swing.JTextArea availableListTextArea;
    private javax.swing.JRadioButton black;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JRadioButton darkgray;
    private javax.swing.JLabel director;
    private javax.swing.JLabel downloadDateLabel;
    private javax.swing.JButton downloadSearchButton;
    private javax.swing.JButton downloadSelected;
    private javax.swing.JLabel downloadTypeLabel;
    private javax.swing.JComboBox favoritesList;
    private javax.swing.JTextField fileDirectory;
    private javax.swing.JButton fileDirectoryChange;
    private javax.swing.JRadioButton fortyMin;
    private javax.swing.JLabel genre;
    private javax.swing.JRadioButton gray;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel imdbrating;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JMenuBar menuBar;
    private org.jdesktop.swingx.JXBusyLabel movieSearchBusy;
    private javax.swing.JRadioButton oneHour;
    private javax.swing.JTextArea plot;
    private javax.swing.JLabel randomPoster1;
    private javax.swing.JLabel randomPoster2;
    private javax.swing.JLabel randomPoster3;
    private javax.swing.JLabel randomPoster4;
    private javax.swing.JLabel released;
    private javax.swing.JTable resultsTable;
    private javax.swing.JButton runinBackground;
    private javax.swing.JLabel runtime;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchDefault;
    private javax.swing.JLabel seriesNameLabel;
    private javax.swing.JRadioButton thirtyMin;
    private javax.swing.JLabel title;
    private javax.swing.JTextField torrentDirectory;
    private javax.swing.JButton torrentDirectoryChange;
    private javax.swing.JRadioButton twentyMin;
    private javax.swing.JLabel userAvatar;
    private javax.swing.JLabel welcomeLabel;
    private javax.swing.JRadioButton white;
    private javax.swing.JLabel year;
    // End of variables declaration//GEN-END:variables
}
