/*
This class is the handler of blockevents.
Implemented as a singleton, one is enough.
last modified: 2002 03 16
author: Werner Kunkel
*/

package de.jtrain.block;
import java.util.Vector;

import de.jtrain.event.CBlockEvent;
import de.jtrain.event.CBlockListener;

public class CBlockEventHandler {

  private static CBlockEventHandler blockEventHandler = null;
  protected static Vector listeners = new Vector ();


  public static CBlockEventHandler getBlockEventHandler(){
    if (blockEventHandler == null)
      blockEventHandler = new CBlockEventHandler ();
    return blockEventHandler;
  }

  public static void addBlockListener (CBlockListener l){
    listeners.addElement (l);
  }

  public static void removeBlockListener (CBlockListener l){
    listeners.removeElement (l);
  }

  public static void fireEvent (CBlockEvent e){
    for (int i = 0; i < listeners.size(); i++){
      CBlockListener l = (CBlockListener) listeners.elementAt (i);
      l.handleEvent (e);
    }
  }
}

