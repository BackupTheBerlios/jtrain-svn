/*
KeyAdapterClass to get keystrokes working:
emergency stop, help ...

last modified: 2002 02 27
author: Werner Kunkel
*/

package de.jtrain.event;
import java.awt.event.*;

import de.jtrain.main.JTrain;

public class CMyKeyAdapter extends KeyAdapter {
  public void KeyReleased (KeyEvent e){
    if (e.getKeyCode() == KeyEvent.VK_SPACE){
      JTrain.mf.powerOnOff ();
    }
  }
}

