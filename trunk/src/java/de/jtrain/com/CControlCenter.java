/*
this class holds the list of turnouts, the list of locos etc;
implemented as singleton, we just need one;
last modified: 2002 05 08
author: Werner Kunkel
*/

package de.jtrain.com;

import java.util.*;
import javax.swing.*;

import de.jtrain.config.CLang;
import de.jtrain.control.CLoco;
import de.jtrain.turnout.CTurnout;

import java.io.*;
import java.awt.*;

public class CControlCenter {

  private static CControlCenter controlCenter   = null;
  private static CTurnout [] turnoutList         = new CTurnout [255];
  private static Vector vLocoList                = null;
  private static Vector vBlockList               = null;
  private static Vector vChainList               = null;
  /* store global settings here: */
  private static Properties Settings = new Properties ();
  private static final Dimension screenSize
    = Toolkit.getDefaultToolkit().getScreenSize();
  private static final Point origin
    = new Point((int)screenSize.getWidth() / 2 -150,
    (int)screenSize.getHeight() / 2 - 100);
  private static Image icon                      = null;
  private static boolean bConnected              = false;

  public final static CTurnout [] getTurnoutList () {return turnoutList;}
  public final static Vector getLocoList ()         {return vLocoList;}
  public final static Vector getBlockList ()        {return vBlockList;}
  public final static Vector getChainList ()        {return vChainList;}
  public final static Dimension getScreenSize ()    {return screenSize;}
  public final static Point getOrigin ()            {return origin;}
  public final static Image getIcon ()              {return icon;}
  public final static boolean getConnected ()       {return bConnected;}
  public final static void setIcon (Image img)      {icon = img;}
  public final static void setConnected (boolean b) {bConnected = b;}

  /* define all file names */
  private final static String PROPERTYFILE = ".jtrainrc";
  private final static String LOCOFILE     = "loco.dat";
  private final static String BLOCKFILE    = "block.dat";
  private final static String CHAINFILE    = "chain.dat";
  private final static String TURNOUTFILE  = "to.dat";

  public static CControlCenter getControlCenter (){
    if (controlCenter == null)
      controlCenter = new CControlCenter ();
    return controlCenter;
  }

  private CControlCenter (){
    loadSettings();
    turnoutList     = loadTurnoutList ();
    vLocoList       = loadLocoList ();
    vBlockList      = loadBlockList ();
    vChainList      = loadChainList ();
    icon            = loadIcon ();
    CLang.getLang ();
  }

  private final Image loadIcon (){
    ImageIcon ii = new ImageIcon ("jt11_32.gif");
    return ii.getImage();
  }

  public final static String getSetting(String key) {
    return Settings.getProperty(key);
  }

  public final static void setSetting(String key, String value) {
    Settings.setProperty(key, value);
  }

  public final static void updateBlockList (){
    vBlockList      = loadBlockList ();
  }

