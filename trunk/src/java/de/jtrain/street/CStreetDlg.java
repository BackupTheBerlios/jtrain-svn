/*
a dialog to choose streets
last modified: 2002 03 27
author: Werner Kunkel
*/

package de.jtrain.street;
import javax.swing.JDialog;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.gb.CGb;

 public class CStreetDlg extends JDialog implements ActionListener{

    private Frame owner         = null;
    private CStreet street      = null;
    private Choice chc          = new Choice ();
    private JLabel jLab         = new JLabel ("Bitte w‰hlen Sie: ");
    private JLabel jLab2        = new JLabel ("Fahrstraﬂen: ");
    private JPanel jPWest       = new JPanel ();
    private JPanel jPNorth      = new JPanel ();
    private JPanel jPSouth      = new JPanel ();
    private JPanel jPCenter     = new JPanel ();
    private JButton jBEnd       = new JButton ("Abbrechen");
    private JButton jBOk        = new JButton ("OK");
    private BorderLayout bl     = new BorderLayout ();

    public CStreetDlg (Frame owner, String title, boolean modal){
      super (owner, title, modal);
      this.owner = owner;
      this.setSize (300,200);
      this.setResizable (false);
      this.setLocation (CControlCenter.getOrigin ());
      this.getContentPane ().setBackground (Color.yellow);
      this.getContentPane ().setLayout (bl);
      jPNorth.add (jLab);
      jPNorth.setBackground (Color.yellow);
      this.getContentPane ().add (jPNorth, BorderLayout.NORTH);
      jPWest.setMinimumSize (new Dimension (50, 50));
      jPWest.setPreferredSize (new Dimension (50, 50));
      jPWest.setBackground (Color.yellow);
      this.getContentPane ().add (jPWest, BorderLayout.WEST);
      for (Enumeration en = ((CGb)owner).streetList.elements ();
           en.hasMoreElements ();){
        street = (CStreet) en.nextElement ();
        chc.add (street.getName ());
      }
      jPCenter.add (jLab2);
      jPCenter.add (chc);
      jPCenter.setBackground (Color.yellow);
      this.getContentPane ().add (jPCenter, BorderLayout.CENTER);
      jBEnd.addActionListener (this);
      jPSouth.add (jBEnd);
      jBOk.addActionListener (this);
      jPSouth.add (jBOk);
      jPSouth.setBackground (Color.yellow);
      this.getContentPane ().add (jPSouth, BorderLayout.SOUTH);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.show ();
    }

    public void actionPerformed (ActionEvent evt){
      Object o = evt.getSource ();
      if (o == jBEnd){
        setVisible (false);
        dispose ();
      }
      else if (o == jBOk){
        CGb.s = chc.getSelectedItem ();
        setVisible (false);
        dispose ();
        return;
      }
    }

}

