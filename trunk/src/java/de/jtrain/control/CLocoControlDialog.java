/*
This class is a JDialog to choose one loco to control

last changes: 2004 10 08
author: Werner Kunkel, Guido Scholz (redesign)
*/

package de.jtrain.control;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.main.JTrain;

import java.awt.event.*;
import java.awt.*;
import java.util.*;


public class CLocoControlDialog extends JDialog implements ActionListener {

  private JLabel jLabNorth;
  private JComboBox locoList;
  private JPanel jPSouth, jPNorth, jPCenter;
  private JButton jBX, jBOk;

  public CLocoControlDialog (Frame owner){
    super (owner);
    this.setTitle("Lok auswählen");
    this.setResizable(false);
    this.setModal(true);
    this.setLocation(CControlCenter.getOrigin ());

    getContentPane().setLayout (new BorderLayout (20, 20));

    jPNorth = new JPanel();
    jPNorth.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    jLabNorth = new JLabel("Wählen Sie eine Lok aus:",
                            SwingConstants.CENTER);
    jPNorth.add(jLabNorth);
    getContentPane().add(jPNorth, BorderLayout.NORTH);

    jPCenter = new JPanel();
    jPCenter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    locoList= new JComboBox();
    /*Was ist, wenn die Liste leer ist? */
    for (Enumeration en = CControlCenter.getLocoList().elements ();
      en.hasMoreElements ();)
        locoList.addItem(((CLoco)en.nextElement()).getName());
    jPCenter.add(locoList);
    getContentPane().add(jPCenter, BorderLayout.CENTER);

    jPSouth = new JPanel(new GridLayout(1, 2, 5, 5));
    jPSouth.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    jBOk = new JButton ("OK");
    jBOk.addActionListener (this);
    jPSouth.add (jBOk);
    getRootPane().setDefaultButton(jBOk);

    jBX = new JButton("Abbrechen");
    jBX.addActionListener (this);
    jPSouth.add (jBX);

    getContentPane().add (jPSouth, BorderLayout.SOUTH);
    
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new MyWindowAdapter ());
    pack();
    setVisible(true);
  }

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBX){
      JTrain.mf.setControlmode (false);
      setVisible (false);
      dispose ();
      return;
    }
    if (o == jBOk){
      for (Enumeration en = CControlCenter.getLocoList ().elements();
        en.hasMoreElements();){
        Object ob = en.nextElement ();
        if (((CLoco)ob).getName().equals(locoList.getSelectedItem())){
          if (((CLoco)ob).getLocoBusy ()== true){
            JOptionPane.showMessageDialog(
              this,
              "Achtung! Nur Anzeige, da Lok in Benutzung!",
              "Warnung",
              JOptionPane.INFORMATION_MESSAGE);
          }
          CLocoControl lc = new CLocoControl ((CLoco)ob);
          lc.show ();
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
        JTrain.mf.setControlmode (false);
      }
    }
  }
  
}

