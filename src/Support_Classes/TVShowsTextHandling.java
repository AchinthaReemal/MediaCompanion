/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Support_Classes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Sony
 */
public class TVShowsTextHandling {
    
  public static String[] readShowsFile() {
      
    ArrayList<String> showList = new ArrayList<>();
    
    try {
        
       FileInputStream fstream = new FileInputStream("src\\File_Dependencies\\testwriter.txt");
       BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
       String stringLine;
       while ((stringLine = br.readLine()) != null) {
          showList.add(stringLine);
       }
       br.close();
       
    } catch (Exception e) {
    }
    
    return showList.toArray(new String[showList.size()]);
  
 }
    
}
