/*
this class holds the list of turnouts, the list of locos etc;
implemented as singleton, we just need one;
last modified: 2002 05 08
author: Werner Kunkel
*/

package de.jtrain.com;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.jtrain.config.CLang;
import de.jtrain.control.CLoco;
import de.jtrain.turnout.CTurnout;

public class CControlCenter {

  private static CControlCenter controlCenter;
  private static CTurnout [] turnoutList         = new CTurnout [255];
  private static Collection vLocoList;
  private static Collection vBlockList;
  private static Collection vChainList;
  /* store global settings here: */
  private static Properties settings = new Properties ();
  private static final Dimension screenSize
    = Toolkit.getDefaultToolkit().getScreenSize();
  private static final Point origin
    = new Point((int)screenSize.getWidth() / 2 -150,
    (int)screenSize.getHeight() / 2 - 100);
  private static Image icon;
  private static boolean bConnected;

  public final static CTurnout [] getTurnoutList () {return turnoutList;}
  public final static Collection getLocoList ()         {return vLocoList;}
  public final static Collection getBlockList ()        {return vBlockList;}
  public final static Collection getChainList ()        {return vChainList;}
  public final static Dimension getScreenSize ()    {return screenSize;}
  public final static Point getOrigin ()            {return origin;}
  public final static Image getIcon ()              {return icon;}
  public final static boolean getConnected ()       {return bConnected;}
  public final static void setIcon (Image img)      {icon = img;}
  public final static void setConnected (boolean b) {bConnected = b;}

  /* define all file names */
  private final static String PROPERTYFILE = ".jtrainrc";
  private final static String LOCOFILE     = "loco.xml";
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
    return settings.getProperty(key);
  }

  public final static void setSetting(String key, String value) {
    settings.setProperty(key, value);
  }

  public final static void updateBlockList (){
    vBlockList      = loadBlockList ();
  }

  /* 5 */
  private static final void loadSettings(){

  /* personal settings are saved in "user.home" -> .jtrainrc */
    String fileName= System.getProperty ("user.home") 
      + File.separator + PROPERTYFILE;
      
    FileInputStream fis = null;

    try {
      fis = new FileInputStream(fileName);
      settings.load(fis);
      fis.close();
    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(
        null,
        "Persönliche Konfigurationsdatei\n'" + fileName +
	"'\nwurde nicht gefunden, benutze Voreinstellungen!\n"+
	"Das Arbeitsverzeichnis ist: " + System.getProperty("user.home")+
	"\nBitte legen Sie das Verzeichnis für Ihre Dateiablage im\n"+
	"Menü unter 'Editoren/Einstellungen' fest.",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
      /* hier Voreinstellungen setzen: */
      settings.setProperty("workingdir",
        System.getProperty("user.home"));
      settings.setProperty("host", "localhost");
      settings.setProperty("port", "12345");
      settings.setProperty("lookandfeel",
        UIManager.getCrossPlatformLookAndFeelClassName());
        /*UIManager.getSystemLookAndFeelClassName());*/
      settings.setProperty("language", "deutsch");
      settings.setProperty("sendstop", "JTrain");
      
    }
  }

  public static final void saveSettings(){

    String fileName= System.getProperty ("user.home")
      + File.separator + PROPERTYFILE;
    
    FileOutputStream fos = null;
    try{
      fos = new FileOutputStream(fileName);
      settings.store(fos, "JTrain-Properties");
      fos.flush ();
      fos.close ();
    }
    catch (IOException e) {
      JOptionPane.showMessageDialog(
        null,
        "Achtung!\nPersönliche Konfigurationsdatei\n'" + fileName +
	"'\nkonnte nicht gespeichert werden!",
        "Fehler beim Speichern",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /* 2 */
  private static final Collection loadLocoList (){

    String fileName= settings.getProperty ("workingdir")
      + File.separator + LOCOFILE;
      
    ArrayList v = new ArrayList ();
    try{
      FileInputStream fis = new FileInputStream (fileName);
      Document document = null;
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      document = docBuilder.parse(fis);
      fis.close ();
      Element root = document.getDocumentElement();
      NodeList lNodes = root.getElementsByTagName("loco");
      for(int i = 0; i < lNodes.getLength(); i++)
      {
      	Node nLoco = (Element)lNodes.item(i);
      	if(nLoco instanceof Element)
      	{
      		Element eLoco = (Element) nLoco;
      		v.add(new CLoco(eLoco.getAttribute("id"),
      				          eLoco.getAttribute("protocol"),
									 Integer.parseInt(eLoco.getAttribute("address")),
									 Integer.parseInt(eLoco.getAttribute("speed")),
									 Integer.parseInt(eLoco.getAttribute("speedsteps")),
									 Integer.parseInt(eLoco.getAttribute("maxspeed")),
									 Integer.parseInt(eLoco.getAttribute("number-of-functions")),
									 eLoco.getAttribute("srcpd-bus-number")
				));
      	}
      }
    }
    catch (Exception e){
      JOptionPane.showMessageDialog(null,
        "Achtung!\nLokliste '" + fileName +
	     "' nicht vorhanden oder defekt!",
        "Fehler beim Laden",
        JOptionPane.INFORMATION_MESSAGE);
    }
    return v;
  }

  public static final void saveLocoList (Collection vLcList)
  {

    String FileName= settings.getProperty ("workingdir")
      + File.separator + LOCOFILE;
      
    FileOutputStream   fos = null;
    Document document = null;
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    TransformerFactory transFactory = TransformerFactory.newInstance(); 
    try {
      fos = new FileOutputStream(FileName);
      
      // create document
      DocumentBuilder builder = docFactory.newDocumentBuilder();
      document = builder.newDocument();
      
      // create elements
      Element root = document.createElement("lococonfig");
      document.appendChild(root);
      Element eLoco = null;
      CLoco cLoco = null;
      Iterator iter = vLcList.iterator();
      while(iter.hasNext())
      {
         cLoco = (CLoco)iter.next();
         eLoco = document.createElement("loco");
      	eLoco.setAttribute("id", cLoco.getName());
      	eLoco.setAttribute("protocol", cLoco.getProtocoll());
      	eLoco.setAttribute("address", Integer.toString(cLoco.getAddr()));
      	eLoco.setAttribute("speed", Integer.toString(cLoco.getSpeed()));
      	eLoco.setAttribute("speedsteps", Integer.toString(cLoco.getSpeedsteps()));
      	eLoco.setAttribute("maxspeed", Integer.toString(cLoco.getMaxSpeed()));
      	eLoco.setAttribute("number-of-functions", Integer.toString(cLoco.getNrOF()));
      	eLoco.setAttribute("srcpd-bus-number", cLoco.getBusNumber());
			root.appendChild(eLoco);
      }
      
      // save dom to xml file
      Transformer transformer = transFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-15");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(fos);
      transformer.transform(source, result);
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

    String FileName= settings.getProperty ("workingdir")
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

    String FileName= settings.getProperty ("workingdir")
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

    String FileName= settings.getProperty ("workingdir")
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

    String FileName= settings.getProperty("workingdir")
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

    String FileName= settings.getProperty("workingdir")
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

    String FileName= settings.getProperty ("workingdir")
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

  public static CLoco getLocoByName (String s)
  {
  	 Iterator iter = vLocoList.iterator();
  	 while(iter.hasNext())
  	 {
      CLoco loco = (CLoco) iter.next();
      if (loco.getName().equals (s))
        return loco;
    }
    return null;
  }

}

