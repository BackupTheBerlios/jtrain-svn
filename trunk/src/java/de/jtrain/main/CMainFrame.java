/*
This class defines the main window controlling all other windows

last modified: 2004 10 15
author: Werner Kunkel, Guido Scholz
*/

package de.jtrain.main;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import de.jtrain.block.CBlockEventHandler;
import de.jtrain.chain.CChainEditorDialog;
import de.jtrain.com.CCommand;
import de.jtrain.com.CCommandPipe;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFeedbackportHandler;
import de.jtrain.com.CInfoportHandler;
import de.jtrain.com.CS88Test;
import de.jtrain.control.CLoco;
import de.jtrain.control.CLocoControlDialog;
import de.jtrain.control.CLocoDialog;
import de.jtrain.editor.CPropertyEditor;
import de.jtrain.event.CMyKeyAdapter;
import de.jtrain.gb.CGb;
import de.jtrain.gb.CGbBlockEditor;
import de.jtrain.gb.CGbDialog;
import de.jtrain.gb.CGbExec;
import de.jtrain.gb.CGbStreetEditor;
import de.jtrain.help.CHelp;
import de.jtrain.srcp.Command;
import de.jtrain.srcp.SRCP;
import de.jtrain.turnout.CTurnoutEditor;
import de.jtrain.turnout.CTurnoutHandler;
import de.jtrain.turnout.CTurnoutTest;
import de.jtrain.turnout.CTurnoutTestDialog;

public class CMainFrame extends JFrame implements ActionListener {

  private JMenuBar mbar    = new JMenuBar ();
  private JMenu dat        = new JMenu ("Datei");
  private JMenuItem nloco  = new JMenuItem ("Neuer Lokregler", 'l');
  private JMenuItem ngb    = new JMenuItem ("Gleisbild...", 'g');
  private JMenuItem end    = new JMenuItem ("Beenden", 'e');
  private JMenu edit       = new JMenu ("Editoren");
  private JMenuItem sol    = new JMenuItem ("Weichen und Signale",'w');
  private JMenuItem loco   = new JMenuItem ("Lokomotiven", 'l');
  private JMenuItem gb     = new JMenuItem ("Gleisbild", 'g');
  private JMenuItem block  = new JMenuItem ("Blockstrecken", 'b');
  private JMenuItem street = new JMenuItem ("Fahrstrassen", 'f');
  private JMenuItem auto   = new JMenuItem ("Automatikfahrt", 'a');
  private JMenuItem prop   = new JMenuItem ("Einstellungen", 'e');
  private JMenu dae        = new JMenu ("Daemon");
  private JMenuItem stat   = new JMenuItem ("Serverinfo", 's');
  private JMenuItem pow    = new JMenuItem ("Fahrstrom ein/aus", 'f');
  private JMenu test       = new JMenu ("Testprogramme");
  private JMenuItem allsol = new JMenuItem ("Alle Magnetartikel testen", 'a');
  private JMenuItem onesol = new JMenuItem ("Einen Magnetartikel testen", 'e');
  private JMenuItem s88    = new JMenuItem ("S88 - Tester", 's');
  private JMenu info       = new JMenu ("Info");
  private JMenuItem about  = new JMenuItem ("Über", 'b');
  private JMenuItem help   = new JMenuItem ("Hilfe", 'h');
  private JTextArea jTa           = null;
  private CFeedbackportHandler fh = null;
  private CTurnoutHandler th      = null;
  private CBlockEventHandler beh  = null;
  private CInfoportHandler ih     = null;
  private CCommandPipe cp         = null;
  private Image icon              = null;
  private boolean bPowerOn     = true;
  private boolean bEditmode    = false;
  private boolean bControlmode = false;
  private int iEditor          = 0;
  private int iController      = 0;

//constructor
  CMainFrame (String sTitle)
  {
    super (sTitle);
    this.setResizable (true);
    this.setSize (new Dimension(600, 60));
    /*this.setLocation (1, 1);*/
    getContentPane().setBackground(Color.magenta);
    this.setIconImage (CControlCenter.getIcon ());
    this.setJMenuBar (mbar);
    mbar.add (dat);
    dat.setMnemonic ('D');
    setCtrlAccelerator (nloco, 'L');
    dat.add (nloco);
    nloco.addActionListener (this);
    dat.add (ngb);
    setCtrlAccelerator (ngb, 'G');
    ngb.addActionListener (this);
    dat.addSeparator();
    dat.add (end);
    setCtrlAccelerator (end, 'Q');
    end.addActionListener (this);
    mbar.add (edit);
    edit.setMnemonic ('E');
    edit.add (sol);
    sol.addActionListener (this);
    edit.add (loco);
    loco.addActionListener (this);
    edit.add (gb);
    gb.addActionListener (this);
    edit.add (block);
    block.addActionListener (this);
    edit.add (street);
    street.addActionListener (this);
    edit.add (auto);
    auto.addActionListener (this);
    edit.addSeparator ();
    edit.add (prop);
    setCtrlAccelerator (prop, 'P');
    prop.addActionListener (this);
    mbar.add (dae);
    dae.setMnemonic ('a');
    dae.add (stat);
    stat.addActionListener (this);
    dae.add (pow);
    pow.setEnabled (false);
    pow.addActionListener (this);
    mbar.add (test);
    test.setMnemonic ('T');
    test.add (allsol);
    allsol.addActionListener (this);
    test.add (onesol);
    onesol.addActionListener (this);
    test.add (s88);
    s88.addActionListener (this);
    mbar.add (info);
    info.setMnemonic ('I');
    info.add (about);
    about.addActionListener (this);
    info.add (help);
    setCtrlAccelerator (help, 'H');
    help.addActionListener (this);
    this.addWindowListener (new MyWindowAdapter ());
    this.addKeyListener (new CMyKeyAdapter ());
    
    cp = CCommandPipe.getCommandPipe(this);
    if(Command.getSessionId() > 0)
    {
    	this.setTitle(this.getTitle() + " [Session ID: " + Command.getSessionId() + "]");
    }
  }
    

