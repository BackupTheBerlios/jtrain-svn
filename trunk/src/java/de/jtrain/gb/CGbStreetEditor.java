/*
class to create and manage streets
last modified : 2002 05 05
author : Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.block.CBlContainer;
import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFbElement;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;
import de.jtrain.street.CStreet;
import de.jtrain.street.CStreetDlg;

import java.io.*;
import java.net.*;

public class CGbStreetEditor extends CGb implements ActionListener{

  private JPanel panNorth             = new JPanel ();
  private JPanel panCenter            = new JPanel ();
  private BorderLayout bl             = new BorderLayout ();
  private static String sHead         = new String (": J-Train Fahrstraßeneditor");
  private static String sName         = null;

  private JMenuBar mbar               = new JMenuBar ();
  private JMenu data                  = new JMenu ("Datei");
  private JMenuItem sav               = new JMenuItem ("Sichern", 's');
  private JMenuItem end               = new JMenuItem ("Beenden", 'e');
  private JMenu fs                    = new JMenu ("Fahrstaße");
  private JMenuItem neo               = new JMenuItem ("Neue Fahrstraße", 'n');
  private JMenuItem del               = new JMenuItem ("Fahrstaße löschen", 'l');
  private JMenuItem alldel            = new JMenuItem ("Alle Fahrstraßen löschen", 'a');
  private JMenu help                  = new JMenu ("Hilfe");
  private JMenuItem hlp               = new JMenuItem ("Hilfe", 'h');

  private CFbElement fbtmp            = null;
  private CGbElement eltmp            = null;
  private JButton bltmp               = null;
  private MyMouseAdapter mma          = new MyMouseAdapter ();
  private JLabel lab                  = new JLabel ();
  private JTextField tf               = new JTextField ();
  private boolean bNewFs              = false;

  public CGbStreetEditor (String streetName){
    super (sName);
    sName = streetName;
    setResizable (false);
    this.setLocation (1, 20);
    this.setIconImage (CControlCenter.getIcon ());
    sav.addActionListener (this);
    data.setMnemonic ('d');
    data.add (sav);
    end.addActionListener (this);
    data.add (end);
    mbar.add (data);
    fs.setMnemonic ('f');
    neo.addActionListener (this);
    fs.add (neo);
    del.addActionListener (this);
    fs.add (del);
    alldel.addActionListener (this);
    fs.add (alldel);
    mbar.add (fs);
    help.setMnemonic ('h');
    hlp.addActionListener (this);
    help.add (hlp);
    mbar.add (help);
    this.setJMenuBar (mbar);
    this.getContentPane ().setLayout (bl);
    panNorth.setSize (735, 80);
    panNorth.setMinimumSize(new Dimension (400,80));
    panNorth.setPreferredSize(new Dimension (400,80));
    panNorth.setBackground (Color.lightGray);
    panNorth.setLocation (0, 0);
    panNorth.setVisible (true);
    panNorth.setLayout (null);
    lab.setBounds (10, 10, 500, 30);
    panNorth.add (lab);
    lab.setVisible (true);
    tf.setBounds (10, 50, 300, 30);
    panNorth.add (tf);
    tf.setVisible (true);
    tf.addActionListener (this);
    this.getContentPane ().add (panNorth, BorderLayout.NORTH);
    panCenter.setBackground (Color.lightGray);
    panCenter.setLayout (null);
    panCenter.setVisible (true);
    this.getContentPane ().add (panCenter, BorderLayout.CENTER);
    if (!open ()){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addWindowListener (new MyWindowAdapter ());
  }

  private final boolean open (){
    boolean bReturn = false;
    if (load () == true){
      bReturn = true;
      this.setSize (getGbSize ());
      panCenter.setSize ((int)super.getGbSize().getWidth(),(int)super.getGbSize().getHeight()-80);
      elementSize = getElementSize ();
      fbtmp = null;
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        panCenter.add (fbtmp);
      }
      fbtmp = null;
      eltmp = null;
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        CGbElement eltmp = (CGbElement)en.nextElement ();
        panCenter.add (eltmp);
      }
      eltmp = null;
      if (blList.isEmpty ()){
        JTrain.mf.setEditmode (false);
        setVisible (false);
        dispose ();
      }
      bltmp = null;
      for (Enumeration en = blList.elements (); en.hasMoreElements ();){
        CBlContainer blc = (CBlContainer) en.nextElement ();
        bltmp = blc.getBlBox ();
        bltmp.removeMouseListener (mma);
        bltmp.addMouseListener (mma);
        panCenter.add (bltmp, 0);
      }
      bltmp = null;
      if (elementSize.equals (smSize)){
        elementSize = medSize;
        elementSize = smSize;
      }
      else if (elementSize.equals (medSize)){
        elementSize = smSize;
        elementSize = medSize;
      }
      sName = sTitle;
      this.setTitle (sName + sHead);
      refresh ();
      repaint ();
    }
    return bReturn;
  }

  private final void newFs (){
    bNewFs = true;
    lab.setText
      ("Blocknummern durch Komma getrennt eingeben, oder Blöcke anklicken");
  }

  private final void newFs_2 (){
    CStreet street = new CStreet ();
    stringToVector (street);
    String s = JOptionPane.showInputDialog (
      this,
      "Bitte geben Sie ein Kürzel für die Fahrstraße ein!",
      "Fahrstraße: Namen vergeben",
      JOptionPane.OK_OPTION);
    if (s.length() > 5) s = s.substring (0, 5);
    street.setName (s);
    lab.setText("Fahrstraße " + s + " fertig!");
    streetList.add (street);
    tf.setText ("");
    save (sName);
    bNewFs = false;
  }

  private final void delFs (){
    s = null;
    CStreetDlg dsd = new CStreetDlg (this, "Fahrstraße löschen", true);
    if (s != null){
      for (Enumeration en = streetList.elements (); en.hasMoreElements();){
        CStreet street = (CStreet) en.nextElement ();
        if (street.getName ().equalsIgnoreCase (s)){
          streetList.remove (street);
          break;
        }
      }
      lab.setText("Fahrstraße " + s + " gelöscht!");
      save (sName);
    }
  }

  private final void delAll(){
    streetList.removeAllElements();
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource();
    if (o == sav) save (sName);
    else if (o == end) {
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    else if (o == neo) newFs ();
    else if (o == tf && bNewFs) newFs_2 ();
    else if (o == del) delFs ();
    else if (o == alldel) delAll ();
    else if (o == hlp){
      try {showHelp ();}
      catch (MalformedURLException ex){}
    }
  }

  private final void showHelp () throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_35.htm#fsuse"));
    hlp.hyperlinkUpdate (he);
  }

  public final void stringToVector (CStreet street){
    String s  = null;
    int iNr   = 0;
    CBlock bl = null;
    StringTokenizer stok = new StringTokenizer (tf.getText (), ",");
    while (stok.hasMoreTokens()){
      s = stok.nextToken();
      try{
        iNr = Integer.parseInt (s);
      }
      catch (Exception e){
        iNr = 0;
      }
      if (iNr > 0){
        bl = getBlock (iNr);
        street.addBlock (bl);
      }
    }
  }

  class MyMouseAdapter extends MouseAdapter{
    public void mousePressed (MouseEvent e){
      if (e.getSource () instanceof JButton){
        if (bNewFs){
          String s = ((JButton)e.getSource ()).getText ();
          s = s.substring (4);
          int iCount = 0;
          while (s.charAt (iCount) >='0' && s.charAt (iCount++) <= '9');
          s = s.substring (0, iCount) + ",";
          tf.setText (tf.getText() + s);
          tf.requestFocus ();
        }
      }
    }
  }

  protected class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      JTrain.mf.setEditmode (false);
    }
  }
  
}

