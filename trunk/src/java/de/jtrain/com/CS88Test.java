/*
This class is a JFrame to visualize the output of 1 s88-module

last modified: 2002 05 08
author: Werner Kunkel
*/

package de.jtrain.com;
import java.awt.*;
import javax.swing.*;

import de.jtrain.event.CFeedbackportEvent;
import de.jtrain.event.CFeedbackportListener;
import de.jtrain.main.JTrain;

import java.awt.event.*;

public class CS88Test extends JFrame implements CFeedbackportListener{

  private GridLayout gl = new GridLayout (2, 8, 10, 10);
  private JTextField field [] = new JTextField [16];
  private JTextField tf = null;
  private int iModnr = 0;
  private Container c = null;

  public CS88Test (int iModnr)
  {
    super ();
    this.iModnr = iModnr;
    this.setTitle ("Modul " + iModnr + ": JTrain S88-Tester");
    this.setSize (300, 100);
    this.setLocation (CControlCenter.getOrigin ());
    this.setIconImage (CControlCenter.getIcon ());
    c = this.getContentPane ();
    c.setLayout (gl);
    for (int i = 1; i <= 16; i++){
      tf = new JTextField ("" + i);
      field [i-1] = tf;
      tf.setHorizontalAlignment (SwingConstants.CENTER);
      tf.setBackground (Color.white);
      c.add (tf);
      tf.setVisible (true);
      tf.setEditable (false);
    }
    CFeedbackportHandler.getFeedbackportHandler().addFeedbackportListener (this);
    this.addWindowListener (new MyWindowAdapter ());
  }

  public void handleEvent (CFeedbackportEvent e){
    int iPort, iState, iNr;
    iPort = e.getPort();
    if (iPort >= (iModnr -1) * 16 + 1 && iPort < iModnr * 16 + 1){
      iNr = iPort - (iModnr -1) * 16;
      iState = e.getState();
      if (iState == 1){
        field [iNr -1].setBackground (Color.red);
      }
      else {
        field [iNr -1].setBackground (Color.yellow);
      }
    }
  }

  private final void cleanUp (){
    JTrain.mf.setControlmode (false);
    CFeedbackportHandler.getFeedbackportHandler().
        removeFeedbackportListener (this);
    setVisible (false);
    dispose ();
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      cleanUp ();
    }
  }
  
}

