/*
this class is used for handling locoevents;
implemented as singleton, we just need one!

last modified : 2002 02 15
author : Werner Kunkel
*/

package de.jtrain.control;
import java.awt.event.*;
import java.util.*;

public class CLocoEventHandler 
{

  private static CLocoEventHandler leh = null;
  protected static Vector listeners = new Vector ();

  public static CLocoEventHandler getLocoEventHandler()
  {
    if (leh == null)
    	leh = new CLocoEventHandler ();
    return leh;
  }

  public static void addLocoListener (CLocoListener l)
  {
    listeners.addElement (l);
  }

  public static void removeLocoListener (CLocoListener l)
  {
    listeners.removeElement (l);
  }

  public static void fireEvent (CLocoEvent e)
  {
    for (int i = 0; i < listeners.size(); i++)
    {
      CLocoListener l = (CLocoListener) listeners.elementAt (i);
      l.handleEvent (e);
    }
  }
}
