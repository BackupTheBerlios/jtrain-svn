/* Class enables editing the data of one loco
last changes : 2002 05 01
Author : Werner Kunkel
*/

package de.jtrain.control;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;

import de.jtrain.com.CControlCenter;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;

public class CLocoEdit extends JFrame
  implements ActionListener, ItemListener, KeyListener {

  private String title;
  private CLoco loco                  = null;
  private BorderLayout bLay           = new BorderLayout (10, 10);
  private GridLayout gLay             = new GridLayout (10 , 2, 5, 5);
  private JPanel jPan                 = new JPanel ();
  private JPanel jPanEdit             = new JPanel ();
  private JLabel jLabName             = new JLabel ("Bezeichnung");
  private JTextField jTextName        = null;
  private JLabel jLabProtocoll        = new JLabel ("Protokoll");
  private JTextField jTextProtocoll   = null;
  private JLabel jLabAddr             = new JLabel ("Digitaladresse");
  private JTextField jTextAddr        = null;
  private JLabel jLabSpeedsteps       = new JLabel ("Anzahl der Fahrstufen");
  private JTextField jTextSpeedsteps  = null;
  private JLabel jLabNrOF             = new JLabel ("Anzahl der Funktionen");
  private JTextField jTextNrOF        = null;
  private JLabel jLabAccByJBahn       = new JLabel ("Beschleunigung durch JTrain");
  private Checkbox cBox               = new Checkbox ("ja");
  private JLabel jLabAccTime          = new JLabel ("Beschleunigung in ms/step");
  private JTextField jTextAccTime     = null;
  private JLabel jLabDecTime          = new JLabel ("Verzögerung in ms/step");
  private JTextField jTextDecTime     = null;
  private JLabel jLabMaxSpeed         = new JLabel ("Höchsgeschw. des Originals");
  private JTextField jTextMaxSpeed    = null;
  private JLabel jLabBusNumber        = new JLabel ("SRCPD Bus Nummer");
  private JTextField jTextBusNumber;
  private JLabel jLabLeft             = new JLabel ("   ");
  private JLabel jLabRight            = new JLabel ("   ");
  private JLabel jLabNorth            = new JLabel ("   ");
  private JPanel jPanSouth            = new JPanel ();
  private JButton jButHelp            = new JButton ("Hilfe");
  private JButton jButCancel          = new JButton ("Abbrechen");
  private JButton jButSave            = new JButton ("Speichern");
  private boolean [] bErrField        = new boolean [9];

  CLocoEdit (String title, CLoco loco){
    super (title);
    this.loco = loco;
    loco.setLocoBusy (true);
    this.setSize (400,300);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    this.setIconImage (CControlCenter.getIcon ());
    this.addKeyListener (this);
    jPan.setLayout (bLay);
    jPanEdit.setLayout (gLay);
    jPanEdit.add (jLabName);
    jTextName = new JTextField (loco.getName ());
    jTextName.addActionListener (this);
    jPanEdit.add (jTextName);
    jLabProtocoll.setToolTipText ("L,M1,M2,M3,M4,M5,N1,N2,N3,N4,S,PS");
    jPanEdit.add (jLabProtocoll);
    jTextProtocoll = new JTextField (loco.getProtocoll ());
    jTextProtocoll.addActionListener (this);
    jPanEdit.add (jTextProtocoll);
    jPanEdit.add (jLabAddr);
    jTextAddr = new JTextField (Integer.toString (loco.getAddr ()));
    jTextAddr.addActionListener (this);
    jPanEdit.add (jTextAddr);
    jPanEdit.add (jLabSpeedsteps);
    jTextSpeedsteps = new JTextField (Integer.toString (loco.getSpeedsteps ()));
    jTextSpeedsteps.addActionListener (this);
    jPanEdit.add (jTextSpeedsteps);
    jPanEdit.add (jLabNrOF);
    jTextNrOF = new JTextField (Integer.toString (loco.getNrOF ()));
    jTextNrOF.addActionListener (this);
    jPanEdit.add (jTextNrOF);
    jPanEdit.add (jLabAccByJBahn);
    cBox.setState (loco.getAccByJBahn ());
    cBox.addItemListener (this);
    jPanEdit.add (cBox);
    jPanEdit.add (jLabAccTime);
    jTextAccTime = new JTextField (Integer.toString (loco.getAccTime ()));
    jTextAccTime.addActionListener (this);
    if (cBox.getState () == true){
      jTextAccTime.setBackground (Color.white);
      jTextAccTime.setEditable (true);
    }
    else if (cBox.getState () == false){
      jTextAccTime.setBackground (Color.lightGray);
      jTextAccTime.setEditable (false);
    }
    jPanEdit.add (jTextAccTime);
    jPanEdit.add (jLabDecTime);
    jTextDecTime = new JTextField (Integer.toString (loco.getDecTime ()));
    jTextDecTime.addActionListener (this);
    if (cBox.getState () == true){
      jTextDecTime.setBackground (Color.white);
      jTextDecTime.setEditable (true);
    }
    else if (cBox.getState () == false){
      jTextDecTime.setBackground (Color.lightGray);
      jTextDecTime.setEditable (false);
    }
    jPanEdit.add (jTextDecTime);
    jPanEdit.add (jLabMaxSpeed);
    jTextMaxSpeed = new JTextField (Integer.toString (loco.getMaxSpeed ()));
    jTextMaxSpeed.addActionListener (this);
    jPanEdit.add (jTextMaxSpeed);
    jPanEdit.add (jLabBusNumber);
    jTextBusNumber = new JTextField (loco.getBusNumber());
    jTextBusNumber.addActionListener (this);
    jPanEdit.add (jTextBusNumber);
    jPan.add (jPanEdit, BorderLayout.CENTER);
    jPan.add (jLabLeft, BorderLayout.WEST);
    jPan.add (jLabRight, BorderLayout.EAST);
    jPan.add (jLabNorth, BorderLayout.NORTH);
    jButHelp.setMnemonic ('h');
    jButHelp.addActionListener (this);
    jPanSouth.add (jButHelp);
    jButCancel.setMnemonic ('a');
    jButCancel.addActionListener (this);
    jPanSouth.add (jButCancel);
    jButSave.setMnemonic ('s');
    jButSave.addActionListener (this);
    jPanSouth.add (jButSave);
    jPan.add (jPanSouth, BorderLayout.SOUTH);
    this.getContentPane ().add (jPan);
    if (jTextName.getText ().equals (""))bErrField [0] = true;
    if (jTextProtocoll.getText ().equals (""))bErrField [1] = true;
    if (jTextAddr.getText ().equals (""))bErrField [2] = true;
    if (jTextSpeedsteps.getText ().equals (""))bErrField [3] = true;
    if (jTextNrOF.getText ().equals (""))bErrField [4] = true;
    bErrField [5] = false;
    if (cBox.getState() == true && jTextAccTime.getText ().
      equals (""))bErrField [6] = true;
    else if (cBox.getState() == false) bErrField [6] = false;
    if (cBox.getState() == true && jTextDecTime.getText ().
      equals (""))bErrField [7] = true;
    else if (cBox.getState() == false) bErrField [7] = false;
    if (jTextMaxSpeed.getText ().equals (""))bErrField [8] = true;
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addWindowListener (new MyWindowAdapter ());
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_32.htm#lokedit"));
    hlp.hyperlinkUpdate (he);
  }

  public void actionPerformed (ActionEvent e){
    if (e.getSource () == jTextName){
      if (jTextName.getText ().equals ("")) bErrField [0] = true;
      else {
        bErrField [0] = false;
        loco.setName (jTextName.getText ());
        jTextProtocoll.requestFocus();
      }
    }
    else if (e.getSource () == jTextProtocoll){
      setLocoProtocoll (jTextProtocoll.getText().toUpperCase());
      jTextProtocoll.setBackground (Color.white);
    }
    else if (e.getSource () == jTextAddr){
      int i = 0;
      try{
        i = Integer.parseInt (jTextAddr.getText());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextAddr.setText("");
        bErrField [2] = true;
      }
      else{
        loco.setAddr (i);
        bErrField [2] = false;
        jTextAddr.setBackground (Color.white);
        if (jTextSpeedsteps.isEditable() == true)
          jTextSpeedsteps.requestFocus();
        else jTextNrOF.requestFocus();
      }
    }
    else if (e.getSource () == jTextSpeedsteps){
      setLocoSpeedsteps (jTextSpeedsteps.getText());
      jTextSpeedsteps.setBackground (Color.white);
    }
    else if (e.getSource () == jTextNrOF){
      int i = 0;
      try{
        i = Integer.parseInt (jTextNrOF.getText());
      }
      catch (Exception ex){}
      if (i < 0){
        jTextNrOF.setText("");
        bErrField [4] = true;
      }
      else{
         loco.setNrOF (i);
         bErrField [4] = false;
         jTextNrOF.setBackground (Color.white);
         jTextMaxSpeed.requestFocus();
      }
    }
    else if (e.getSource () == jTextAccTime){
      int i = 0;
      try{
        i = Integer.parseInt (jTextAccTime.getText ());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextAccTime.setText ("");
        bErrField [6] = true;
      }
      else{
         loco.setAccTime (i);
         bErrField [6] = false;
         jTextAccTime.setBackground (Color.white);
         jTextDecTime.requestFocus ();
      }
    }
    else if (e.getSource() == jTextDecTime){
      int i = 0;
      try{
        i = Integer.parseInt (jTextDecTime.getText ());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextDecTime.setText ("");
        bErrField [7] = true;
      }
      else{
         loco.setDecTime (i);
         bErrField [7] = false;
         jTextDecTime.setBackground (Color.white);
         jTextMaxSpeed.requestFocus ();
      }
    }
    else if (e.getSource() == jTextMaxSpeed){
      int i = 0;
      try{
        i = Integer.parseInt (jTextMaxSpeed.getText ());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextMaxSpeed.setText ("");
        bErrField [8] = true;
      }
      else{
         loco.setMaxSpeed (i);
         bErrField [8] = false;
         jTextMaxSpeed.setBackground (Color.white);
         jTextBusNumber.requestFocus ();
      }
    }
    else if (e.getSource () == jTextBusNumber)
    {
      loco.setBusNumber(jTextBusNumber.getText());
      jTextBusNumber.setBackground (Color.white);
      jButSave.requestFocus ();
    }
    else if (e.getSource() == jButHelp){
      try{
        showHelp ();
      }
      catch (MalformedURLException ex){}
    }
    else if (e.getSource () == jButCancel) {
      JTrain.mf.setEditmode (false);
      loco.setLocoBusy (false);
      this.setVisible (false);
      dispose ();
    }
    else if (e.getSource () == jButSave){
      readAll ();
      if (errorCheck () == true){
        saveLoco ();
        JTrain.mf.setEditmode (false);
      }
    }
  }

  public void itemStateChanged (ItemEvent e){
    if (((Checkbox)e.getSource ()).getState () == true){
      bErrField [6] = true;
      bErrField [7] = true;
      loco.setAccByJBahn (true);
      jTextAccTime.setEditable (true);
      jTextAccTime.setText ("");
      jTextAccTime.setBackground (Color.white);
      jTextAccTime.requestFocus ();
      jTextDecTime.setEditable (true);
      jTextDecTime.setText ("");
      jTextDecTime.setBackground (Color.white);
    }
    else if (((Checkbox)e.getSource ()).getState () == false){
      bErrField [6] = false;
      bErrField [7] = false;
      loco.setAccByJBahn (true);
      jTextAccTime.setEditable (false);
      jTextAccTime.setText ("");
      jTextAccTime.setBackground (Color.lightGray);
      jTextDecTime.setEditable (false);
      jTextDecTime.setText ("");
      jTextDecTime.setBackground (Color.lightGray);
    }
  }

  private final void setLocoProtocoll (String s){
    if (!s.equalsIgnoreCase ("L") && !s.equalsIgnoreCase ("M1")
    && !s.equalsIgnoreCase ("M2") && !s.equalsIgnoreCase ("M3")
    && !s.equalsIgnoreCase ("M4") && !s.equalsIgnoreCase ("M5")
    && !s.equalsIgnoreCase ("MF") && !s.equalsIgnoreCase ("NB")
    && !s.equalsIgnoreCase ("N1") && !s.equalsIgnoreCase ("N2")
    && !s.equalsIgnoreCase ("N3") && !s.equalsIgnoreCase ("N4")
    && !s.equalsIgnoreCase ("S")  &&! s.equalsIgnoreCase ("PS")) {
      loco.setProtocoll ("");
      jTextProtocoll.setText("");
      bErrField [1] = true;
      jTextSpeedsteps.setText ("");
      jTextSpeedsteps.setEditable (true);
      jTextSpeedsteps.setBackground (Color.white);
    }
    else{
      if (s.equalsIgnoreCase ("M1") || s.equalsIgnoreCase ("M2")
       || s.equalsIgnoreCase ("M4") || s.equalsIgnoreCase ("NB")){
        jTextSpeedsteps.setText ("14");
        setLocoSpeedsteps ("14");
        jTextSpeedsteps.setEditable (false);
        jTextSpeedsteps.setBackground (Color.lightGray);
      }
      if (s.equalsIgnoreCase ("M5")){
        jTextSpeedsteps.setText ("27");
        setLocoSpeedsteps ("27");
        jTextSpeedsteps.setEditable (false);
        jTextSpeedsteps.setBackground (Color.lightGray);
      }
      if (s.equalsIgnoreCase ("M3") || s.equalsIgnoreCase("N1")
       || s.equalsIgnoreCase("N3")){
        jTextSpeedsteps.setText ("28");
        setLocoSpeedsteps ("28");
        jTextSpeedsteps.setEditable (false);
        jTextSpeedsteps.setBackground (Color.lightGray);
      }
      if (s.equalsIgnoreCase ("N2") || s.equalsIgnoreCase ("N4")){
        jTextSpeedsteps.setText ("128");
        setLocoSpeedsteps ("128");
        jTextSpeedsteps.setEditable (false);
        jTextSpeedsteps.setBackground (Color.lightGray);
      }
      if (s.equalsIgnoreCase ("S")){
        jTextSpeedsteps.setText ("31");
        setLocoSpeedsteps ("31");
        jTextSpeedsteps.setEditable (false);
        jTextSpeedsteps.setBackground (Color.lightGray);
      }
      loco.setProtocoll (s);
      bErrField [1] = false;
      jTextAddr.requestFocus();
    }
  }

  private final void setLocoSpeedsteps (String s){
    int i = 0;
    try{
      i = Integer.parseInt (s);
    }
    catch (Exception ex){}
    if (i <= 0){
      jTextSpeedsteps.setText("");
      bErrField [3] = true;
    }
    else{
      loco.setSpeedsteps (i);
      bErrField [3] = false;
      jTextNrOF.requestFocus();
    }
  }

  //this is needed to catch everything in the JTextFields even without "Enter"
  private final void readAll (){
      if (jTextName.getText ().equals ("")) bErrField [0] = true;
      else {
        bErrField [0] = false;
        loco.setName (jTextName.getText ());
        jTextName.setBackground (Color.white);
        jTextProtocoll.requestFocus();
      }
      setLocoProtocoll (jTextProtocoll.getText().toUpperCase());
      int i = 0;
      try{
        i = Integer.parseInt (jTextAddr.getText());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextAddr.setText("");
        bErrField [2] = true;
      }
      else{
        loco.setAddr (i);
        bErrField [2] = false;
      }
      setLocoSpeedsteps (jTextSpeedsteps.getText());
      try{
        i = Integer.parseInt (jTextNrOF.getText());
      }
      catch (Exception ex){}
      if (i < 0){
        jTextNrOF.setText("");
        bErrField [4] = true;
      }
      else{
         loco.setNrOF (i);
         bErrField [4] = false;
      }
      if (loco.getAccByJBahn () == true){
        i = 0;
        try{
          i = Integer.parseInt (jTextAccTime.getText ());
        }
        catch (Exception ex){}
        if (i <= 0){
          jTextAccTime.setText ("");
          bErrField [6] = true;
        }
        else{
          loco.setAccTime (i);
          bErrField [6] = false;
        }
        i = 0;
        try{
          i = Integer.parseInt (jTextDecTime.getText ());
        }
        catch (Exception ex){}
        if (i <= 0){
          jTextDecTime.setText ("");
          bErrField [7] = true;
        }
        else{
          loco.setDecTime (i);
          bErrField [7] = false;
        }
      }
      i = 0;
      try{
        i = Integer.parseInt (jTextMaxSpeed.getText());
      }
      catch (Exception ex){}
      if (i <= 0){
        jTextMaxSpeed.setText("");
        bErrField [8] = true;
      }
      else{
        loco.setMaxSpeed (i);
        bErrField [8] = false;
      }
      loco.setBusNumber(jTextBusNumber.getText());
  }


  private final boolean errorCheck (){
    boolean bOk = true;
    for (int i = 0; i < 9; i++){
      if (bErrField [i] == true){
        JOptionPane.showMessageDialog(
          this,
         "Fehlerhafte oder fehlende Angabe !",
          "Warnung",
          JOptionPane.INFORMATION_MESSAGE);
        bOk = false;
        if (i == 0){
          jTextName.setBackground (Color.yellow);
          jTextName.requestFocus ();
          break;
        }
        if (i == 1){
          jTextProtocoll.setText ("");
          jTextProtocoll.setBackground (Color.yellow);
          jTextProtocoll.requestFocus ();
          break;
        }
        if (i == 2){
          jTextAddr.setText ("");
          jTextAddr.setBackground (Color.yellow);
          jTextAddr.requestFocus ();
          break;
        }
        if (i == 3){
          jTextSpeedsteps.setText ("");
          jTextSpeedsteps.setBackground (Color.yellow);
          jTextSpeedsteps.requestFocus ();
          break;
        }
        if (i == 4){
          jTextNrOF.setText ("");
          jTextNrOF.setBackground (Color.yellow);
          jTextNrOF.requestFocus ();
          break;
        }
        if (i == 6){
          jTextAccTime.setText ("");
          jTextAccTime.setBackground (Color.yellow);
          jTextAccTime.requestFocus ();
          break;
        }
        if (i == 7){
          jTextDecTime.setText ("");
          jTextDecTime.setBackground (Color.yellow);
          jTextDecTime.requestFocus ();
        }
        if (i == 8){
          jTextMaxSpeed.setText ("");
          jTextMaxSpeed.setBackground (Color.yellow);
          jTextMaxSpeed.requestFocus ();
          break;
        }
      }
    }
    return bOk;
  }

  private final void saveLoco (){
    if (!CControlCenter.getLocoList ().contains (loco))
      CControlCenter.getLocoList ().add (loco);
    CControlCenter.saveLocoList (CControlCenter.getLocoList ());
    loco.setLocoBusy (false);
    setVisible (false);
    dispose ();
    return;
  }

  //the following trick helps setting the focus;
  //this doesn`t work in the constructor
  public void keyPressed (KeyEvent e){
    if (jTextName.getText().equals("")){
      jTextProtocoll.requestFocus();
      jTextName.requestFocus();
    }
  }
  public void keyTyped (KeyEvent e){}
  public void keyReleased (KeyEvent e){}

  private class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      JTrain.mf.setEditmode (false);
      loco.setLocoBusy (false);
    }
  }

}
