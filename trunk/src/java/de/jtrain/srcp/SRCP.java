/*
 * Created on 26.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.jtrain.srcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.jtrain.com.CControlCenter;

/**
 * @author ibruell
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SRCP 
{
	private static String host;
	private static String serverInfo = " nicht verbunden!";
	private static String srcp_Version;
	private static String serverVersion;
	private static int srcpVersion;
	private static int srcpMajor;
	private static int srcpMinor;
	private Socket socket;
	
	static {
		host = CControlCenter.getSetting("host");
	}
	
	/**
	 * connecting to srcpd and compute welcome string
	 *
	 */
	protected SRCP()
	{
		this(new Integer (CControlCenter.getSetting("port")));
	}
	/**
	 * connecting to srcpd and compute welcome string
	 *
	 */
	protected SRCP(Integer port)
	{
		try {
			socket = new Socket (host, port.intValue());

			if (socket != null)
		   {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				char[] recv = new char[80];
				int bytes = isr.read (recv);
		      serverInfo = new String (recv, 0, bytes);
		      String [] welcome = serverInfo.split(";");
		      for (int i =0; i < welcome.length; i++)
		      {
		      	String[] keyvalue = welcome[i].trim().split(" ");
		      	if(keyvalue[0].equals("SRCP"))
		      	{
		      		srcp_Version = keyvalue[1]; 
		      	}
		      	if(keyvalue[0].equals("srcpd"))
		      	{
		      		serverVersion = keyvalue[1]; 
		      	}
		      }
		      String[] parts = srcp_Version.split("\\.");
		      for(int i=0; i < parts.length; i++)
		      {
		      	switch(i)
					{
		      		case 0:
		      			srcpVersion = Integer.parseInt(parts[i]);
		      			break;
		      		case 1:
		      			srcpMajor = Integer.parseInt(parts[i]);
		      			break;
		      		case 2:
		      			srcpMinor = Integer.parseInt(parts[i]);
		      			break;
					}
		      }
		      System.out.println("Server: " + serverVersion);
		      System.out.println("SRCP: " + srcpVersion + "." + srcpMajor + "." + srcpMinor);
		   }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * InputStream to read from srcpd
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException
	{
		return socket.getInputStream();
	}
	  
	/**
	 * OutputStream to write to srcpd
	 * @return
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return socket.getOutputStream();
	}
	  
	/**
	 * getting srcp version string
	 * @return
	 */
   public static String getSRCP_Version()
   {
   	return srcp_Version;
   }
   
   /**
    * getting the whole srcp welcome string
    * @return
    */
   public static String getServerInfo()
   {
   	return serverInfo;
   }
   
   /**
    * getting left part of version number
    * @return
    */
   public static int getSRCPVersion()
   {
   	return srcpVersion;
   }
   
   /**
    * getting middler part of version number
    * @return
    */
   public static int getSRCPMajor()
   {
   	return srcpMajor;
   }
   
   /**
    * getting right part of version number
    * @return
    */
   public static int getSRCPMinor()
   {
   	return srcpMinor;
   }

   /**
    * close connection to srcpd
    *
    */
   public void close()
   {
   	try {
   		socket.close();
   	} catch (IOException e) {
   		e.printStackTrace();
   	}
   }

}
