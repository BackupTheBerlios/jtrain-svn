/*
A Dialog to choose a chain to execute

last modified: 2002 04 22
author: Werner Kunkel
*/

package de.jtrain.chain;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.control.CLoco;
import de.jtrain.gb.CGbExec;
import de.jtrain.main.JTrain;

import java.util.*;

public class CChainFrameDialog extends JDialog implements ActionListener {

  private CGbExec parent      = null;
  private JLabel jLab         = new JLabel ("Bitte wählen Sie:", SwingConstants.CENTER) ;
  private JLabel jLabnew      = new JLabel ("Vorhandene Ketten:", SwingConstants.CENTER);
  private Choice chc          = new Choice ();
  private JPanel jPWest       = new JPanel ();
  private JPanel jPEast       = new JPanel ();
  private JPanel jPSouth      = new JPanel ();
  private JPanel jPCenter     = new JPanel ();
  private JButton jBEnd       = new JButton ("Abbrechen");
  private JButton jBOk        = new JButton ("OK");
  private BorderLayout bl     = new BorderLayout (20, 20);
  private GridLayout gl       = new GridLayout (2, 2, 20, 20);
  private CLoco loco          = null;

  public CChainFrameDialog (Frame owner, String title, boolean modal){
    super (owner, "Automatik-Kette: Auswahl", modal);
    this.parent = (CGbExec) owner;
    this.setSize (300, 200);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    this.getContentPane ().setBackground (Color.yellow);
    this.getContentPane ().setLayout (bl);
    chc.add ("< alle >");
    Iterator iter = CControlCenter.getChainList ().iterator();
    while(iter.hasNext())
    {
      chc.add (((CChain) iter.next()).getName ());
    }
    this.getContentPane ().add (jLab, BorderLayout.NORTH);
    jPWest.setBackground (Color.yellow);
    jPCenter.add (jPWest);
    jPCenter.add (jPEast);
    jPCenter.setBackground (Color.yellow);
    this.getContentPane ().add (jPCenter, BorderLayout.CENTER);
    jBEnd.addActionListener (this);
    jPSouth.add (jBEnd);
    jBOk.addActionListener (this);
    jPSouth.add (jBOk);
    jPSouth.setBackground (Color.yellow);
    this.getContentPane ().add (jPSouth, BorderLayout.SOUTH);
    jPEast.add (jLabnew);
    jPEast.add (chc);
    jPEast.setBackground (Color.yellow);
    addWindowListener (new MyWindowAdapter ());
  }

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBEnd){
      JTrain.mf.setControlmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBOk){
      JFrame ce = null;
      String s = chc.getSelectedItem ();
      Iterator iter = CControlCenter.getChainList ().iterator();
      while(iter.hasNext())
      {
          CChain tchain = (CChain) iter.next();
          if (s.equals ("< alle >") || tchain.getName ().equals (s)){
            ce = new CChainFrame (tchain, parent);
            ce.show ();
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
        setVisible (false);
        dispose();
      }
    }
  }
}

