/**/

package de.jtrain.control;
import java.awt.event.*;
import java.util.*;

public interface CLocoListener extends java.util.EventListener {
   public void handleEvent (CLocoEvent e);
}