  /* 5 */
  private static final void loadSettings(){

  /* personal settings are saved in "user.home" -> .jtrainrc */
    String FileName= System.getProperty ("user.home") 
      + File.separator + PROPERTYFILE;
      
    FileInputStream fis = null;

    try {
      fis = new FileInputStream(FileName);
      Settings.load(fis);
      fis.close();
    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(
        null,
        "Persönliche Konfigurationsdatei\n'" + FileName +
	"'\nwurde nicht gefunden, benutze Voreinstellungen!\n"+
	"Das Arbeitsverzeichnis ist: " + System.getProperty("user.home")+
	"\nBitte legen Sie das Verzeichnis für Ihre Dateiablage im\n"+
	"Menü unter 'Editoren/Einstellungen' fest.",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
      /* hier Voreinstellungen setzen: */
      Settings.setProperty("workingdir",
        System.getProperty("user.home"));
      Settings.setProperty("host", "localhost");
      Settings.setProperty("port", "12345");
      Settings.setProperty("lookandfeel",
        UIManager.getCrossPlatformLookAndFeelClassName());
        /*UIManager.getSystemLookAndFeelClassName());*/
      Settings.setProperty("language", "deutsch");
      Settings.setProperty("sendstop", "JTrain");
      
    }
  }

  public static final void saveSettings(){

    String FileName= System.getProperty ("user.home")
      + File.separator + PROPERTYFILE;
    
    FileOutputStream fos = null;
    try{
      fos = new FileOutputStream(FileName);
      Settings.store(fos, "JTrain-Properties");
      fos.flush ();
      fos.close ();
    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nPersönliche Konfigurationsdatei\n'" + FileName +
	"'\nkonnte nicht gespeichert werden!",
        "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /* 2 */
  private static final Vector loadLocoList (){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + LOCOFILE;
      
    Vector v = new Vector ();
    try{
      FileInputStream fis = new FileInputStream (FileName);
      ObjectInputStream ois = new ObjectInputStream (fis);
      v = (Vector)ois.readObject ();
      ois.close ();
      fis.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(null,
        "Achtung!\nLokliste '" + FileName +
	"' nicht vorhanden oder defekt!",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
    }
    return v;
  }

  public static final void saveLocoList (Vector vLcList){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + LOCOFILE;
      
    FileOutputStream   fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream(FileName);
      oos = new ObjectOutputStream (fos);
      oos.writeObject (vLcList);
      oos.flush();
      oos.close();
      fos.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nLokliste '" + FileName +
	"' konnte nicht geschrieben werden! "+e.toString(),
        "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /* 1 */
  public static final CTurnout [] loadTurnoutList (){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + TURNOUTFILE;
  
    CTurnout [] tl = null;
    try{
      FileInputStream fis = new FileInputStream (FileName);
      ObjectInputStream ois = new ObjectInputStream (fis);
      tl = (CTurnout [])ois.readObject ();
      ois.close ();
      fis.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nSchaltdekoderliste '" + FileName +
	"' nicht vorhanden oder defekt!",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
    }
    return tl;
  }

  public static final void saveTurnoutList (CTurnout [] tl){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + TURNOUTFILE;
  
    FileOutputStream   fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream (FileName);
      oos = new ObjectOutputStream (fos);
      oos.writeObject (tl);
      oos.flush();
      oos.close();
      fos.close();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nSchaltdekoderliste '" + FileName + 
	"' konnte nicht geschrieben werden! "
	+ e.toString(), "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /* 3 */
  private static final Vector loadBlockList (){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + BLOCKFILE;
  
    Vector v = new Vector ();
    try{
      FileInputStream fis = new FileInputStream (FileName);
      ObjectInputStream ois = new ObjectInputStream (fis);
      v = (Vector)ois.readObject ();
      ois.close ();
      fis.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(null,
        "Achtung!\nBlockliste '" + FileName + 
	"' nicht vorhanden oder defekt!",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
    }
    if (v == null) v = new Vector ();
    return v;
  }

  public static final void saveBlockList (){

    String FileName= Settings.getProperty("workingdir")
      + File.separator + BLOCKFILE;
  
    FileOutputStream   fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream (FileName);
      oos = new ObjectOutputStream (fos);
      oos.writeObject (vBlockList);
      oos.flush ();
      oos.close ();
      fos.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nBlockliste '" + FileName +
	"' konnte nicht geschrieben werden! "+ e.toString (),
        "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /* 4 */
  private static final Vector loadChainList (){

    String FileName= Settings.getProperty("workingdir")
      + File.separator + CHAINFILE;
  
    Vector v = new Vector ();
    try{
      FileInputStream fis = new FileInputStream (FileName);
      ObjectInputStream ois = new ObjectInputStream (fis);
      v = (Vector)ois.readObject ();
      ois.close ();
      fis.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(null,
        "Achtung!\nKettenliste '" + FileName +
	"' nicht vorhanden oder defekt!",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
    }
    if (v == null) v = new Vector ();
    return v;
  }

  public static final void saveChainList (){

    String FileName= Settings.getProperty ("workingdir")
      + File.separator + CHAINFILE;
  
    FileOutputStream   fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream (FileName);
      oos = new ObjectOutputStream (fos);
      oos.writeObject (vChainList);
      oos.flush ();
      oos.close ();
      fos.close ();
    }
    catch (Exception e){
      JOptionPane.showMessageDialog( null,
        "Achtung!\nKettenliste '" + FileName +
	"' konnte nicht geschrieben werden! "+ e.toString (),
        "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public static CLoco getLocoByName (String s){
    for (Enumeration en = vLocoList.elements (); en.hasMoreElements ();) {
      CLoco loco = (CLoco) en.nextElement ();
      if (loco.getName().equals (s))
        return loco;
    }
    return null;
  }

}