  private void setCtrlAccelerator (JMenuItem mi, char acc) {
    KeyStroke ks = KeyStroke.getKeyStroke (acc, Event.CTRL_MASK );
    mi.setAccelerator (ks);
  }

  public boolean getPowerOn (){
    return bPowerOn;
  }

  public void setPowerOn (boolean bOn){
    bPowerOn = bOn;
  }

  public boolean getControlmode (){
    return bControlmode;
  }

  public void setControlmode (boolean b)
  {
    if (b == true)
    {
      if (bControlmode == false)
      {
        CControlCenter.setConnected (true);
        th = new CTurnoutHandler ();
        beh = CBlockEventHandler.getBlockEventHandler ();
        fh = CFeedbackportHandler.getFeedbackportHandler ();
        ih = CInfoportHandler.getInfoportHandler ();
        this.getContentPane ().setBackground (Color.green);
        repaint ();
      }
      CCommandPipe.setCommandMode(true);
      bControlmode = true;
      iController ++;
      if (CControlCenter.getConnected () == true)
        pow.setEnabled (true);
    }
    else if (b == false){
      if (iController > 0) iController--;
      if (iController == 0){
        bControlmode = false;
        th = null;
        fh = null;
        ih = null;
//        if (t != null) t.interrupt();
        CCommandPipe.setCommandMode(false);
        this.getContentPane ().setBackground (Color.magenta);
        repaint ();
        pow.setEnabled (false);
      }
    }
  }

  public boolean getEditmode (){
    return bEditmode;
  }

  public void setEditmode (boolean b){
    if (b == true){
      iEditor++;
      bEditmode = true;
      this.getContentPane ().setBackground (Color.blue);
      repaint ();
    }
    else if (b == false){
      if (iEditor > 0) iEditor--;
      if (iEditor == 0){
        bEditmode = false;
        this.getContentPane ().setBackground (Color.magenta);
        repaint ();
      }
    }
  }

