/*
this class defines the turnouts

last modified : 2002 03 13
author : Werner Kunkel
*/

package de.jtrain.turnout;
import java.io.Serializable;
import java.util.Vector;

import de.jtrain.com.CCommand;
import de.jtrain.com.CCommandPipe;
import de.jtrain.com.CControlCenter;


public class CTurnout implements Serializable{

  private int iId                   = 0;
  private String sProtocol          = null;
  private boolean bIsGreen          = false;
  private boolean bGreenOnStart     = false;
  private boolean bHorizontal       = false;
  private int iSwitchtime           = 100;
  private String sType              = null;
  private boolean bInUse            = false;
  private boolean bEnabled          = true;
  public Vector blockVec            = new Vector ();
  static String sSwitchoff          = null;
  static boolean bSwitchoff         = false;

  CTurnout (int iId)
  {
    super ();
    this.iId = iId;
  }

  public boolean getIsGreen (){
    return bIsGreen;
  }

  public void setIsGreen (boolean bMode){
    bIsGreen = bMode;
  }

  public boolean getHorizontal (){
    return bHorizontal;
  }

  public void setHorizontal (boolean b){
    this.bHorizontal = b;
  }

  public boolean getGreenOnStart (){
    return bGreenOnStart;
  }

  public void setGreenOnStart (boolean bMode){
    bGreenOnStart = bMode;
  }

  public int getId (){
    return this.iId;
  }

  public void setProtocol (String s){
    sProtocol = s;
  }

  public String getProtocol (){
    return sProtocol  ;
  }

  public void setSwitchtime (int iMs){
    this.iSwitchtime = iMs;
    return;
  }

  public int getSwitchtime (){
    return this.iSwitchtime;
  }

  public void setTyp (String s){
    sType = s;
  }

  public String getTyp (){
    return sType;
  }

  public void setInUse (boolean b){
    bInUse = b;
  }

  public boolean getInUse (){
    return bInUse;
  }

  public void setEnabled (boolean b){
    bEnabled = b;
  }

  public boolean getEnabled (){
    return bEnabled;
  }

  public final static void sendTcString (CTurnout to){
    sSwitchoff = CControlCenter.getSetting("sendstop");
    if (sSwitchoff.equals ("JTrain"))
      bSwitchoff = true;
    else
      bSwitchoff = false;
    String sTcs = null;
    String sTcs2 = null;
    int iPort = 1;
    CCommand com = new CCommand ();
    CCommand com2 = new CCommand ();
    if (to.getIsGreen ()== true)  iPort = 1;
    if (to.getIsGreen ()== false) iPort = 0;
    sTcs = ("SET GA "+ to.getProtocol () + " " + to.getId ()+ " " + iPort
    + " 1 " + to.getSwitchtime () + "\n");
    com.setCommand (sTcs);
    com.setTime (to.getSwitchtime ());
    CCommandPipe.putTcCommand (com);
    if (bSwitchoff){
      sTcs2 = ("SET GA "+ to.getProtocol () + " " + to.getId ()+ " " + iPort
      + " 0 " + "20" + "\n");
      com2.setCommand (sTcs2);
      com2.setTime (20);
      CCommandPipe.putTcCommand (com2);
    }
  }
}
