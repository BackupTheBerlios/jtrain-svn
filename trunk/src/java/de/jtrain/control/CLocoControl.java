/*
this class contains the loco controller for handy usage;
also displays the "loco-news" of the locolistener
last modified: 2002 05 09
author: Werner Kunkel
*/

package de.jtrain.control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.event.CMyKeyAdapter;
import de.jtrain.main.JTrain;

class CLocoControl extends JFrame
  implements ActionListener, ItemListener, ChangeListener, CLocoListener {

  private CLoco loco                  = null;
  private String sHead                = new String ("Lok-Controller");
  private JSlider jSl                 = null;
  private JButton jB1                 = new JButton ("-- vor->");
  private JButton jB2                 = new JButton ("<-rück--");
  private JButton jB3                 = new JButton ("STOP");
  private JButton jBF                 = new JButton ("F");
  private JButton jBF1                = new JButton ("F1");
  private JButton jBF2                = new JButton ("F2");
  private JButton jBF3                = new JButton ("F3");
  private JButton jBF4                = new JButton ("F4");
  private JPanel jPanel               = new JPanel ();
  private GridLayout gl               = new GridLayout (5,1,5,5);
  private GridLayout gl2              = new GridLayout (1,5,5,5);
  private GridLayout gl3              = new GridLayout (1,3,20,20);
  private JPanel jPanSouth            = new JPanel ();
  private JPanel jPanFGroup           = new JPanel ();
  private JPanel jPanCbGroup          = new JPanel ();
  private JTextField jTf              = new JTextField (4);
  private CheckboxGroup cbg           = new CheckboxGroup ();
  private Checkbox cBPercent          = new Checkbox ("in Prozent", cbg, false );
  private Checkbox cBKmh              = new Checkbox ("in km/h", cbg, true );
  private Checkbox cBStep             = new Checkbox ("in Stufen", cbg, false );
  private Font font                   = new Font ("SansSerif",1,30);
  private String sName                = null;
  private String sProtocoll           = null;
  private int iVMax;
  private int iSpeedsteps;
  private int iAddr;
  private int iDirection              = 1;
  private int iSpeed;
  private int iFunc;
  private int iNrOF;
  private int iF1;
  private int iF2;
  private int iF3;
  private int iF4;
// the values deriving from locoevents, used for displaying
  private int i2Direction              = 1;
  private int i2Speed;
  private int i2Vmax;
  private int i2Func;
  private int i2NrOF;
  private int i2F1;
  private int i2F2;
  private int i2F3;
  private int i2F4;
  private boolean bLocoBusy = false;
  private CLocoAccClock lac = null;

  CLocoControl (CLoco loco){
    super ();
    this.loco = loco;
    if (loco.getAccByJBahn () && !loco.getLocoBusy())lac = new CLocoAccClock (loco);
    this.setResizable (false);
    this.setSize (300, 300);
    this.setLocation (400, 55);
    this.setIconImage (CControlCenter.getIcon ());
    this.getContentPane().setBackground (Color.blue);
    sName = loco.getName();
    this.setTitle (sName + " " + sHead);
    sProtocoll = loco.getProtocoll ();
    iVMax = loco.getMaxSpeed();
    iSpeedsteps = loco.getSpeedsteps();
    iAddr = loco.getAddr();
    iNrOF = loco.getNrOF();
    jPanel.setLayout (gl);
    jPanFGroup.setLayout (gl2);
    jPanSouth.setLayout (gl3);
    jTf.setBackground (Color.white);
    jTf.setFont (font);
    jTf.setEditable (false);
    jTf.setHorizontalAlignment (SwingConstants.CENTER);
    jPanel.add (jTf);
    if (!loco.getLocoBusy()){
      this.setLocation (1, 55);
      jSl = new JSlider (SwingConstants.HORIZONTAL, 0, iSpeedsteps, 0);
      jSl.setPaintTicks (true);
      jSl.setPaintLabels (true);
      jSl.setMajorTickSpacing (5);
      jSl.setMinorTickSpacing (1);
      jSl.setSnapToTicks (true);
      jSl.addChangeListener (this);
      jPanel.add (jSl);
      jBF.setBackground (Color.gray);
      jBF.addActionListener (this);
      if (iNrOF >= 1){
        jBF1.setBackground (Color.gray);
        jBF1.addActionListener (this);
      }
      if (iNrOF >= 2){
        jBF2.setBackground (Color.gray);
        jBF2.addActionListener (this);
      }
      if (iNrOF >= 3){
        jBF3.setBackground (Color.gray);
        jBF3.addActionListener (this);
      }
      if (iNrOF >= 4){
        jBF4.setBackground (Color.gray);
        jBF4.addActionListener (this);
      }
      jB1.setBackground (Color.cyan);
      jB1.addActionListener (this);
      jB2.setBackground (Color.gray);
      jB2.addActionListener (this);
      jB3.addActionListener (this);
      jB3.setBackground (Color.red);
      jPanFGroup.add (jBF);
      jPanFGroup.add (jBF1);
      jPanFGroup.add (jBF2);
      jPanFGroup.add (jBF3);
      jPanFGroup.add (jBF4);
      jPanel.add (jPanFGroup);
      jPanSouth.add (jB2);
      jPanSouth.add (jB3);
      jPanSouth.add (jB1);
      bLocoBusy = true;
    }
    else{
      this.setSize(300, 150);
      jPanel.setLayout (new GridLayout (2, 1, 0, 0));
    }
    cBPercent.addItemListener (this);
    jPanCbGroup.add (cBPercent);
    cBKmh.addItemListener (this);
    jPanCbGroup.add (cBKmh);
    cBStep.addItemListener (this);
    jPanCbGroup.add (cBStep);
    jPanel.add (jPanCbGroup);
    if (loco.getLocoBusy ()== false) jPanel.add (jPanSouth);
    loco.setLocoBusy (true);
    for (int i = 0; i < jPanFGroup.getComponentCount (); i++){
      Component c = jPanFGroup.getComponent (i);
      if (c instanceof JButton){
        ((JButton)c).setMargin (new Insets (0,0,0,0));
        ((JButton)c).setFocusPainted (false);
      }
    }
    for (int i = 0; i < jPanSouth.getComponentCount (); i++){
      Component c = jPanSouth.getComponent (i);
      if (c instanceof JButton){
        ((JButton)c).setMargin (new Insets (0,0,0,0));
        ((JButton)c).setFocusPainted (false);
      }
    }
    this.getContentPane ().add (jPanel);
    this.addKeyListener (new CMyKeyAdapter ());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new LcWindowAdapter ());
    CLocoEventHandler.getLocoEventHandler ().addLocoListener (this);
    loco.setDirection (1);
  }

  private final void setTfText (){
    String sDir = null;
    String sF = null;
    String sF1 = null;
    String sF2 = null;
    String sF3 = null;
    String sF4 = null;
    String sExt = null;
    String sSpd = null;
    String sSub = null;
    if (i2Direction == 1)
      sDir = ">>";
    else if (i2Direction == 0)
      sDir = "<<";
    else if (i2Direction == 2)
      sDir = "!!";
    if (i2Func == 1) sSub = "  F";
    else sSub = "";

    Checkbox choice = cbg.getSelectedCheckbox ();
    if (choice.equals (cBPercent)){
      sExt = " %";
      sSpd = "" + (int)(i2Speed * 100.0 / i2Vmax);
    }
    if (choice.equals (cBKmh)){
      sExt = " km/h";
      sSpd = "" + (int)(i2Speed * 1.0 / i2Vmax * iVMax);
    }
    if (choice.equals (cBStep)){
      sExt = "/" + i2Vmax;
      sSpd = "" + i2Speed;
    }
    jTf.setText (sDir + "  " + sSpd + sExt + "  " + sDir + sSub);
  }

  public void actionPerformed (ActionEvent e){
    if (e.getSource() == jB1){
      iDirection = 1;
      loco.setDirection (iDirection);
      jSl.setValue(0);
      jSl.setEnabled (true);
      loco.setSpeed (0);
      if (loco.getAccByJBahn () == true && lac != null)
        lac.setEndSpeed (0);
      loco.sendLcString ();
      jB1.setBackground (Color.cyan);
      jB2.setBackground (Color.gray);
    }
    if (e.getSource() == jB2){
      iDirection = 0;
      loco.setDirection (iDirection);
      jSl.setValue(0);
      jSl.setEnabled (true);
      loco.setSpeed (0);
      if (loco.getAccByJBahn () == true && lac != null)
        lac.setEndSpeed (0);
      loco.sendLcString ();
      jB2.setBackground (Color.cyan);
      jB1.setBackground (Color.gray);
    }
    if (e.getSource() == jB3){
      iDirection = 2;
      loco.setDirection (iDirection);
      jSl.setValue(0);
      jSl.setEnabled (false);
      iSpeed = 0;
      loco.setSpeed (0);
      if (loco.getAccByJBahn () == true && lac != null)
        lac.setEndSpeed (0);
      loco.sendLcString ();
      jB1.setBackground (Color.gray);
      jB2.setBackground (Color.gray);
    }
    if (e.getActionCommand ().equals("F")){
      if (iFunc == 0){
        iFunc = 1;
        loco.setFunc (iFunc);
        ((JButton)e.getSource()).setBackground(Color.cyan);
      }
      else if (iFunc == 1){
        iFunc = 0;
        loco.setFunc (iFunc);
        ((JButton)e.getSource()).setBackground(Color.gray);
      }
      setTfText ();
      loco.sendLcString ();
    }
    if (e.getActionCommand ().equals ("F1")){
      if (iF1 == 0){
        iF1 = 1;
        loco.setF1 (iF1);
        ((JButton)e.getSource()).setBackground(Color.cyan);
      }
      else if (iF1 == 1){
        iF1 = 0;
        loco.setF1 (iF1);
        ((JButton)e.getSource()).setBackground(Color.gray);
      }
      setTfText ();
      loco.sendLcString ();
    }
    if (e.getActionCommand ().equals ("F2")){
      if (iF2 == 0){
        iF2 = 1;
        loco.setF2 (iF2);
        ((JButton)e.getSource()).setBackground(Color.cyan);
      }
      else if (iF2 == 1){
        iF2 = 0;
        loco.setF2 (iF2);
        ((JButton)e.getSource()).setBackground(Color.gray);
      }
      setTfText ();
      loco.sendLcString ();
    }
    if (e.getActionCommand ().equals("F3")){
      if (iF3 == 0){
        iF3 = 1;
        loco.setF3 (iF3);
        ((JButton)e.getSource()).setBackground(Color.cyan);
      }
      else if (iF3 == 1){
        iF3 = 0;
        loco.setF3 (iF3);
        ((JButton)e.getSource()).setBackground(Color.gray);
      }
      setTfText ();
      loco.sendLcString ();
    }
    if (e.getActionCommand ().equals ("F4")){
      if (iF4 == 0){
        iF4 = 1;
        loco.setF4 (iF4);
        ((JButton)e.getSource()).setBackground(Color.cyan);
      }
      else if (iF4 == 1){
        iF4 = 0;
        loco.setF4 (iF4);
        ((JButton)e.getSource()).setBackground(Color.gray);
      }
      setTfText ();
      loco.sendLcString ();
    }
  }

  public void stateChanged (ChangeEvent e){

    JSlider sl = (JSlider)e.getSource ();
    iSpeed = sl.getValue ();
    if (loco.getDirection() != 2){
      if (loco.getAccByJBahn () == true){
        lac.setEndSpeed (iSpeed);
        if (lac.getIState () == lac.NOTRUNNING && loco.getSpeed() != iSpeed){
          lac.start ();
        }
        if (!lac.isAlive())lac = new CLocoAccClock (loco);
      }
      else if (loco.getAccByJBahn () == false) {
        loco.setSpeed (iSpeed);
        loco.sendLcString ();
      }
    }
  }

  public void itemStateChanged (ItemEvent e){
    setTfText ();
  }

  public void handleEvent (CLocoEvent e){
    if (e.getAddr () == iAddr){
      i2Direction = e.getDirection ();
      i2Speed = e.getSpeed ();
      i2Vmax = e.getVmax ();
      i2Func = e.getFunc ();
      i2NrOF = e.getNrOF ();
      i2F1 = e.getFF () & 01;
      i2F2 = e.getFF () >> 1 & 01;
      i2F3 = e.getFF () >> 2 & 01;
      i2F4 = e.getFF () >> 3 & 01;
      setTfText ();
    }
  }

  public final void cleanUp (){
    CLocoEventHandler.getLocoEventHandler ().removeLocoListener (this);
    JTrain.mf.setControlmode (false);
    if ( bLocoBusy == true) loco.setLocoBusy (false);
  }

  class LcWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      cleanUp ();
    }
  }

}

