/*
This class connects to the srcp daemon

last modified: 2002 02 18
author: Werner Kunkel
 */
package de.jtrain.srcp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;

import de.jtrain.com.CCommand;

/**
 * @author ibruell
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Command extends SRCP
{
	private static Command me;
	private OutputStreamWriter osr;
	private static int sessionId;

	protected Command()
	{
		super();
		try {

			osr = new OutputStreamWriter (getOutputStream ());
			
			if(getSRCPVersion() > 0 || 
				getSRCPVersion() == 0 && getSRCPMajor() > 8 || 
				getSRCPVersion() == 0 && getSRCPMajor() == 8 && getSRCPMinor() > 1)
			{
		    	InputStreamReader isr = new InputStreamReader(getInputStream());
		    	String replyString = null;
		    	char[] recv = new char[80];;
		    	int bytes = 0;

		    	CCommand init = null;
		    	init = new CCommand("SET CONNECTIONMODE SRCP COMMAND\n", 20);
		    	sendCommand(init);
				bytes = isr.read (recv);
		      replyString = new String (recv, 0, bytes-1);
		      System.out.println(replyString);

		      init = new CCommand("GO\n", 20);
		    	sendCommand(init);
		    	
		    	// getting session info
				bytes = isr.read (recv);
		      replyString = new String (recv, 0, bytes-1);
		      System.out.println(replyString);
		      String [] session = replyString.split(" ");
		      System.out.println("ID:      " + session[4]);
		      if(session.length > 4 && session[1].equals("200"))
		      {
		      	sessionId = Integer.parseInt(session[4]);
		      }
		      System.out.println("ID:      " + sessionId);
		      
		    	// TODO bus numbers from config file
		    	init = new CCommand("SET 1 POWER ON\n", 20);
		    	sendCommand(init);
				bytes = isr.read (recv);
		      replyString = new String (recv, 0, bytes-1);
		      System.out.println(replyString);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Command getInstance()
	{
		if(me == null)
		{
			me = new Command();
		}
		return me;
	}
	
	public static int getSessionId()
	{
		return sessionId;
	}
	
	public void close()
	{
		try {
			osr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.close();
	}
	  
  /**
   * send command
   * @param comm a command within a Command object
   * @throws java.io.IOException
   * @see CCommand
   */
	public void sendCommand(CCommand comm) throws java.io.IOException
	{
		String s = comm.getCommand ();
		osr.write (s.toCharArray ());
		osr.flush ();
		System.out.print ("gesendet : " + s);
	}

}
