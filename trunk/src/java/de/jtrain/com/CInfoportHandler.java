/*
This class enables listening to the infoport
last modified: 2002 05 08
author: Werner Kunkel
adapted from: Torsten Vogt
socket debugging by Guido Scholz
*/

package de.jtrain.com;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.jtrain.control.CLocoEvent;
import de.jtrain.control.CLocoEventHandler;
import de.jtrain.event.CTurnoutEvent;
import de.jtrain.main.JTrain;
import de.jtrain.srcp.Info;
import de.jtrain.srcp.SRCP;
import de.jtrain.turnout.CTurnoutHandler;

public class CInfoportHandler extends Thread 
{
  private Info srcpInfo;
  private InputStreamReader isr;

  protected Vector listeners = new Vector();
  private static CInfoportHandler ih;

  public static CInfoportHandler getInfoportHandler ()
  {
    if (ih == null)
    	ih = new CInfoportHandler ();
    return ih;
  }

  private CInfoportHandler () 
  {
    super ();
    connect ();
  }

  public final void finalize () 
  {
    srcpInfo = null; 
    isr = null;
  }

  public final void disconnect () 
  {
    this.interrupt();
    if (srcpInfo != null) 
    {
      srcpInfo.close();
      srcpInfo = null;
      isr = null;
    }
  }

  public final void connect () 
  {
    if ( srcpInfo != null)
    {
    	return;
    }
    try{
    	srcpInfo = Info.getInstance();
      isr = new InputStreamReader (srcpInfo.getInputStream());
      start ();
    }
    catch (Exception e) {
    	e.printStackTrace();
      if (srcpInfo != null) 
      {
          srcpInfo.close();
      }
      srcpInfo = null;
      isr = null;
      CControlCenter.setConnected (false);
      JOptionPane.showMessageDialog(JTrain.mf,
        "Es konnte keine Verbindung zum Info-Port\n"+
	     "des SRCP-Servers '" + 
		  CControlCenter.getSetting("host") + 
		  "' hergestellt werden.",
        "Warnung", JOptionPane.INFORMATION_MESSAGE);
    }
  }

   // thread
  public void run() 
  {
    this.setPriority (Thread.NORM_PRIORITY - 2);
    int iCnt, i;
    char recv[] = new char[8192];
    for (i = 0; i < 8192; i++) recv [i]=' ';
    while (!Thread.interrupted()) 
    {
      if (isr!=null) 
      {
        try {
          iCnt = isr.read (recv);
          String sStr = new String (recv);
          for (i = 0; i < iCnt; i++) recv [i]=' ';
          StringTokenizer st = new StringTokenizer (sStr,"\n");
          while (st.hasMoreElements ()) 
          {
          	computeInfoData((String)st.nextElement ());
          }
          st = null;
          sStr = null;
        }
        catch (IOException e) {
	       isr = null;
          if (srcpInfo != null)
          {
             srcpInfo.close();
          }
          srcpInfo = null;
          return;
        }
      }
      else return;
    }
  }
  
  private void computeInfoData(String data)
  {
		if(SRCP.getSRCPVersion() > 0 || 
				SRCP.getSRCPVersion() == 0 && SRCP.getSRCPMajor() > 8 || 
				SRCP.getSRCPVersion() == 0 && SRCP.getSRCPMajor() == 8 && SRCP.getSRCPMinor() > 1)
		{
			computeInfoData08(data);
		}
		else
		{
			computeInfoData07(data);
		}
  }

  private void computeInfoData07(String sInfo)
  {
   if (sInfo!= null && sInfo.startsWith("INFO")){
      StringTokenizer st2 = new StringTokenizer (sInfo," ");
      while (st2.hasMoreElements ()){
        String sDummy = (String)st2.nextElement ();
        String sDevice = (String)st2.nextElement ();
        if (sDevice.startsWith("GL")) {
          parseLocoInfo07 (sInfo);
          break;
        }
        if (sDevice.startsWith("GA")) {
          parseTurnoutInfo07 (sInfo);
          break;
        }
        if (sDevice.startsWith("POWER")) {
          parsePowerInfo07 (sInfo);
          break;
        }
      }
    }
  }
  
