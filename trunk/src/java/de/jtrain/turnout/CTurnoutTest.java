/*
this class defines a turnouttester to check hardware- or connectionproblems

last modified : 2002 20 18
author : Werner Kunkel
*/

package de.jtrain.turnout;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.event.CMyKeyAdapter;
import de.jtrain.main.JTrain;

import java.awt.event.*;

public class CTurnoutTest extends JFrame
  implements ActionListener, ChangeListener, Runnable{

  private BorderLayout bl     = new BorderLayout (20, 20);
  private JSlider jSl         = null;
  private JLabel jTF          = new JLabel ("000", SwingConstants.CENTER);
  private Font f              = new Font ("SansSerif",1,80);
  private JPanel jPSouth      = new JPanel ();
  private JButton jBStart     = new JButton ("Start");
  private JButton jBStop      = new JButton ("Stop");
  private JButton jBEnd       = new JButton ("Ende");
  private CTurnout to         = null;
  private Thread t            = null;
  private int iSleeptime      = 500;
  private int iAdr            = 0;
  private boolean bSingletest = false;

  public CTurnoutTest (int iAdr) {
    super ();
    this.iAdr = iAdr;
    if (iAdr > 0) bSingletest = true;
    else bSingletest = false;
    this.setTitle ("Schaltdekoder-Tester");
    this.setSize (300, 250);
    this.setIconImage (CControlCenter.getIcon ());
    Point p = new Point (CControlCenter.getOrigin ());
    if (bSingletest) p.translate (-150, +150);
    else p.translate (+150, +150);
    this.setLocation (p);
    this.setResizable (false);
    this.getContentPane ().setLayout (bl);
    this.getContentPane().setBackground (Color.white);
    jSl = new JSlider (SwingConstants.HORIZONTAL, 0, 2000, 500);
    jSl.setPaintTicks (true);
    jSl.setPaintLabels (true);
    jSl.setMajorTickSpacing (250);
    jSl.setMinorTickSpacing (50);
    jSl.setSnapToTicks (false);
    jSl.addChangeListener (this);
    this.getContentPane ().add (jSl, bl.NORTH);
    jTF.setSize (100, 100);
    jTF.setBackground (Color.white);
    jTF.setFont (f);
    this.getContentPane().add (jTF, bl.CENTER);
    jBStart.addActionListener (this);
    jBStop.addActionListener (this);
    jBEnd.addActionListener (this);
    jPSouth.add (jBStart);
    jPSouth.add (jBStop);
    jPSouth.add (jBEnd);
    this.getContentPane ().add (jPSouth, bl.SOUTH);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addWindowListener (new MyWindowAdapter ());
    this.addKeyListener (new CMyKeyAdapter ());
    if (bSingletest) jTF.setText ("" + iAdr);
  }

  public void run (){
    while (!Thread.interrupted ()){
      try{
        jTF.setForeground (Color.green);
        if (!bSingletest){
          for (int i = 1; i < 255; i++){
            if (CControlCenter.getTurnoutList ()[i].getInUse()){
              jTF.setText ("" + i);
              CControlCenter.getTurnoutList ()[i].setIsGreen (true);
              CTurnout.sendTcString (CControlCenter.getTurnoutList ()[i]);
              Thread.sleep (iSleeptime);
            }
          }
          jTF.setForeground (Color.red);
          for (int i = 1; i < 255; i++){
            if (CControlCenter.getTurnoutList ()[i].getInUse()){
              jTF.setText ("" + i);
              CControlCenter.getTurnoutList ()[i].setIsGreen (false);
              CTurnout.sendTcString (CControlCenter.getTurnoutList ()[i]);
              Thread.sleep (iSleeptime);
            }
          }
        }
        else if (bSingletest){
          jTF.setForeground (Color.green);
          jTF.setText ("" + iAdr);
          CControlCenter.getTurnoutList ()[iAdr].setIsGreen (true);
          CTurnout.sendTcString (CControlCenter.getTurnoutList ()[iAdr]);
          Thread.sleep (iSleeptime);
          jTF.setForeground (Color.red);
          jTF.setText ("" + iAdr);
          CControlCenter.getTurnoutList ()[iAdr].setIsGreen (false);
          CTurnout.sendTcString (CControlCenter.getTurnoutList ()[iAdr]);
          Thread.sleep (iSleeptime);
        }
      }
      catch (Exception e){
        Thread.currentThread ().interrupt ();
      }
    }
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource();
    if (o == jBEnd){
      if (t != null) t.interrupt ();
      JTrain.mf.setControlmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBStart){
      if (t == null || !t.isAlive()){
        t = null;
        t = new Thread (this);
        t.start ();
      }
    }
    if (o == jBStop){
      if (t != null)
        t.interrupt ();
    }
  }

  public void stateChanged (ChangeEvent e){
    JSlider sl = (JSlider)e.getSource ();
    iSleeptime = sl.getValue ();
    if (iSleeptime < 50) iSleeptime = 50;
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      if (t != null) t.interrupt ();
      JTrain.mf.setControlmode (false);
    }
  }

}

