/*
A Dialog to create a GB-Editor
last modified: 2002 05 01
author: Werner Kunkel
*/

package de.jtrain.gb;

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


public class CGbDialog extends JDialog implements ActionListener {

  private JLabel jLab         = new JLabel ("Bitte wählen Sie:", SwingConstants.CENTER) ;
  private JLabel jLabnew      = new JLabel ("Neues Gleisbild: Format", SwingConstants.CENTER);
  private Choice chc          = new Choice ();
  private CheckboxGroup cbg   = new CheckboxGroup ();
  private Checkbox cbEdit     = new Checkbox ("Vorhandenes Gleisbild", cbg, false );
  private Checkbox cbNew      = new Checkbox ("Neues Gleisbild", cbg, true );
  private JPanel jPWest       = new JPanel ();
  private JPanel jPEast       = new JPanel ();
  private JPanel jPSouth      = new JPanel ();
  private JPanel jPCenter     = new JPanel ();
  private JButton jBHelp      = new JButton ("Hilfe");
  private JButton jBEnd       = new JButton ("Abbrechen");
  private JButton jBOk        = new JButton ("OK");
  private BorderLayout bl     = new BorderLayout (20, 20);
  private GridLayout gl       = new GridLayout (2, 2, 20, 20);

  public CGbDialog (Frame owner, String title, boolean modal){
    super (owner,"Gleisbildeditor - Auswahl", modal);
    this.setSize (300,200);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    this.getContentPane ().setBackground (Color.yellow);
    this.getContentPane ().setLayout (bl);
    chc.add ("maximum");
    chc.add ("740 X 300");
    chc.add ("740 X 450");
    chc.add ("900 X 300");
    chc.add ("900 X 450");
    chc.add ("900 X 600");
    chc.add ("1100 X 600");
    chc.add ("1100 X 750");
    chc.add ("1100 X 900");
    this.getContentPane ().add (jLab, BorderLayout.NORTH);
    jPWest.add (cbEdit);
    jPWest.add (cbNew);
    jPWest.setBackground (Color.yellow);
    jPCenter.add (jPWest);
    jPCenter.add (jPEast);
    jPCenter.setBackground (Color.yellow);
    this.getContentPane ().add (jPCenter, BorderLayout.CENTER);
    jBHelp.addActionListener (this);
    jPSouth.add (jBHelp);
    jBEnd.addActionListener (this);
    jPSouth.add (jBEnd);
    jBOk.addActionListener (this);
    jPSouth.add (jBOk);
    jPSouth.setBackground (Color.yellow);
    this.getContentPane ().add (jPSouth, BorderLayout.SOUTH);
    jPEast.add (jLabnew);
    jPEast.add (chc);
    jPEast.setBackground (Color.yellow);

    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new MyWindowAdapter ());
    this.show ();
  }

   private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_33.htm#gbdialog"));
    hlp.hyperlinkUpdate (he);
  }

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBHelp){
      try{
        showHelp ();
      }
      catch (MalformedURLException ex){}
    }
    else if (o == jBEnd){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    else if (o == jBOk){
      Checkbox choice = cbg.getSelectedCheckbox();
      if (choice.equals (cbNew)){
        CGbEditor.sGbDim = chc.getSelectedItem ();
        CGbEditor gb = new CGbEditor ("");
        gb.setGbSize();
        gb.setSize (gb.getGbSize ());
        gb.show ();
      }
      else if (choice.equals (cbEdit)){
        CGbEditor gb = new CGbEditor ("");
        if (gb.openGb () == false)
          JTrain.mf.setEditmode (false);
        else{
          gb.setSize ((int) gb.getGbSize ().getWidth () < 735 ?
          (int) gb.getGbSize ().getWidth () : 735,
          (int) gb.getGbSize ().getHeight ());
          CGb.elementSize = CGb.getElementSize ();
          gb.show ();
        }
      }
      setVisible (false);
      dispose ();
      return;
    }
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      if (e.getID() == WindowEvent.WINDOW_CLOSING){
        JTrain.mf.setEditmode (false);
      }
    }
  }
}

