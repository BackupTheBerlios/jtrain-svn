/*
the window for automatic train rides
last modified: 2002 05 05
author: Werner Kunkel
*/

package de.jtrain.chain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.control.CLoco;
import de.jtrain.control.CLocoEvent;
import de.jtrain.control.CLocoEventHandler;
import de.jtrain.gb.CGbExec;
import de.jtrain.gb.CStep;

import java.util.*;

public class CChainFrame extends JFrame implements ActionListener {

  private CGbExec parent          = null;
  private JFrame jChain           = new JFrame ();
  private JPanel jPSouth          = new JPanel ();
  private JButton jBStart, jBStop, jBAbort, jBShow, jBExit;
  private JScrollPane scrollPane  = null;
  private Container c             = null;
  private CChain chain            = null;
  private CChainExec  chainExec   = null;
  private Thread thread           = null;
  private CLoco loco              = null;
  private String sName            = null;
  private MyMouseAdapter mma      = new MyMouseAdapter ();
  private final Insets insets     = new Insets (0,0,0,0);

  CChainFrame (CChain chain, CGbExec parent){
    super ();
    this.chain = chain;
    this.loco = chain.getLoco();
    chain.setEdit (false);
    this.parent = parent;
    this.setSize (330, 60);
    this.setResizable (true);
    this.setLocation (100, 40);
    this.setIconImage (CControlCenter.getIcon ());
    c = this.getContentPane ();
    jPSouth.setLayout (new GridLayout (1, 5, 0, 0));

    jBStart = new JButton ("Start");
    jBStop  = new JButton ("Stop");
    jBAbort = new JButton ("Abbrechen");
    jBShow  = new JButton ("Zeige");
    jBExit  = new JButton ("Ende");
    jBStart.addActionListener (this);
    jBStart.setMargin (insets);
    jBStart.setFocusPainted (false);
    jPSouth.add (jBStart);
    jBStop.addActionListener (this);
    jBStop.setMargin (insets);
    jBStop.setFocusPainted (false);
    jPSouth.add (jBStop);
    jBAbort.addActionListener (this);
    jBAbort.setMargin (insets);
    jBAbort.setFocusPainted (false);
    jPSouth.add (jBAbort);
    jBShow.addActionListener (this);
    jBShow.setMargin (insets);
    jBShow.setFocusPainted (false);
    jPSouth.add (jBShow);
    jBExit.addActionListener (this);
    jBExit.setMargin (insets);
    jBExit.setFocusPainted (false);
    jPSouth.add (jBExit);
    c.add (jPSouth);

    //the graphic step-window
    scrollPane = new JScrollPane ();
    scrollPane.setViewportView (chain);
    scrollPane.getVerticalScrollBar ().setUnitIncrement (10);
    jChain.getContentPane ().add (scrollPane, "Center");
    jChain.setSize (330, 450);
    jChain.setLocation (100, 100);
    jChain.setResizable (false);
    jChain.setIconImage (CControlCenter.getIcon ());
    jChain.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
    for (Enumeration en = chain.getSteps (); en.hasMoreElements ();){
      CStep step = (CStep) en.nextElement ();
      step.removeMouseListener(mma);
      step.addMouseListener (mma);
    }
    chain.getFirstStep().setLocation (130, 10);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    sName = chain.getName ();
    jChain.setTitle (sName + ": JTrain - Stepfläche");
    this.setTitle (sName + ": JTrain Automatikfahrt");

  }

  public final JScrollPane getScrollPane (){return scrollPane;}

  public void stopLoco (){
    loco.setSpeed (0);
    loco.setDirection (2);
    loco.sendLcString();
    int iFF = loco.getF1() + loco.getF2() << 1
      + loco.getF3() << 2 + loco.getF4() << 3;
    CLocoEventHandler.fireEvent (new CLocoEvent
    (this, loco.getAddr (), loco.getDirection (), loco.getSpeed (),
    loco.getSpeedsteps(), loco.getFunc(), loco.getNrOF (), iFF));
    loco.setLocoBusy (false);
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jBStart){
      if (chainExec == null){
        chainExec = new CChainExec (this, chain, parent);
        thread = new Thread (chainExec);
        thread.start () ;
      }
      else if (thread == null){
        thread = new Thread (chainExec);
        thread.start ();
      }
      else{
        thread = null;
        thread = new Thread (chainExec);
        thread.start ();
      }
      chain.setStop (false);
      jBStop.setBackground (Color.lightGray);
    }
    else if (o == jBAbort){
      chainExec.setState (CChainExec.NOTRUNNING);
      if (loco != null) {
        stopLoco ();
      }
    }
    else if (o == jBStop){
      chain.setStop (true);
      jBStop.setBackground (Color.yellow);
    }
    else if (o == jBShow){
      if (!jChain.isShowing ()){
        jChain.show ();
        jBShow.setText ("Weg!");
      }
      else{
        jChain.hide ();
        jBShow.setText ("Zeige");
      }
    }
    else if (o == jBExit){
      cleanUp ();
    }
  }

  public final void cleanUp (){

    for (Enumeration en = chain.getSteps(); en.hasMoreElements ();){
      CStep tStep = (CStep) en.nextElement ();
      if (tStep != null){
        tStep.removeMouseListener (mma);
        tStep.setBackground (Color.white);
      }
    }
    if (loco != null) stopLoco ();
    jChain.setVisible (false);
    jChain.dispose ();
    setVisible (false);
    dispose ();
  }

  class MyMouseAdapter extends MouseAdapter {
    public void mousePressed (MouseEvent e){
      Object o = e.getSource ();
      if (o instanceof CStep){
        chain.showPropWindow ((CStep)o);
      }
    }
  }
  
}

