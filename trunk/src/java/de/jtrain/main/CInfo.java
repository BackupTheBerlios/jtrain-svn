/*
little class to info dialog
last modified: 2004 10 01
author: Werner Kunkel
*/

package de.jtrain.main;
import javax.swing.*;

import de.jtrain.com.CControlCenter;

import java.awt.*;

public class CInfo extends JDialog {

  CInfo (Frame owner, String title, boolean modal){
    super (owner, title, modal);
    this.setSize (300, 110);
    this.setLocation (CControlCenter.getOrigin ());
    JTextArea ta = new JTextArea ();
    this.getContentPane ().add (ta);
    ta.setEditable (false);
    ta.setText("JTrain- Modellbahnsteuerung Version 0.1\n");
    ta.append ("SRCP- Version 0.7.3\n");
    ta.append ("Autor: Werner Kunkel\n");
    ta.append ("Adresse im Internet: www.jtrain.de\n");
    ta.append ("Kontakt: mail@jtrain.de");
  }

}

