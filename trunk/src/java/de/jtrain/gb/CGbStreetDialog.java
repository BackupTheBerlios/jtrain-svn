/*
A Dialog to create a StreetEditor

last modified : 2002 03 24
author : Werner Kunkel
*/

package de.jtrain.gb;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.main.JTrain;

public class CGbStreetDialog extends JDialog implements ActionListener {

  JLabel jLab         = new JLabel ("Bitte wählen Sie: ",
    SwingConstants.CENTER) ;
  JLabel jLabnew      = new JLabel ("Neues Gleisbild: Format",
    SwingConstants.CENTER);
  Choice chc          = new Choice ();
  CheckboxGroup cbg   = new CheckboxGroup ();
  Checkbox cbEdit     = new Checkbox ("Vorhandenes Gleisbild", cbg, false );
  Checkbox cbNew      = new Checkbox ("Neues Gleisbild", cbg, true );
  JPanel jPWest       = new JPanel ();
  JPanel jPEast       = new JPanel ();
  JPanel jPSouth      = new JPanel ();
  JPanel jPCenter     = new JPanel ();
  JButton jBEnd       = new JButton ("Abbrechen");
  JButton jBOk        = new JButton ("OK");
  BorderLayout bl     = new BorderLayout (20, 20);
  GridLayout gl       = new GridLayout (2, 2, 20, 20);

  CGbStreetDialog (Frame owner, String title, boolean modal){
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

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBEnd){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBOk){
      Checkbox choice = cbg.getSelectedCheckbox();
      if (choice.equals (cbNew)){
        CGbEditor.sGbDim = chc.getSelectedItem ();
        CGbEditor gb = new CGbEditor ("");
        gb.setGbSize();
        gb.setSize (gb.getGbSize ());
        gb.show ();
      }
      if (choice.equals (cbEdit)){
        CGbEditor gb = new CGbEditor ("");
        if (gb.openGb () == false)
          JTrain.mf.setEditmode (false);
        else{
          gb.setSize (gb.getGbSize());
          gb.elementSize = gb.getElementSize ();
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

