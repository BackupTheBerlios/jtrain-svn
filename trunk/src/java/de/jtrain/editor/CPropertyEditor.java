/*
editorpanel for JTrain's properties
last modified: 2004 10 07
author: Werner Kunkel, Guido Scholz (Redesign)
*/

package de.jtrain.editor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;

import java.io.*;
import java.net.*;

public class CPropertyEditor extends JFrame implements ActionListener {

  private JButton jBHelp, jBQuit, jBSave;
  private JLabel jLN, jLHost, jLPort, jLWorkingdir, jLLnF, jLLang, jLSendStop;
  private JTextField jTfHost, jTfPort, jTfWorkingdir;
  private JComboBox lafList, langList;
  private JRadioButton jtrainRB, serverRB;
  private ButtonGroup sendstopBG;
  private JPanel jPWest, jPEast, jPNorth, jPSouth, jPSouthF, jPSendStop;


  public CPropertyEditor (String title)
  {
    super (title);
    setIconImage(CControlCenter.getIcon());

    getContentPane().setLayout(new BorderLayout(10, 10));

    jPNorth = new JPanel();
    jPNorth.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    jLN = new JLabel("ACHTUNG! Bevor sie hier etwas ändern, "+
      "lesen sie bitte Kapitel 3.7 in der Hilfe!");
    jPNorth.add(jLN);
    getContentPane().add(jPNorth, BorderLayout.NORTH);


    jPSouthF = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    jPSouth = new JPanel(new GridLayout(1, 3, 5, 5));
    jPSouth.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
    jPSouthF.add(jPSouth);

    jBSave = new JButton ("OK");
    jBSave.addActionListener (this);
    jBSave.setDefaultCapable(true);
    getRootPane().setDefaultButton(jBSave);
    jPSouth.add (jBSave);

    jBQuit = new JButton ("Abbrechen");
    jBQuit.addActionListener (this);
    jBQuit.setMnemonic ('A');
    jPSouth.add (jBQuit);

    jBHelp = new JButton ("Hilfe");
    jBHelp.addActionListener (this);
    jBHelp.setMnemonic ('H');
    jPSouth.add (jBHelp);

    getContentPane().add(jPSouthF, BorderLayout.SOUTH);


    jPWest = new JPanel(new GridLayout(6, 1, 5, 5));
    jPWest.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    getContentPane().add(jPWest, BorderLayout.WEST);

    jLHost = new JLabel("Name des Servercomputers:");
    jLPort = new JLabel("Portnummer des SRCP-Servers:");
    jLWorkingdir = new JLabel("Arbeitsverzeichnis:");
    jLLnF = new JLabel("JTrain's neue Kleider:");
    jLLang = new JLabel("Sprache:");
    jLSendStop = new JLabel("Abschaltbefehl für MA senden:");

    jPWest.add(jLHost);
    jPWest.add(jLPort);
    jPWest.add(jLWorkingdir);
    jPWest.add(jLLnF);
    jPWest.add(jLLang);
    jPWest.add(jLSendStop);


    jPEast = new JPanel(new GridLayout(6, 1, 5, 5));
    jPEast.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    getContentPane().add(jPEast, BorderLayout.CENTER);
    
    jTfHost = new JTextField(CControlCenter.getSetting("host"));
    jTfPort = new JTextField(CControlCenter.getSetting("port"));
    jTfWorkingdir = new JTextField(CControlCenter.getSetting("workingdir"));
  
    String[] lafStrings ={"Metal", "Windows", "Motif"};
    lafList = new JComboBox(lafStrings);

    String sLnF = CControlCenter.getSetting("lookandfeel");
    if (sLnF.equals(
      "javax.swing.plaf.metal.MetalLookAndFeel"))
        lafList.setSelectedIndex(0);
    else if 
      (sLnF.equals(
        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"))
          lafList.setSelectedIndex(1);
    else
      lafList.setSelectedIndex(2);

    String[] langStrings ={"englisch", "deutsch"};
    langList = new JComboBox(langStrings);
    langList.setSelectedIndex(1);
    if (!langList.getSelectedItem().equals(
      CControlCenter.getSetting("language")))
    langList.setSelectedIndex(0);

    jPSendStop = new JPanel (new GridLayout(1, 2));
    jtrainRB= new JRadioButton("JTrain");
    serverRB= new JRadioButton("SRCP-Server");
    if (jtrainRB.getText().equals(
      CControlCenter.getSetting("sendstop"))) {
        jtrainRB.setSelected(true);
        serverRB.setSelected(false);
    }
    else {
      jtrainRB.setSelected(false);
      serverRB.setSelected(true);
    }
    sendstopBG= new ButtonGroup();
    sendstopBG.add(jtrainRB);
    sendstopBG.add(serverRB);
    jPSendStop.add(jtrainRB);
    jPSendStop.add(serverRB);
    
    jPEast.add (jTfHost);
    jPEast.add (jTfPort);
    jPEast.add(jTfWorkingdir);
    jPEast.add(lafList);
    jPEast.add(langList);
    jPEast.add(jPSendStop);
    
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new MyWindowAdapter ());
    pack();
    setVisible(true);
  }

  private final void save (){
    CControlCenter.setSetting("host", jTfHost.getText());
    CControlCenter.setSetting("port", jTfPort.getText());
    CControlCenter.setSetting("workingdir", jTfWorkingdir.getText());
    String s = (String)lafList.getSelectedItem();
    String sProp ="";
    if (s.equals ("Windows"))
      sProp = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    else if (s.equals ("Metal"))
      sProp = "javax.swing.plaf.metal.MetalLookAndFeel";
    else if (s.equals ("Motif"))
      sProp = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    try{
      UIManager.setLookAndFeel(sProp);
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception e){
      JOptionPane.showMessageDialog (
      null,
      "Hat leider nicht geklappt mit dem Look and Feel.\n"+
      "Das Design bleibt, wie es ist.",
      "Warnung",
      JOptionPane.OK_OPTION);
      sProp = UIManager.getSystemLookAndFeelClassName ();
      try {
        UIManager.setLookAndFeel (sProp);
      }
      catch (Exception ex){}
    }
    CControlCenter.setSetting("lookandfeel", sProp);
    CControlCenter.setSetting("language", (String)langList.getSelectedItem());
    if (jtrainRB.isSelected())
      CControlCenter.setSetting("sendstop", "JTrain");
    else CControlCenter.setSetting("sendstop", "Server");

    CControlCenter.saveSettings();
    JTrain.mf.setEditmode(false);
    setVisible(false);
    dispose();
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_37.htm#propedit"));
    hlp.hyperlinkUpdate (he);
  }

  public final void exit (){
     JTrain.mf.setEditmode (false);
     setVisible (false);
     dispose ();
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jBHelp){
      try {showHelp ();}
      catch (Exception ex){}
    }
    else if (o == jBSave) save();
    else if (o == jBQuit) exit ();
  }


  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      if (e.getID() == WindowEvent.WINDOW_CLOSING){
        JTrain.mf.setEditmode (false);
      }
    }
  }

}

