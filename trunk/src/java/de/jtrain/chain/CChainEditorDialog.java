/*
A Dialog to create a Chain-Editor

last modified: 2002 05 05
author: Werner Kunkel
*/

package de.jtrain.chain;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.control.CLoco;
import de.jtrain.main.JTrain;

import java.util.*;

public class CChainEditorDialog extends JDialog
implements ActionListener, ItemListener {

  private JLabel jLab         = new JLabel ("Bitte wählen Sie:", SwingConstants.CENTER) ;
  private JLabel jLabnew      = new JLabel ("Neue Kette für Lok:", SwingConstants.CENTER);
  private Choice chc          = new Choice ();
  private CheckboxGroup cbg   = new CheckboxGroup ();
  private Checkbox cbEdit     = new Checkbox ("Vorhandene Kette", cbg, false );
  private Checkbox cbNew      = new Checkbox ("Neue Kette", cbg, true );
  private JPanel jPWest       = new JPanel ();
  private JPanel jPEast       = new JPanel ();
  private JPanel jPSouth      = new JPanel ();
  private JPanel jPCenter     = new JPanel ();
  private JButton jBAbort     = new JButton ("Abbrechen");
  private JButton jBOk        = new JButton ("OK");
  private BorderLayout bl     = new BorderLayout (20, 20);
  private GridLayout gl       = new GridLayout (2, 2, 20, 20);
  private CLoco loco          = null;

  public CChainEditorDialog (Frame owner, String title, boolean modal)
  {
    super (owner,"Automatik-Ketteneditor - Auswahl", modal);
    this.setSize (300,200);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    this.getContentPane ().setBackground (Color.yellow);
    this.getContentPane ().setLayout (bl);
    chc.add ("<keine Lok>");
    if (!CControlCenter.getLocoList ().isEmpty()){
      Iterator iter = CControlCenter.getLocoList ().iterator();
      while(iter.hasNext())
      {
        loco = (CLoco) iter.next();
        chc.add (loco.getName());
      }
    }
    this.getContentPane ().add (jLab, BorderLayout.NORTH);
    cbEdit.addItemListener (this);
    jPWest.add (cbEdit);
    cbNew.addItemListener (this);
    jPWest.add (cbNew);
    jPWest.setBackground (Color.yellow);
    jPCenter.add (jPWest);
    jPCenter.add (jPEast);
    jPCenter.setBackground (Color.yellow);
    this.getContentPane ().add (jPCenter, BorderLayout.CENTER);
    jBAbort.addActionListener (this);
    jBAbort.setMnemonic('a');
    jPSouth.add (jBAbort);
    jBOk.addActionListener (this);
    jBOk.setMnemonic ('o');
    jPSouth.add (jBOk);
    jPSouth.setBackground (Color.yellow);
    this.getContentPane ().add (jPSouth, BorderLayout.SOUTH);
    jPEast.add (jLabnew);
    jPEast.add (chc);
    jPEast.setBackground (Color.yellow);
    addWindowListener (new MyWindowAdapter ());
  }

  public void itemStateChanged (ItemEvent e){
    if (e.getItem().equals ("Neue Kette")
    && e.getStateChange () == ItemEvent.SELECTED){
      jLabnew.setText ("Neue Kette für Lok:");
      chc.removeAll ();
      chc.add ("<keine Lok>");
      if (!CControlCenter.getLocoList ().isEmpty ()){
        Iterator iter = CControlCenter.getLocoList ().iterator();
        while(iter.hasNext())
        {
          CLoco loco = (CLoco) iter.next();
          chc.add (loco.getName());
        }
      }
    }
    else {
      jLabnew.setText ("vorhandene Kette:");
      chc.removeAll ();
      if (!CControlCenter.getChainList ().isEmpty ()){
        Iterator iter = CControlCenter.getChainList ().iterator();
        while(iter.hasNext())
        {
          CChain chain = (CChain) iter.next();
          chc.add (chain.getName ());
        }
      }
    }
  }

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBAbort){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBOk){
      JFrame ce = null;
      Checkbox choice = cbg.getSelectedCheckbox();
      if (choice.equals (cbNew)){
        String s = chc.getSelectedItem ();
        Iterator iter = CControlCenter.getLocoList ().iterator();
        while(iter.hasNext())
        {
          CLoco tloco = (CLoco) iter.next();
          if (tloco.getName ().equals (s)){
            ce = new CChainEditor (tloco);
            break;
          }
        }
        if (ce == null) ce = new CChainEditor ();
        ce.show ();
      }
      if (choice.equals (cbEdit)){
        String s = chc.getSelectedItem ();
        Iterator iter = CControlCenter.getChainList ().iterator();
        while(iter.hasNext())
        {
          CChain tchain = (CChain) iter.next();
          if (tchain.getName ().equals (s)){
            ce = new CChainEditor (tchain);
            break;
          }
        }
        if (ce != null)
          ce.show ();
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
        setVisible (false);
        dispose();
      }
    }
  }
}

