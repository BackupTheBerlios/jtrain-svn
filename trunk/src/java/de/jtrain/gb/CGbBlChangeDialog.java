/*
the class to manage the changes on blocks
last modified: 2002 04 25
author: Werner Kunkel
*/

package de.jtrain.gb;

import javax.swing.*;

import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CGbBlChangeDialog extends JDialog implements ActionListener {
  private JPanel pNorth     = new JPanel ();
  private JLabel lNorth     = new JLabel ("Welchen Block möchten Sie editieren?");
  private JTextField tf     = new JTextField ();
  private BorderLayout blay = new BorderLayout ();
  private JPanel pSouth     = new JPanel ();
  private JButton jbOk      = new JButton ("OK");
  private JPanel pCenter    = new JPanel ();
  private GridLayout glay   = new GridLayout (7, 2, 20, 20);
  private CheckboxGroup cbg = new CheckboxGroup ();
  private Checkbox cb_1     = new Checkbox ("Block hier entfernen", cbg, false);
  private Checkbox cb_2     = new Checkbox ("Farbe ändern", cbg, false);
  private Checkbox cb_3     = new Checkbox ("Gleiselemente neu eingeben", cbg, false);
  private Checkbox cb_4     = new Checkbox ("Gleiselemente hinzu-/wegnehmen", cbg, false);
  private Checkbox cb_5     = new Checkbox ("Blockfeld bewegen", cbg, false);
  private Checkbox cb_6     = new Checkbox ("Zusatzblock ändern", cbg, false);
  private Checkbox cb_7     = new Checkbox ("Startbelegung ändern", cbg, false);
  private JPanel pWest      = new JPanel ();
  private Frame owner       = null;

  CGbBlChangeDialog (Frame owner, String title, boolean modal){
    super (owner, title, modal);
    this.setSize (300, 350);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    this.getContentPane ().setBackground (Color.yellow);
    this.getContentPane ().setLayout (blay);
    pNorth.setBackground (Color.yellow);
    pNorth.add (lNorth);
    tf.setMinimumSize (new Dimension (40,20));
    tf.setPreferredSize (new Dimension (40,20));
    pNorth.add (tf);
    this.getContentPane ().add (pNorth, BorderLayout.NORTH);
    jbOk.addActionListener (this);
    pSouth.add (jbOk);
    pSouth.setBackground (Color.yellow);
    this.getContentPane ().add (pSouth, BorderLayout.SOUTH);
    pCenter.setBackground (Color.yellow);
    pCenter.setLayout (glay);
    pCenter.add (cb_1);
    pCenter.add (cb_2);
    pCenter.add (cb_3);
    pCenter.add (cb_4);
    pCenter.add (cb_5);
    pCenter.add (cb_6);
    pCenter.add (cb_7);
    this.getContentPane ().add (pCenter, BorderLayout.CENTER);
    pWest.setBackground (Color.yellow);
    pWest.setMinimumSize (new Dimension (20,20));
    pWest.setPreferredSize (new Dimension (20,20));
    this.getContentPane ().add (pWest, BorderLayout.WEST);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  public void actionPerformed (ActionEvent e){
    int iBl = 0;
    boolean bFound = false;
    try{
      iBl = Integer.parseInt (tf.getText ());
    }
    catch (Exception ex){
      iBl = 0;
    }
    Checkbox choice = cbg.getSelectedCheckbox ();
    if (iBl > 0){
      Iterator iter = CControlCenter.getBlockList ().iterator();
      while(iter.hasNext())
      {
        CBlock bl = (CBlock) iter.next();
        if (bl.getBlNumber () == iBl){
          bFound = true;
          break;
        }
      }
    }
    if (iBl == 0 || !bFound){
      JOptionPane.showMessageDialog(
        this,
         tf.getText () + " ist kein gültiger Block!",
        "Warnung",
        JOptionPane.OK_OPTION);
      return;
    }
    ((CGbBlockEditor)this.getOwner()).iBlNr = iBl;
    if ( choice == null){
       JOptionPane.showMessageDialog(
        this,
        "Keine Aktion ausgewählt!",
        "Warnung",
        JOptionPane.OK_OPTION);
        return;
    }
    else if (choice.equals(cb_1)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 1;
    else if (choice.equals(cb_2)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 2;
    else if (choice.equals(cb_3)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 3;
    else if (choice.equals(cb_4)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 4;
    else if (choice.equals(cb_5)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 5;
    else if (choice.equals(cb_6)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 6;
    else if (choice.equals(cb_7)) ((CGbBlockEditor)this.getOwner()).iEditChoice = 7;
    setVisible (false);
    dispose ();
  }

}

