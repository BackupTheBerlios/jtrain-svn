/**/

package de.jtrain.turnout;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.main.JTrain;

import java.awt.*;
import java.awt.event.*;

public class CTurnoutTestDialog extends JDialog implements ActionListener{

  private JPanel jPNorth        = new JPanel ();
  private GridLayout gl         = new GridLayout (3, 1, 5, 5);
  private BorderLayout bl       = new BorderLayout (20, 20);
  private JLabel jL             = new JLabel
    ("Bitte geben Sie die Dekoderadresse ein: ", SwingConstants.CENTER);
  private JLabel jLWarn         = new JLabel
    ("ACHTUNG! BITTE DIE SCHALTZEIT NICHT", SwingConstants.CENTER);
  private JLabel jLWarn2        = new JLabel
    (" ZU KURZ EINSTELLEN (s. Hilfe, Kap.6.2)", SwingConstants.CENTER);
  private JPanel jP             = new JPanel ();
  private JTextField jTF        = new JTextField (5);
  private JLabel jLE            = new JLabel ("    ");
  private JLabel jLW            = new JLabel ("    ");
  private JPanel jButfield      = new JPanel();
  private JButton jBEnd         = new JButton ("Abbrechen");
  private JButton jBOK          = new JButton ("OK");
  private Container c           = null;
  private int iChoice           = -1;

  public CTurnoutTestDialog (Frame owner, String title, boolean modal) {
    super (owner, title, modal);
    this.setSize (300, 200);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    c = this.getContentPane ();
    c.setBackground (Color.yellow);
    c.setLayout (bl);
    jPNorth.setBackground (Color.yellow);
    jPNorth.setLayout (gl);
    jPNorth.add (jLWarn);
    jPNorth.add (jLWarn2);
    jPNorth.add (jL);
    c.add(jPNorth, BorderLayout.NORTH);
    jTF.addActionListener (this);
    jP.add (jTF);
    jP.setBackground (Color.yellow);
    c.add (jP, BorderLayout.CENTER);
    c.add (jLE, BorderLayout.EAST);
    c.add (jLW, BorderLayout.WEST);
    jBEnd.addActionListener (this);
    jButfield.add (jBEnd);
    jBOK.addActionListener (this);
    jButfield.add (jBOK);
    jButfield.setBackground (Color.yellow);
    c.add (jButfield, BorderLayout.SOUTH);
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource();
    if (o == jTF){
      try{
        iChoice = Integer.parseInt (jTF.getText ());
      }
      catch (Exception ex){}
    }
    if (o == jBEnd){
      JTrain.mf.setControlmode (false);
      setVisible (false);
      dispose ();
    }
    if (o == jBOK){
      try{
        iChoice = Integer.parseInt (jTF.getText ());
      }
      catch (Exception ex){}
      if (iChoice < 0){
        JOptionPane.showMessageDialog(
        this,
        "Bitte erst Digitaladresse eingeben",
        "Warnung",
        JOptionPane.INFORMATION_MESSAGE);
      }
      else if (iChoice >= 0){
        CTurnoutTest tt = new CTurnoutTest (iChoice);
        tt.show ();
        setVisible (false);
        dispose ();
      }
    }
  }
}

