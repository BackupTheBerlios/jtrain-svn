/*
little class to generate loco-commands
last modified: 2002 04 12
author: Werner Kunkel
*/

package de.jtrain.control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.chain.CChainEditor;
import de.jtrain.com.CControlCenter;

public class CLocoCommandDialog extends JDialog implements ActionListener{
  private JFrame owner        = null;
  private String sLcName      = null;
  private CLoco loco          = null;
  private JLabel jLSpeed      = new JLabel ();
  private JLabel jLDir        = new JLabel ("Richtung");
  private JLabel jLFunc       = new JLabel ("Funktionen", SwingConstants.CENTER);
  private JTextField jTfSpeed = new JTextField ();
  private CheckboxGroup cbg   = new CheckboxGroup ();
  private Checkbox cbFor      = new Checkbox ("Vor", cbg, true);
  private Checkbox cbBack     = new Checkbox ("Zurück", cbg, false);
  private Checkbox cbF        = new Checkbox ("F");
  private Checkbox cbF1       = new Checkbox ("F1");
  private Checkbox cbF2       = new Checkbox ("F2");
  private Checkbox cbF3       = new Checkbox ("F3");
  private Checkbox cbF4       = new Checkbox ("F4");
  private JLabel jLNorth      = new JLabel ();
  private JPanel jPCenter     = new JPanel ();
  private JPanel jPSouth      = new JPanel ();
  private JPanel jPCN         = new JPanel ();
  private JPanel jPCS         = new JPanel ();
  private JPanel jPCh         = new JPanel ();
  private JPanel jPF          = new JPanel ();
  private JPanel jPWest       = new JPanel ();
  private JPanel jPEast       = new JPanel ();
  private JButton jBQ         = new JButton ("Nase voll");
  private JButton jBOk        = new JButton ("OK");
  private Container c         = null;
  private GridLayout gl1      = new GridLayout (2, 1, 10, 10);
  private GridLayout gl2      = new GridLayout (2, 2, 10, 10);
  private GridLayout gl3      = new GridLayout (2, 1, 10, 10);

  public CLocoCommandDialog (Frame owner, String title, boolean modal, String sLcName){
    super (owner, title, modal);
    this.sLcName = sLcName;
    this.loco = ((CChainEditor)owner).getChain ().getLoco ();
    this.setSize(300, 250);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin());
    c = this.getContentPane ();
    c.setBackground (Color.yellow);
    jLNorth.setText("Lokbefehl für " + sLcName);
    jLNorth.setHorizontalAlignment (SwingConstants.CENTER);
    jLNorth.setPreferredSize (new Dimension (200, 40));
    c.add (jLNorth, "North");
    jPCenter.setLayout (gl1);
    jPCenter.setBackground (Color.yellow);
    jPCN.setLayout (gl2);
    jLSpeed.setText("Geschwindigkeit: 0.." + loco.getSpeedsteps ());
    jPCN.add (jLSpeed);
    jTfSpeed.setHorizontalAlignment (SwingConstants.CENTER);
    jPCN.add (jTfSpeed);
    jPCN.add (jLDir);
    jPCh.setBackground (Color.yellow);
    jPCh.add (cbFor);
    jPCh.add (cbBack);
    jPCN.add (jPCh);
    jPCN.setBackground (Color.yellow);
    jPCenter.add (jPCN);
    jPCS.setLayout (gl3);
    jLFunc.setBackground (Color.yellow);
    jPCS.add (jLFunc);
    jPF.setBackground (Color.yellow);
    jPF.add (cbF);
    jPF.add (cbF1);
    jPF.add (cbF2);
    jPF.add (cbF3);
    jPF.add (cbF4);
    jPCS.add (jPF);
    jPCS.setBackground (Color.yellow);
    jPCenter.add (jPCS);
    c.add (jPCenter, "Center");
    jBQ.addActionListener (this);
    jPSouth.add (jBQ);
    jBOk.addActionListener (this);
    jPSouth.add (jBOk);
    jPSouth.setBackground (Color.yellow);
    c.add (jPSouth, "South");
    jPWest.setBackground (Color.yellow);
    c.add (jPWest, "West");
    jPEast.setBackground (Color.yellow);
    c.add (jPEast, "East");
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jBQ){
      ((CChainEditor)this.getParent()).getPane ().getCbLoco ().setState (false);
      setVisible (false);
      dispose ();
    }
    else if (o == jBOk){
      int i = -1;
      try{
        i = Integer.parseInt (jTfSpeed.getText());
      }
      catch (Exception ex){}
      if (i < 0 || i > loco.getMaxSpeed()){
        JOptionPane.showMessageDialog(
          this,
          "Geschwindigkeit nicht im zulässigen Bereich!",
          "Warnung",
          JOptionPane.OK_OPTION);
        jTfSpeed.setText("");
      }
      else {
        String sOn = "ON ";
        String sOff = "OFF ";
        String sF, sF1, sF2, sF3, sF4, sDir, sLocoCommand;
        if (cbF.getState() == true) sF = sOn;
        else sF = sOff;
        if (cbF1.getState() == true) sF1 = sOn;
        else sF1 = sOff;
        if (cbF2.getState() == true) sF2 = sOn;
        else sF2 = sOff;
        if (cbF3.getState() == true) sF3 = sOn;
        else sF3 = sOff;
        if (cbF4.getState() == true) sF4 = sOn;
        else sF4 = sOff;
        if (cbFor.getState() == true) sDir = " FORWARD ";
        else sDir = " BACKWARD ";
        sLocoCommand = "Lok " + loco.getName() + " SPEED " + i + sDir
        + sF + sF1 + sF2 + sF3 + sF4;
        ((CChainEditor)this.getParent()).getPane ().getStep ().getJobs ().
          add (sLocoCommand);
        ((CChainEditor)this.getParent()).getPane ().getList ().setListData (
          ((CChainEditor)this.getParent()).getPane ().getStep ().getJobs ());
        setVisible (false);
        dispose ();
      }
    }
  }
}
