/*
This class is the handler of turnoutevents.
last modified : 2002 02 18
author : Werner Kunkel
*/

package de.jtrain.turnout;
import java.awt.event.*;
import java.util.*;

import de.jtrain.event.CTurnoutEvent;
import de.jtrain.event.CTurnoutListener;

public class CTurnoutHandler {  //Als Singleton implementiert, es wird nur einer
                               //benötigt

  private static CTurnoutHandler turnoutHandler = null;
  protected static Vector listeners = new Vector ();

  public static CTurnoutHandler getTurnoutHandler(){
    if (turnoutHandler == null)
      turnoutHandler = new CTurnoutHandler ();
    return turnoutHandler;
  }

  public static void addTurnoutListener (CTurnoutListener l){
    listeners.addElement (l);
  }

  public static void removeTurnoutListener (CTurnoutListener l){
    listeners.removeElement (l);
  }

  public static void fireEvent (CTurnoutEvent e){
    for (int i = 0; i < listeners.size(); i++){
      CTurnoutListener l = (CTurnoutListener) listeners.elementAt (i);
      l.handleEvent (e);
    }
  }
}
