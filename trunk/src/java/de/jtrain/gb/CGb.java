/*
the parent class for all Gb-classes handling the tracklayout

last modified: 2002 05 07
author: Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import de.jtrain.block.CBlContainer;
import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFbElement;
import de.jtrain.main.ExampleFileFilter;

public class CGb extends JFrame
{
  protected String sTitle                 = null;
  private Dimension gbSize                = new Dimension ();
  final static Dimension smSize           = new Dimension (33,15);
  public final static Dimension medSize          = new Dimension (47,21);
  public static Dimension elementSize  = medSize;
  public Vector elList                    = new Vector (10,10);
  public Vector fbList                    = new Vector ();
  public Vector blList                    = new Vector ();
  public Vector streetList                = new Vector ();
  private String sFb                      = null;
  public  static String s               = null;
  protected int iDeltaX                   = (int) (elementSize.getWidth () -1);
  protected int iDeltaY                   = (int) (elementSize.getHeight () -1);
  final static int iYStart                = 125;
  final static Dimension dim              = new Dimension (735, 80);

  public CGb (String sTitle){
    super (sTitle);
    this.sTitle = sTitle;
  }

  public void setGbSize (int x, int y){
    gbSize.setSize (x, y);
  }

  public Dimension getGbSize (){
    return gbSize;
  }

  public static void setSmSize (){
    elementSize = smSize;
  }

  public static void setMedSize (){
    elementSize = medSize;
  }

  public static Dimension getElementSize (){
    return elementSize;
  }

  private final void buildFbStrg (){

    sFb = "";
    String sSub = "";

    for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
      CFbElement fbTmp = (CFbElement) en.nextElement ();
      sSub = "fb," + fbTmp.getX () + "," + fbTmp.getY () + "," + fbTmp.getId ()
      + "," + (int) fbTmp.getSize ().getHeight () + "\n";
      sFb = sFb + sSub;
    }
  }

  private final void sFbToFbList (String s){

    StringTokenizer stok1 = null;
    StringTokenizer stok2 = null;
    int iXPos = 0, iYPos = 0, iId = 0, iDim = 0;

    fbList.removeAllElements();
    stok1 = new StringTokenizer (s,"\n");
    while (stok1.hasMoreTokens()){
      CFbElement fbTmp = new CFbElement (this);
      stok2 = new StringTokenizer (stok1.nextToken (),",");
      int iWhereAmI = 0;
      while (stok2.hasMoreTokens ()){
        String sub = stok2.nextToken();
        if (iWhereAmI == 4){
          try{
            iDim = Integer.parseInt (sub);
          }
          catch (Exception e){
            showError (3, e.toString ());
          }
          iWhereAmI++;
        }
         if (iWhereAmI == 3){
          try{
            iId = Integer.parseInt (sub);
          }
          catch (Exception e){
            showError (3, e.toString ());
          }
          iWhereAmI++;
        }
         if (iWhereAmI == 2){
          try{
            iYPos = Integer.parseInt (sub);
          }
          catch (Exception e){
            showError (2, e.toString ());
          }
          iWhereAmI++;
        }
        if (iWhereAmI == 1){
          try{
            iXPos = Integer.parseInt (sub);
          }
          catch (Exception e){
            showError (1, e.toString ());
          }
          iWhereAmI++;
        }
        if (sub.equals ("fb")){
          iWhereAmI = 1;
        }
      }
      fbTmp.setLocation (iXPos, iYPos);
      fbTmp.setId (iId);
      fbTmp.setSize (iDim, iDim);
      fbTmp.setVisible (true);
      fbList.addElement (fbTmp);
    }
  }

  public final void save (String arg){

    FileOutputStream   fos = null;
    ObjectOutputStream oos = null;

    buildFbStrg ();
    try{
      fos = new FileOutputStream (arg + ".gbz");
      oos = new ObjectOutputStream (fos);
      oos.writeObject (arg);
      oos.writeObject (gbSize);
      oos.writeObject (elementSize);
      oos.writeObject (sFb);
      oos.writeObject (elList);
      oos.writeObject (blList);
      oos.writeObject (streetList);
      oos.flush();
      oos.close ();
      fos.close ();
    }
    catch (Exception e) {
      showError (5, e.toString());
    }
  }

  public final boolean load (){

    boolean bReturn = false;
    String workingDir = CControlCenter.getSetting ("workingdir");
    JFileChooser jfc = new JFileChooser (workingDir);
    ExampleFileFilter filter = new ExampleFileFilter ();
    filter.addExtension ("gbz");
    filter.setDescription ("JTrain-Gleisbilder");
    jfc.setFileFilter (filter);
    int returnVal = jfc.showOpenDialog (this);
    if (returnVal == JFileChooser.APPROVE_OPTION){
      try{
        FileInputStream fis = new FileInputStream
          (jfc.getCurrentDirectory().getAbsolutePath() +
	  File.separator + jfc.getSelectedFile ().getName ());
        ObjectInputStream ois = new ObjectInputStream (fis);
        sTitle = (String) ois.readObject ();
        gbSize = (Dimension) ois.readObject ();
        elementSize = (Dimension) ois.readObject ();
        sFb = (String) ois.readObject ();
        elList = (Vector) ois.readObject ();
        blList = (Vector) ois.readObject ();
        streetList = (Vector) ois.readObject ();
        ois.close ();
        fis.close ();
        sFbToFbList (sFb);
        bReturn = true;
      }
      catch (Exception e) {
        showError (4, e.toString());
      }
    }
    return bReturn;
  }

  public final void refresh (){
    CBlContainer blc  = null;
    int iBlNr         = 0;
    Color c           = null;
    CBlock bl         = null;
    Vector v          = null;
    CGbElement el     = null;
    if (!blList.isEmpty ()){
      for (Enumeration enu = blList.elements (); enu.hasMoreElements ();){
        blc = (CBlContainer) enu.nextElement ();
        iBlNr = blc.getBlNumber ();
        bl = getBlock (iBlNr);
        if (bl != null) {
          if (bl.getState () == CBlock.BLOCK_EXECUTED) c = Color.red;
          else c = bl.getColor ();
          v = blc.getBlElements ();
          if (v != null){
            for (Enumeration e = v.elements (); e.hasMoreElements ();){
              el = (CGbElement) e.nextElement ();
              el.setActiveColor (c);
              el.repaint ();
            }
            JButton blBox = blc.getBlBox ();
            blBox.setBackground (c);
            blBox.repaint ();
          }
        }
      }
    }
  }

  public final void refresh (int iBlNr){
    CBlContainer blc  = null;
    int iNr           = 0;
    Color c           = null;
    CBlock bl         = null;
    Vector v          = null;
    CGbElement el     = null;
    JButton blBox     = null;
    if (!blList.isEmpty ()){
      blc = getBlContainer (iBlNr);
      bl = getBlock (iBlNr);
      if (bl.getState() == CBlock.BLOCK_EXECUTED) c = Color.red;
      else c = bl.getColor ();
      v = blc.getBlElements ();
      for (Enumeration e = v.elements (); e.hasMoreElements ();){
        el = (CGbElement) e.nextElement ();
        el.setActiveColor (c);
        el.repaint ();
      }
      blBox = blc.getBlBox ();
      blBox.setBackground (c);
      blBox.repaint ();
    }
  }

  public final CBlock getBlock (int iBlNr){
    CBlock bl = null;
    if (iBlNr > 0){
      for (Enumeration en = CControlCenter.getBlockList ().elements ();
                          en.hasMoreElements();){
        bl = (CBlock) en.nextElement();
        if (bl.getBlNumber () == iBlNr){
          return bl;
        }
        bl = null;
      }
    }
    return bl;
  }

  public final CBlContainer getBlContainer (int iBlNr){
    CBlContainer blc = null;
    for (Enumeration enu = blList.elements (); enu.hasMoreElements ();){
      blc = (CBlContainer) enu.nextElement ();
      if (blc.getBlNumber () == iBlNr){
        return blc;
      }
    }
    return null;
  }

  public final boolean isEditable (CGbElement el){
    boolean bEditable = true;
    for (Enumeration en = blList.elements (); en.hasMoreElements ();){
      CBlContainer blc = (CBlContainer) en.nextElement ();
      Vector v = blc.getBlElements ();
      if (v != null && v.contains (el)){
        bEditable = false;
        break;
      }
    }
    return bEditable;
  }


  public final void showError (int iNr, String sErrMessage){
    String sMessage = null;

    if (iNr > 0 && iNr < 4) sMessage = "Fehler beim Dekodieren : ";
    if (iNr == 4)           sMessage = "Fehler beim Laden : ";
    if (iNr == 5)           sMessage = "Fehler beim Speichern : ";
    JOptionPane.showMessageDialog (
      this,
      sMessage + sErrMessage,
      "Warnung",
      JOptionPane.INFORMATION_MESSAGE);
  }
}
