/*
the listener interface for blockEvents

last modified: 2001 11 28
author: Werner Kunkel
*/

package de.jtrain.event;
import java.util.*;

public interface CBlockListener extends EventListener {
  public void handleEvent (CBlockEvent e);
}

