/*
This class enables editing the turnouts globally and individually

last modified : 2002 04 28
author : Werner Kunkel
*/

package de.jtrain.turnout;

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

public class CTurnoutEditor extends JFrame
  implements ActionListener, ItemListener{

  private CTurnout [] tl                = null;
  private BorderLayout bl               = new BorderLayout (20,20);
  private JPanel jPNorth                = new JPanel ();
  private JLabel jLPFA                  = new JLabel ("Protokoll global: ");
  private Checkbox cby                  = new Checkbox ("ja", false);
  private CheckboxGroup cbg             = new CheckboxGroup ();
  private Checkbox cbm                  = new Checkbox ("Märklin",cbg, false);
  private Checkbox cbn                  = new Checkbox ("NMRA", cbg, false);
  private Checkbox cbp                  = new Checkbox ("byServer", cbg, false);
  private JLabel jLWest                 = new JLabel ("   ");
  private JLabel jLEast                 = new JLabel ("   ");
  private GridLayout gl                 = new GridLayout (6, 2, 20, 20);
  private JPanel jPCenter               = new JPanel ();
  private JLabel jLAddr                 = new JLabel ("Digitaladresse: ");
  private JTextField jTAddr             = new JTextField ("1");
  private JLabel jLProtocol             = new JLabel ("Protokoll (M,N,P): ");
  private JTextField jTProtocol         = new JTextField ();
  private JLabel jLGOS                  = new JLabel ("Startstellung: ");
  private JPanel jPGOS                  = new JPanel ();
  private CheckboxGroup cbgp            = new CheckboxGroup ();
  private Checkbox cbgr                 = new Checkbox ("grün", cbgp, false);
  private Checkbox cbr                  = new Checkbox ("rot", cbgp, true);
  private JLabel jLSwtime               = new JLabel ("Schaltzeit in ms: ");
  private JTextField jTSwtime           = new JTextField ("100");
  private JLabel jLType                 = new JLabel ("Typ: ");
  private JPanel jPType                 = new JPanel ();
  private Choice chc                    = new Choice ();
  private Checkbox cbx                  = new Checkbox ("wird benutzt");
  private JPanel jPButfield             = new JPanel ();
  private JButton jBHelp                = new JButton ("Hilfe");
  private JButton jBQuit                = new JButton ("Ende");
  private JButton jBNext                = new JButton ("der Nächste, bitte");
  private JButton jBSave                = new JButton ("Speichern");
  private int iActTo                    = 1;

  public CTurnoutEditor ()
  {
    super ();
    this.setTitle ("Schaltdekodereditor");
    this.setSize (450, 350);
    this.setResizable (true);
    this.setIconImage (CControlCenter.getIcon ());
    Point p = new Point (CControlCenter.getOrigin ());
    p.translate (-75, -(int)p.getY() + 100);
    this.setLocation (p);
    this.getContentPane ().setLayout (bl);
    if ((tl = CControlCenter.loadTurnoutList ()) == null){
      tl = new CTurnout [255];
      for (int i = 1; i < 255; i++)
        tl[i] = new CTurnout (i);
    }
    jPNorth.add (jLPFA);
    cby.addItemListener (this);
    jPNorth.add (cby);
    cbm.setEnabled (false);
    jPNorth.add (cbm);
    cbn.setEnabled (false);
    jPNorth.add (cbn);
    cbp.setEnabled (false);
    jPNorth.add (cbp);
    jPCenter.setLayout (gl);
    jPCenter.add (jLAddr);
    jPCenter.add (jTAddr);
    jTAddr.addActionListener (this);
    jPCenter.add (jTAddr);
    jPCenter.add (jLProtocol);
    jTProtocol.addActionListener (this);
    jTProtocol.setText (tl[1].getProtocol ());
    jPCenter.add (jTProtocol);
    jPCenter.add (jLGOS);
    jPGOS.add (cbgr);
    jPGOS.add (cbr);
    jPCenter.add (jPGOS);
    jPCenter.add (jLSwtime);
    jTSwtime.addActionListener (this);
    jPCenter.add (jTSwtime);
    jPCenter.add (jLType);
    chc.addItem ("Signal");
    chc.addItem ("Weiche");
    chc.addItem ("Doppelweiche");
    chc.addItem ("Kombination");
    jPType.add (chc);
    jPCenter.add (jPType);
    jPCenter.add (cbx);
    jBHelp.addActionListener (this);
    jBHelp.setMnemonic ('h');
    jPButfield.add (jBHelp);
    jBQuit.addActionListener (this);
    jBQuit.setMnemonic ('e');
    jPButfield.add (jBQuit);
    jBNext.addActionListener (this);
    jBNext.setMnemonic ('n');
    jPButfield.add (jBNext);
    jBSave.addActionListener (this);
    jBSave.setMnemonic ('s');
    jPButfield.add (jBSave);
    jTAddr.requestFocus ();
    this.getContentPane ().add (jPNorth, BorderLayout.NORTH);
    this.getContentPane ().add (jLWest, BorderLayout.WEST);
    this.getContentPane ().add (jLEast, BorderLayout.EAST);
    this.getContentPane ().add (jPCenter, BorderLayout.CENTER);
    this.getContentPane().add (jPButfield, BorderLayout.SOUTH);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addWindowListener (new MyWindowAdapter ());
    this.show ();
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_31.htm#schaltdek"));
    hlp.hyperlinkUpdate (he);
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jTAddr){
      int i = 0;
      try{
        i = Integer.parseInt (jTAddr.getText());
      }
      catch (Exception ex){}
      jTProtocol.setText (tl[i].getProtocol ());
      jTSwtime.setText (""+ tl[i].getSwitchtime ());
      iActTo = i;
      jTProtocol.requestFocus ();
    }
    if (o == jTProtocol){
      tl[iActTo].setProtocol(jTProtocol.getText ());
      jTSwtime.requestFocus ();
    }
    if (o == jTSwtime){
      tl[iActTo].setSwitchtime (Integer.parseInt(jTSwtime.getText()));
      chc.requestFocus ();
    }
    if (o == jBHelp){
      try{
        showHelp ();
      }
      catch (MalformedURLException ex){}
    }
    if (o == jBQuit){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBNext){
      iActTo = Integer.parseInt(jTAddr.getText ());
      tl[iActTo].setProtocol(jTProtocol.getText ());
      tl[iActTo].setGreenOnStart (cbgr.getState());
      tl[iActTo].setSwitchtime (Integer.parseInt (jTSwtime.getText ()));
      tl[iActTo].setTyp (chc.getSelectedItem ());
      tl[iActTo].setInUse (cbx.getState ());
      iActTo += 1;
      jTAddr.setText ("" + iActTo);
      jTProtocol.setText (tl[iActTo].getProtocol ());
      jTSwtime.setText ("" + tl[iActTo].getSwitchtime ());
      cbx.setState (false);
    }
    if (o == jBSave){
      iActTo = Integer.parseInt (jTAddr.getText ());
      if (cby.getState () == true){
        Checkbox choice = cbg.getSelectedCheckbox();
        if (choice.equals(cbm)){
          for (int i = 1; i < 255; i++)
            tl[i].setProtocol("M");
        }
        if (choice.equals(cbn)){
          for (int i = 1; i < 255; i++)
            tl[i].setProtocol("N");
        }
        if (choice.equals(cbp)){
          for (int i = 1; i < 255; i++)
            tl[i].setProtocol("P");
        }
      }
      tl[iActTo].setProtocol (jTProtocol.getText ());
      tl[iActTo].setGreenOnStart (cbgr.getState());
      tl[iActTo].setSwitchtime (Integer.parseInt (jTSwtime.getText ()));
      tl[iActTo].setTyp (chc.getSelectedItem ());
      tl[iActTo].setInUse (cbx.getState ());
      CControlCenter.saveTurnoutList (tl);
    }
  }

  public void itemStateChanged (ItemEvent e){
    Object o = e.getSource ();
    if (o == cby){
      if (((Checkbox)o).getState () == true){
        cbm.setEnabled (true);
        cbn.setEnabled (true);
        cbp.setEnabled (true);
      }
      if (((Checkbox)o).getState () == false){
        cbg.setSelectedCheckbox (null);
        cbm.setEnabled (false);
        cbn.setEnabled (false);
        cbp.setEnabled (false);
      }
    }
    if (o == cbx){
      if (((Checkbox)o).getState () == true)
        tl[iActTo].setInUse (true);
      if (((Checkbox)o).getState () == false)
        tl[iActTo].setInUse (false);
    }
    if (o == cbgr){
      if (((Checkbox)o).getState () == true)
        tl[iActTo].setGreenOnStart (true);
      if (((Checkbox)o).getState () == false)
        tl[iActTo].setGreenOnStart (false);
    }
    if (o == cbr){
      if (((Checkbox)o).getState () == true)
        tl[iActTo].setGreenOnStart (false);
      if (((Checkbox)o).getState () == false)
        tl[iActTo].setGreenOnStart (true);
    }
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      JTrain.mf.setControlmode (false);
    }
  }
  
}

