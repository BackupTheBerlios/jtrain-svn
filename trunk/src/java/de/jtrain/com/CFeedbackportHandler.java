/*
This class handles the incoming infos of the feedbackport

last changes: 2004 10 15
author: Werner Kunkel, adapted from Torsten Voigt,
        socket debugging by Guido Scholz
*/

package de.jtrain.com;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import de.jtrain.event.CFeedbackportEvent;
import de.jtrain.event.CFeedbackportListener;
import de.jtrain.main.CMainFrame;
import de.jtrain.main.JTrain;

public class CFeedbackportHandler extends Thread {

  private static int NOTRUNNING       = 0;
  private static int RUNNING          = 1;
  private static int WAITING          = 2;
  private Socket s                    = null;
  private InputStream is              = null;
   private InputStreamReader isr       = null;
   private CMainFrame mf               = null;
   static int iState                   = NOTRUNNING;
   protected static Vector listeners   = new Vector();
   private static CFeedbackportHandler fh     = null;
   
  public static CFeedbackportHandler getFeedbackportHandler (){
    if (fh == null) fh = new CFeedbackportHandler ();
    return fh;
  }

  private CFeedbackportHandler (){
      super ();
      connect ();
  }

  public final void addFeedbackportListener (CFeedbackportListener l) {
    if (listeners.isEmpty ()) {
      if (iState == NOTRUNNING) {
        this.start ();
        iState = RUNNING;
      }
      if (iState == WAITING) {
        this.notify ();
        iState = RUNNING;
      }
    }

    listeners.addElement (l);
  }

  public final void removeFeedbackportListener (CFeedbackportListener l) {
    if (listeners.isEmpty ()) {
      if (iState == RUNNING) {
        try{
          this.wait ();
          iState = WAITING;
        }
        catch (InterruptedException e){}
      }
    }
    listeners.removeElement(l);
  }

  public void fireEvent(CFeedbackportEvent e) {
    for (int i = 0; i < listeners.size (); i++) {
      CFeedbackportListener l=(CFeedbackportListener)listeners.elementAt (i);
      l.handleEvent (e);
    }
  }

  public void finalize() {
    s = null;
    is=null;
    isr=null;
  }

  public final void disconnect() {
    this.interrupt();
    iState = NOTRUNNING;
    if (s!=null) {
      if (s.isConnected()) {
        try {
          s.close();
        }
        catch (IOException e) { }
    }
    s = null;
    is=null;
    isr=null;
    }
  }

  public final void connect() {
    String  host = "";
    Integer port;
    if (s!= null) return;
    try {
      host  = CControlCenter.getSetting("host");
      port  = new Integer(CControlCenter.getSetting("port"));
      s     = new Socket(host, port.intValue() + 1);
      is    = s.getInputStream();
      isr   = new InputStreamReader(is);
    }
    catch (Exception e) {
      if (s != null && s.isConnected()) {
        try {
          s.close();
        }
        catch (IOException se) { }
      }
      s=null;
      is=null;
      isr=null;
      CControlCenter.setConnected (false);
      JOptionPane.showMessageDialog(
        JTrain.mf,
        "Es konnte keine Verbindung zum Feedback-Port\n"+
	"des SRCP-Servers '" + host + "' hergestellt werden!",
        "Warnung",
        JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // thread
  public void run() {
    int cnt,i;
    char[] recv = new char [32*16*30];
    for (i = 0; i < 32 * 16 * 30; i++) recv [i]=' ';
    while (!this.isInterrupted()) {
      try {
        if (isr!= null) {
          cnt = isr.read (recv);        // info received
          StringTokenizer st = new StringTokenizer (new String (recv));
          for (i = 0; i < cnt; i++) recv [i]=' ';
          while (st.hasMoreTokens ()) {
            String sDummy = st.nextToken ();
            String sDevice = st.nextToken ();
            if (!sDevice.equals("FB")) break;
            String modtype  = st.nextToken();
            int portnr   = Integer.parseInt(st.nextToken());
            int state    = Integer.parseInt(st.nextToken());

            fireEvent(new CFeedbackportEvent(CFeedbackportHandler.this,
                            portnr, state));
          }
        }
        else return;
      }
      catch (Exception e) {
      }
    }
  }
}

