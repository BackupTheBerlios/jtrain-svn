/*
last modified: 2002 04 05
author: Werner Kunkel
*/

package de.jtrain.event;
import java.util.*;

public interface CTurnoutListener extends EventListener {
  public void handleEvent (CTurnoutEvent e);
}
