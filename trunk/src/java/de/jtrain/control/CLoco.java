/*
this class defines all parameters for the locos
last change: 2002 02 20
author: Werner Kunkel
*/

package de.jtrain.control;
import java.io.Serializable;

import de.jtrain.com.CCommand;
import de.jtrain.com.CCommandPipe;
import de.jtrain.srcp.Command;
import de.jtrain.srcp.SRCP;

public class CLoco implements Serializable{
  //we need that all:
  private static String sHead         = "GL";
  private static String sNumServer    = "1";
  private String sName                = null;
  private String sProtocoll           = null;
  private int iAddr                   = 0;
  private transient int iDirection    = 0;
  private transient int iSpeed        = 0;
  private int iSpeedsteps             = 0;
  private transient int iFunc         = 0;
  private int iNrOF                   = 0;
  private transient int iF1           = 0;
  private transient int iF2           = 0;
  private transient int iF3           = 0;
  private transient int iF4           = 0;
  private int iAccTime                = 0;
  private int iDecTime                = 0;
  private boolean bAccByJBahn         = false;
  private int iMaxSpeed               = 0;
  private transient boolean bLocoBusy = false;
  private boolean changed, speedChanged, dirChanged, funcChanged, f1Changed, f2Changed, f3Changed, f4Changed;
  private boolean inited;

  public static String getHead (){
    return sHead;
  }

  public String getName (){
    return sName;
  }

  public void setName (String s){
    sName = s;
  }

  public String getProtocoll (){
    return sProtocoll;
  }

  public void setProtocoll (String s){
    sProtocoll = s;
  }

  public int getAddr (){
    return iAddr;
  }

  public void setAddr (int i)
  {
  	  		iAddr = i;
  }

  public int getDirection (){
    return iDirection;
  }

  public void setDirection (int i)
  {
	  if (iDirection != i)
  	  {
	  		iDirection = i;
	  		changed = dirChanged = true;
  	  }
  }

  public int getSpeed (){
    return iSpeed;
  }

  public void setSpeed (int i)
  {
  		if(iSpeed != i)
  		{
  			iSpeed = i;
  			changed = speedChanged= true;
  		}
  }

  public int getSpeedsteps (){
    return iSpeedsteps;
  }

  public void setSpeedsteps (int i){
    iSpeedsteps = i;
  }

  public int getFunc (){
    return iFunc;
  }

  public void setFunc (int i)
  {
  		if(iFunc != i)
  		{
  			iFunc = i;
  			changed = funcChanged = true;
  		}
  }

  public int getNrOF (){
    return iNrOF;
  }

  public void setNrOF (int i){
    iNrOF = i;
  }

  public int getF1 (){
    return iF1;
  }

  public void setF1 (int i)
  {
  		if(iNrOF >= 1 && iF1 != i)
  		{
  			iF1 = i;
  			changed = f1Changed = true;
  		}
  }

  public int getF2 (){
    return iF2;
  }

  public void setF2 (int i)
  {
  		if(iNrOF >= 2 && iF2 != i)
  		{
  			iF2 = i;
  			changed = f2Changed = true;
  		}
  }

  public int getF3 (){
    return iF3;
  }

  public void setF3 (int i)
  {
  		if(iNrOF >= 3 && iF3 != i)
  		{
  			iF3 = i;
  			changed = f3Changed = true;
  		}
  }

  public int getF4 (){
    return iF4;
  }

  public void setF4 (int i)
  {
  		if(iNrOF >= 4 && iF4 != i)
  		{
  			iF4 = i;
  			changed = f4Changed = true;
  		}
  }

  public int getAccTime (){
    return iAccTime;
  }

  public void setAccTime (int i){
    iAccTime = i;
  }

  public int getDecTime (){
    return iDecTime;
  }

  public void setDecTime (int i){
    iDecTime = i;
  }

  public boolean getAccByJBahn (){
    return bAccByJBahn;
  }

  public void setAccByJBahn (boolean b){
    bAccByJBahn = b;
  }

  public int getMaxSpeed (){
    return iMaxSpeed;
  }

  public void setMaxSpeed (int i){
    iMaxSpeed = i;
  }

  public boolean getLocoBusy (){
    return bLocoBusy;
  }

  public void setLocoBusy (boolean b){
    bLocoBusy = b;
  }

  public final void sendLcString ()
  {
  		if (changed)
  		{
  			String sSub = "";
  			String sDir = "";
  			String sSpeed = "";
  			String sLcs = null, sInit = null;
  			CCommand com = new CCommand ();
  			com.setTime (20);
			if(SRCP.getSRCPVersion() > 0 || 
					SRCP.getSRCPVersion() == 0 && SRCP.getSRCPMajor() > 8 || 
					SRCP.getSRCPVersion() == 0 && SRCP.getSRCPMajor() == 8 && SRCP.getSRCPMinor() > 1)
  			{
  				if(!inited)
  				{
  					sInit = "INIT " + sNumServer + " " + sHead + " "+ iAddr + " " + sProtocoll.charAt(0) + " " + sProtocoll.charAt(1) + " " + iMaxSpeed + " " + (iNrOF+1) + "\n";
  					com.setCommand(sInit);
  					CCommandPipe.putTcCommand (com);
  					inited = true;
  				}
  				sDir = sDir + ((dirChanged || speedChanged || funcChanged) ? String.valueOf(iDirection) : "=");
  				sSpeed = sSpeed + ((dirChanged || speedChanged || funcChanged) ? String.valueOf(iSpeed) + " " + String.valueOf(iSpeedsteps) : "= =");
  				sSub = "" + iFunc;
  				sSub = sSub + " " + (f1Changed ? String.valueOf(iF1) : "=");
  				sSub = sSub + " " + (f2Changed ? String.valueOf(iF2) : "=");
  				sSub = sSub + " " + (f3Changed ? String.valueOf(iF3) : "=");
  				sSub = sSub + " " + (f4Changed ? String.valueOf(iF4) : "=");
  				sLcs = ("SET " + sNumServer + " " + sHead + " " + iAddr + " " + sDir + " "
  						+ sSpeed + " " + sSub + "\n");
  			}
  			else
  			{
  				if (iNrOF >= 1) sSub = sSub + " " + iF1;
  				if (iNrOF >= 2) sSub = sSub + " " + iF2;
  				if (iNrOF >= 3) sSub = sSub + " " + iF3;
  				if (iNrOF >= 4) sSub = sSub + " " + iF4;
  				sLcs = ("SET GL "+ sProtocoll + " " + iAddr + " " + iDirection + " "
  		  			  + iSpeed + " " + iSpeedsteps + " " + iFunc + " " + iNrOF + sSub + "\n");
  			}
  			com.setCommand (sLcs);
  			CCommandPipe.putTcCommand (com);
  			changed = speedChanged = dirChanged = funcChanged = f1Changed = f2Changed = f3Changed = f4Changed = false;
  		}
  }
}

