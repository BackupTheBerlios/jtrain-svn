/*
This class defines the pipe with all the commands to send.

last modified: 2002 02 18
author: Werner Kunkel
*/

package de.jtrain.com;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.jtrain.srcp.Command;

public class CCommandPipe extends LinkedList implements Runnable 
{
  private static CCommandPipe me;
  private Command srcp;
  private static boolean commandmode;
  private static List hpq;
  private static List npq;
  private static int iSleeptime ;
  private Thread thread;
  private JFrame owner;
  private String sSwitchoff;
  private boolean bSwitchoff;
  
  /**
   * Create instance with given socket
   * @param f parent frame
   * @param s socket
   */
  private CCommandPipe (JFrame f)
  {
    this.owner  = f;
    srcp = Command.getInstance();
    npq = Collections.synchronizedList(new LinkedList());
    hpq = Collections.synchronizedList(new LinkedList());
    sSwitchoff = CControlCenter.getSetting("sendstop");
    if (sSwitchoff.equals ("JTrain"))
      bSwitchoff = true;
    else
      bSwitchoff = false;
	   if (thread == null || !thread.isAlive ())
	   {
	      thread = new Thread (this);
	      thread.setPriority (Thread.NORM_PRIORITY + 1);
	      thread.start();
	   }
  }

  /**
   * get instance (singleton pattern)
   * @param f parent frame
   * @return instance of CCommandPipe
   */
  public static CCommandPipe getCommandPipe (JFrame f)
  {
     if(me == null)
     {
			me = new CCommandPipe(f);
     }
     return me;
  }
  
  /**
   * start CommandPipe and connect to srcpd server
   *
   */
  private void prepareCommandPipe()
  {
  	   // starting thread


   }
  
   public void destroy()
   {
   	thread.interrupt();
		srcp.close();
   }
  
   /**
    * enable/disable command mode
    * @param mode
    */
   public static void setCommandMode(boolean mode)
   {
  	   commandmode = mode;
   }
   
   /**
    * get current command mode
    * @return
    */
   public static boolean getCommandMode()
   {
   	 return commandmode;
   }
   
  /**
   * runs the commandpipe
   * @see java.lang.Runnable#run()
   */
  public synchronized void run ()
  {
  	 CCommand command = null;
  	 
    if (srcp == null)
    {
    	return;
    }
    while (!Thread.interrupted ())
    {
      try {
      	if (!npq.isEmpty() || !hpq.isEmpty() ) 
      	{
      		if(!hpq.isEmpty())
      		{
      			command = (CCommand)hpq.get(0);
      			iSleeptime = command.getTime();
      			srcp.sendCommand(command);
      			hpq.remove(command);
      		}
      		else if(!npq.isEmpty())
      		{
      			command = (CCommand)npq.get(0);
      			iSleeptime = command.getTime();
      			srcp.sendCommand(command); 
      			npq.remove(command);
      		}
      		if (bSwitchoff)
      		{ 
      			Thread.sleep (iSleeptime);
      		}
      	}
      	else
      	{
      		synchronized (me) {
      			wait(); 
      		}
      		//			Thread.sleep (50);
      	}
        } catch (InterruptedException ie) {
        } catch (Exception e) {
        JOptionPane.showMessageDialog(
          owner,
          "Achtung! Fehler bei der Kommandoübermittlung: " + e.toString (),
          "Warnung",
          JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

	/**
	 * put command to high priority queue
	 * @param com command
	 */
	public final static void putLcCommand (CCommand com)
	{
		if(commandmode)
		{
			hpq.add (com);
			synchronized (me)
			{
				me.notify();
			}
		}
	}
    
	/**
	 * put command to low priority queue
	 * @param com command
	 */
	public final static void putTcCommand (CCommand com)
	{
		if(commandmode)
		{
			npq.add (com);
			synchronized (me)
			{
				me.notify();
			}
		}
	}
}