  private void computeInfoData08(String data)
  {
 	String[] pInfo = data.split(" ", 3);
   if (pInfo.length == 3 && pInfo[1].equals("100"))
   {
      String sInfo = pInfo[2];
      if (sInfo!= null && sInfo.startsWith("INFO"))
      {
        StringTokenizer st2 = new StringTokenizer (sInfo," ");
        while (st2.hasMoreElements ())
        {
          String sDummy = (String)st2.nextElement ();
          String sBus = (String)st2.nextElement ();
          int iBus = Integer.valueOf(sBus).intValue();
          String sDevice = (String)st2.nextElement ();
          if(iBus > 0)
          {
             if (sDevice.startsWith("GL")) 
             {
               parseLocoInfo08 (sInfo);
               break;
             }
             if (sDevice.startsWith("GA")) 
             {
               parseTurnoutInfo08 (sInfo);
               break;
             }
             if (sDevice.startsWith("POWER")) 
             {
               parsePowerInfo08 (sInfo);
               break;
             }
          }
       	 break;
        }
      }
   }
  }

  private  final void parseLocoInfo07 (String sStr){

   String  sProtocol  = null;
   int     iAddr      = 0;
   int     iSpeed     = 0;
   int     iVmax      = 0;
   int     iMaxFS     = 0;
   int     iDirection = 1;
   int     iF[]       = new int[9];
   int     iNrOF      = 0;
   int     iFF        = 0;

   StringTokenizer st           = new StringTokenizer (sStr);
   String sDummy                = st.nextToken();
   String sDevice               = st.nextToken();
   if (!sDevice.startsWith("GL")) return;
   if (sDevice.equals("GL")) {
     sProtocol   = st.nextToken ();
     iAddr       = Integer.parseInt (st.nextToken ());
     iDirection  = Integer.parseInt (st.nextToken ());
     iSpeed      = Integer.parseInt (st.nextToken ());
     iVmax       = Integer.parseInt (st.nextToken ());
     iF[0]       = Integer.parseInt (st.nextToken ());
     iNrOF       = Integer.parseInt (st.nextToken ());
     for (int i = 1; i <= iNrOF; i++)
       iF[i]  = Integer.parseInt (st.nextToken ());
     try{
       iFF = 0;
       for (int i = 1; i <= iNrOF; i++)
         iFF = iFF + (i << i);
     }
     catch (Exception e) {}
   }
   CLocoEventHandler.fireEvent (new CLocoEvent
     (this, iAddr, iDirection, iSpeed, iVmax, iF[0], iNrOF, iFF));
 }

