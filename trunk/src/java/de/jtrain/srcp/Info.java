/*
 * Created on 26.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.jtrain.srcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;

import de.jtrain.com.CCommand;
import de.jtrain.com.CControlCenter;

/**
 * @author ibruell
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Info extends SRCP 
{
	private static Info me;
	private OutputStreamWriter osr;

	protected Info()
	{
		super();
		init();
	}
	
	protected Info(Integer port)
	{
		super(port);
		init();
	}
	
	private void init()
	{
		try {
			if(getSRCPVersion() > 0 || 
					getSRCPVersion() == 0 && getSRCPMajor() > 8 || 
					getSRCPVersion() == 0 && getSRCPMajor() == 8 && getSRCPMinor() > 1)
			{
				osr = new OutputStreamWriter (getOutputStream ());
		  		CCommand init = null;
		    	init = new CCommand("SET CONNECTIONMODE SRCP INFO\n", 20);
		    	sendCommand(init);
		      init = new CCommand("GO\n", 20);
		    	sendCommand(init);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Info getInstance()
	{
		if(me == null)
		{
			if(getSRCPVersion() > 0 || 
					getSRCPVersion() == 0 && getSRCPMajor() > 8 || 
					getSRCPVersion() == 0 && getSRCPMajor() == 8 && getSRCPMinor() > 1)
			{
				me = new Info();
			}
			else
			{
				me = new Info(new Integer (CControlCenter.getSetting("port") + 2));
			}
		}
		return me;
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
	protected void sendCommand(CCommand comm) throws java.io.IOException
	{
		String s = comm.getCommand ();
		osr.write (s.toCharArray ());
		osr.flush ();
		System.out.print ("gesendet : " + s);
	}
}
