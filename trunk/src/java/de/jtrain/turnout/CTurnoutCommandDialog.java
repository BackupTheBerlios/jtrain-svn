/*
little class to generate turnout-commands
last modified: 2002 04 12
author: Werner Kunkel
*/

package de.jtrain.turnout;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.chain.CChainEditor;
import de.jtrain.com.CControlCenter;

public class CTurnoutCommandDialog extends JDialog implements ActionListener{

  private JFrame owner          = null;
  private JLabel jLTurnout      = new JLabel ();
  private JTextField jTfTurnout = new JTextField ();
  private JLabel jLDir          = new JLabel ("Stellung: ",
    SwingConstants.CENTER);
  private CheckboxGroup cbg     = new CheckboxGroup ();
  private Checkbox cbGreen      = new Checkbox ("grün", cbg, true);
  private Checkbox cbRed        = new Checkbox ("rot", cbg, false);
  private JLabel jLNorth        = new JLabel ();
  private JPanel jPCenter       = new JPanel ();
  private JPanel jPSouth        = new JPanel ();
  private JPanel jPCh           = new JPanel ();
  private JPanel jPWest         = new JPanel ();
  private JPanel jPEast         = new JPanel ();
  private JButton jBQ           = new JButton ("Nase voll");
  private JButton jBOk          = new JButton ("ok");
  private Container c           = null;
  private GridLayout gl         = new GridLayout (2, 2, 10, 40);

  public CTurnoutCommandDialog (Frame owner, String title, boolean modal){
    super (owner, title, modal);
    this.setSize(300, 250);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin());
    c = this.getContentPane ();
    c.setBackground (Color.yellow);
    jLNorth.setText("Magnetartikel stellen");
    jLNorth.setHorizontalAlignment (SwingConstants.CENTER);
    jLNorth.setPreferredSize (new Dimension (200, 40));
    c.add (jLNorth, "North");
    jPCenter.setLayout (gl);
    jPCenter.setBackground (Color.yellow);
    jLTurnout.setText("Digitaladresse: 0..255:");
    jPCenter.add (jLTurnout);
    jTfTurnout.setHorizontalAlignment (SwingConstants.CENTER);
    jPCenter.add (jTfTurnout);
    jPCenter.add (jLDir);
    jPCh.setBackground (Color.yellow);
    jPCh.add (cbGreen);
    jPCh.add (cbRed);
    jPCenter.add (jPCh);
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
      ((CChainEditor)this.getParent()).getPane ().getCbMa ().setState (false);
      setVisible (false);
      dispose ();
    }
    else if (o == jBOk){
      int i = -1;
      try{
        i = Integer.parseInt (jTfTurnout.getText());
      }
      catch (Exception ex){}
      if (i < 0 || i > 255) {
        JOptionPane.showMessageDialog(
          this,
          "Digitaladresse nicht im zulässigen Bereich!",
          "Warnung",
          JOptionPane.OK_OPTION);
        jTfTurnout.setText("");
      }
      else {
        String sCommand = "Magnetartikel " + i;
        if (cbGreen.getState() == true)
          sCommand = sCommand + "grün";
        else sCommand = sCommand + "rot";
        ((CChainEditor)this.getParent()).getPane ().getStep ().getJobs ().
          add (sCommand);
        ((CChainEditor)this.getParent()).getPane ().getList ().setListData (
          ((CChainEditor)this.getParent()).getPane ().getStep ().getJobs ());
        setVisible (false);
        dispose ();
      }
    }
  }
}