  private  final void parseLocoInfo08 (String sStr)
  {
  	 String  sDummy = null;
  	 String  sDevice = null;
  	 String  sBus = null;
    String  sProtocol  = null;
    int     iProtocolVersion = 0;
    int     iAddr      = 0;
    int     iSpeed     = 0;
    int     iVmax      = 0;
    int     iMaxFS     = 0;
    int     iDirection = 0;
    int     iF[]       = new int[9];
    int     iNrOF      = 4;
    int     iFF        = 0;

    String[] splitStr = sStr.split(" ");
    for(int j = 0; j < splitStr.length; j++)
    {
    	 switch(j)
		 {
    	    case 0:
    	    	sDummy                = splitStr[j];
    	    	break;
    	 	 case 1:
    	 	 	sBus                  = splitStr[j];
    	 	 	break;
    	 	 case 2:
    	 	 	sDevice               = splitStr[j];
    	 	 	break;
		 }
	    if (sDevice != null && !sDevice.startsWith("GL"))
	    {
	    	return;
	    }
	    if (sDevice != null && sDevice.equals("GL")) 
	    {
	    	switch(j)
			{
	    		case 3:
	    			iAddr       = Integer.parseInt (splitStr[j]);
	    			break;
	    		case 4:
			      iDirection  = Integer.parseInt (splitStr[j]);
	    			break;
	    		case 5:
			      iSpeed      = Integer.parseInt (splitStr[j]);
			      break;
	    		case 6:
	    			iVmax       = Integer.parseInt (splitStr[j]);
	    			break;
	    		case 7:
		      	iF[0]       = Integer.parseInt (splitStr[j]);
		      	break;
		      case 8:
		        iF[0]  = Integer.parseInt (splitStr[j]);
		        break;
		      case 9:
		        iF[1]  = Integer.parseInt (splitStr[j]);
		        break;
		      case 10:
		        iF[2]  = Integer.parseInt (splitStr[j]);
		        break;
		      case 11:
		        iF[3]  = Integer.parseInt (splitStr[j]);
		        break;
		    }
		 }
    }
    try{
      iFF = 0;
      for (int i = 1; i <= iNrOF; i++)
        iFF = iFF + (i << i);
    }
    catch (Exception e) {}
    CLocoEventHandler.fireEvent (new CLocoEvent
      (this, iAddr, iDirection, iSpeed, iVmax, iF[0], iNrOF, iFF));
  }

  private final void parseTurnoutInfo07 (String sStr)
  {
    String  sProtocol  = null;
    int     iAddr      = 0;
    int     iPort      = 0;
    int     iState     = 0;
    boolean bIsGreen   = true;

    StringTokenizer st           = new StringTokenizer (sStr);
    String sDummy                = st.nextToken();
    String sDevice               = st.nextToken();
    if (!sDevice.startsWith("GA")) return;
    if (sDevice.equals("GA")) {
      sProtocol   = st.nextToken ();
      iAddr       = Integer.parseInt (st.nextToken ());
      iPort       = Integer.parseInt (st.nextToken ());
      iState      = Integer.parseInt (st.nextToken ());
    }
    if (iPort == 0) bIsGreen = false;
    if (iPort == 1) bIsGreen = true;
    CTurnoutHandler.fireEvent (new CTurnoutEvent (this, iAddr, bIsGreen, false));
  }

  private final void parseTurnoutInfo08 (String sStr)
	{
  	  // TODO
	}
  
  private final void parsePowerInfo07 (String sStr)
  {
    if (sStr.startsWith ("INFO POWER OFF")){
      JTrain.mf.setPowerOn (false);
      JTrain.mf.getContentPane ().setBackground (Color.red);
      JTrain.mf.repaint();
    }
    if (sStr.startsWith ("INFO POWER ON")){
      JTrain.mf.setPowerOn (true);
      JTrain.mf.getContentPane ().setBackground (Color.green);
      JTrain.mf.repaint();
    }
  }
   private final void parsePowerInfo08 (String sStr)
   {
   	String  sDummy = null;
  	 	String  sDevice = null;
  	 	String  sBus = null;
  	 	String  sState = null;
      String[] splitStr = sStr.split(" ");
      for(int j = 0; j < splitStr.length; j++)
      {
      	 switch(j)
			 {
      	    case 0:
      	    	sDummy = splitStr[j];
      	    	break;
      	 	 case 1:
      	 	 	sBus = splitStr[j];
      	 	 	break;
      	 	 case 2:
      	 	 	sDevice = splitStr[j];
      	 	 	break;
      	 	 case 3:
      	 	 	sState = splitStr[j];
      	 	 	break;
			 }
      }
      if(sState.equals("OFF"))
      {
         JTrain.mf.setPowerOn (false);
         JTrain.mf.getContentPane ().setBackground (Color.red);
         JTrain.mf.repaint();
      }
      else
      {
         JTrain.mf.setPowerOn (true);
         JTrain.mf.getContentPane ().setBackground (Color.green);
         JTrain.mf.repaint();
      }
   }
}