  public void actionPerformed (ActionEvent evt)
  {
    Object o = evt.getSource ();
    if (o == nloco)
    {
      if (getEditmode())
      	showWarning ("Editiermodus");
      else 
      {
        /*Fehlermeldung bei leerer Lokliste*/
        if (CControlCenter.getLocoList().isEmpty()) 
        {
          JOptionPane.showMessageDialog(this,
            "Es ist noch keine Lokomotive konfiguriert.\n"+
	    "Bitte legen diese zunächst über das Menü\n"+
	    "'Editoren/Lokomotiven' an.", "Information",
              JOptionPane.INFORMATION_MESSAGE);
	     }
	     else 
	     {
          setControlmode(true);
          CLocoControlDialog lcd = new CLocoControlDialog(this);
	     }
      }
    }
    else if (o == ngb)
    {
      if (getEditmode())
      	showWarning ("Editiermodus");
      else 
      {
        setControlmode (true);
        showGb ();
      }
    }
    else if (o == end){
      if (getEditmode())
        showWarning ("Editiermodus");
      else if (getControlmode())
        showWarning ("Kommandomodus");
      else{
        cleanUp();
        System.exit(0);
      }
    }
    else if (o == gb) {
      if (getControlmode ())
        showWarning ("Kommandomodus");
      else {
        setEditmode (true);
        createGbEditor ();
      }
    }
    else if (o == sol){
      if (getControlmode ())
        showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        CTurnoutEditor te = new CTurnoutEditor ();
        te.show ();
      }
    }
    else if (o == loco){
      if (getControlmode ()) showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        editLoco ();
      }
    }
    else if (o == block){
      if (getControlmode ()) showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        editBlock ();
      }
    }
    else if (o == street){
      if (getControlmode ()) showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        editStreet ();
      }
    }
    else if (o == auto){
      if (getControlmode ()) showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        editAuto ();
      }
    }
    else if (o == prop){
      if (getControlmode ()) showWarning ("Kommandomodus");
      else{
        setEditmode (true);
        CPropertyEditor pe = new CPropertyEditor ("Programmeigenschaften");
        pe.show ();
      }
    }
    else if (o == allsol){
      if (getEditmode()) showWarning ("Editiermodus");
      else {
        setControlmode (true);
        CTurnoutTest tt = new CTurnoutTest (0);
        tt.show();
      }
    }
    else if (o == onesol){
      if (getEditmode()) showWarning ("Editiermodus");
      else{
        setControlmode (true);
        CTurnoutTestDialog ttd = new CTurnoutTestDialog
          (this, "Auswahl Schaltdekoder", true);
        ttd.show ();
      }
    }
    else if (o == s88){
      if (getEditmode()) showWarning ("Editiermodus");
      else{
        setControlmode (true);
        showS88 ();
      }
    }
    else if (o == stat){
      showDaemon ();
    }
    else if (o == pow){
      powerOnOff ();
    }
    else if (o == about){
      showAbout ();
    }
    else if (o == help){
      showHelp ();
    }
  }

  private final void showS88 (){
    int iMod = -1;
    String s = JOptionPane.showInputDialog(
      this,
      "Bitte Geben Sie die Modul-Nr. ein",
      "Modul-Auswahl für S88-Tester",
      JOptionPane.OK_OPTION);
    try {iMod = Integer.parseInt (s);}
    catch (Exception exc){}
    if (iMod > 0){
      CS88Test s88t = new CS88Test (iMod);
      s88t.show ();
    }
  }
  private final void editAuto (){
    CChainEditorDialog ced = new CChainEditorDialog (this, "", true);
    ced.show ();
  }

  private final void showHelp (){
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
  }

  private final void showAbout (){
    CInfo in = new CInfo (this, "Info über JTrain", false);
    in.show ();
  }

  private final void showDaemon(){
    MyServerDialog msd = new MyServerDialog (this, "Server-Info", false);
    msd.show ();
  }

  private class MyServerDialog extends JDialog {
    JLabel top = new JLabel ("JTrain ist verbunden mit:",
      SwingConstants.CENTER);
    JLabel bot = new JLabel (SRCP.getServerInfo().length() > 40
      ? SRCP.getServerInfo().substring(0, 39): SRCP.getServerInfo(),
      SwingConstants.CENTER);
    GridLayout gl = new GridLayout (2, 1, 20, 20);
    MyServerDialog (Frame owner, String title, boolean modal){
      super (owner,title, modal);
      this.setSize (300, 80);
      this.setResizable (false);
      this.setLocation (CControlCenter.getOrigin());
      this.getContentPane ().setBackground (Color.yellow);
      this.getContentPane ().setLayout (gl);
      this.getContentPane ().add (top);
      this.getContentPane ().add (bot);
    }
  }

  public final void powerOnOff (){
    if (bControlmode){
      CCommand com = new CCommand ();
      if (bPowerOn) {
        com.setCommand("SET POWER OFF\n");
        com.setTime (50);
        CCommandPipe.putLcCommand (com);
        this.getContentPane ().setBackground (Color.red);
      }
      if (!bPowerOn){
        com.setCommand("SET POWER ON\n");
        com.setTime (50);
        CCommandPipe.putLcCommand (com);
        this.getContentPane ().setBackground (Color.green);
      }
    }
  }

  private final void createGbEditor(){
    CGbDialog gbdlg = new CGbDialog (this, "", true);
  }

  private final void showGb (){
    this.update(getGraphics());
    CGbExec gbexec = new CGbExec ("");
  }

  private final void editLoco (){
    CLoco loco = new CLoco ();
    CLocoDialog cld = new CLocoDialog (this);
    cld.show();
  }

  private final void editBlock (){
    CGbBlockEditor be = new CGbBlockEditor ("");
    if (be.openGb () == false)
      JTrain.mf.setEditmode (false);
    else{
      be.setSize (be.getGbSize());
      CGb.elementSize = CGb.getElementSize ();
      be.show ();
    }
  }

  private final void editStreet (){
    CGbStreetEditor se = new CGbStreetEditor ("");
    se.show ();
  }
  private final void showWarning (String s){
    JOptionPane.showMessageDialog(
      this,
      "Achtung! Das System befindet sich im " + s + ".\n"+
      "Bitte diesen zuerst beenden!",
      "Warnung",
      JOptionPane.INFORMATION_MESSAGE);
  }

  private final void cleanUp (){
    if (fh != null)
      CFeedbackportHandler.getFeedbackportHandler().disconnect();
    if (ih != null)
      CInfoportHandler.getInfoportHandler().disconnect();
    CCommandPipe.setCommandMode(false);
    cp.destroy();
    cp = null;
    setEditmode (false);
    setControlmode (false);
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      if (e.getID() == WindowEvent.WINDOW_CLOSING)
        cleanUp ();
        System.exit(0);
    }
  }
}

