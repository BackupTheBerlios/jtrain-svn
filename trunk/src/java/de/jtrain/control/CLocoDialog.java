/*This class edits the locolist:
adding new loco,
changing loco
deleting loco

last changes: 2004 10 08
author: Werner Kunkel, Guido Scholz (redesign)
*/

package de.jtrain.control;

import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;

import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class CLocoDialog extends JDialog implements ActionListener{

  private JLabel labNorth;
  private ButtonGroup cbg;
  private JRadioButton lcnew, lcedit, lcdel;
  private Choice chc;
  private JPanel jPNorth, jPWest, jPCenter, jPSouth;
  private JButton jBH, jBX, jBOk;

  public CLocoDialog (Frame owner){
    super (owner);
    setTitle("Lokdatei editieren");
    setResizable (false);
    setModal (true);
    setLocation (CControlCenter.getOrigin ());

    getContentPane().setLayout(new BorderLayout(20, 20));
 
    jPNorth = new JPanel();
    labNorth = new JLabel("Bitte wählen Sie");
    jPNorth.add(labNorth);
    getContentPane ().add(jPNorth, BorderLayout.NORTH);

    cbg = new ButtonGroup();
    lcnew = new JRadioButton("Lok hinzufügen");
    lcnew.setSelected(true);
    lcedit= new JRadioButton("Lok ändern");
    lcdel = new JRadioButton("Lok löschen");
    cbg.add(lcnew);
    cbg.add(lcedit);
    cbg.add(lcdel);
    jPWest = new JPanel(new GridLayout (3, 1));
    jPWest.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    jPWest.add (lcnew);
    jPWest.add (lcedit);
    jPWest.add (lcdel);
    getContentPane().add (jPWest, BorderLayout.WEST);

    chc   = new Choice();
    Iterator iter = CControlCenter.getLocoList ().iterator();
    while(iter.hasNext())
    {
        chc.add (((CLoco)iter.next()).getName());
    }
    jPCenter = new JPanel();
    jPCenter.add(chc);
    this.getContentPane().add(jPCenter, BorderLayout.CENTER);

    jPSouth = new JPanel(new GridLayout (1, 3, 5, 5));
    jPSouth.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    jBOk = new JButton("OK");
    jBX  = new JButton("Abbrechen");
    jBH  = new JButton("Hilfe");
    jBOk.setDefaultCapable(true);
    getRootPane().setDefaultButton(jBOk);
    jBOk.addActionListener(this);
    jBX.addActionListener(this);
    jBH.addActionListener(this);
    jPSouth.add(jBOk);
    jPSouth.add(jBX);
    jPSouth.add(jBH);
    getContentPane().add (jPSouth, BorderLayout.SOUTH);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new MyWindowAdapter ());
    pack();
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_32.htm#lokdialog"));
    hlp.hyperlinkUpdate (he);
  }

  public void actionPerformed (ActionEvent evt){
    Object o = evt.getSource ();
    if (o == jBH){
      try{
        showHelp ();
      }
      catch (MalformedURLException ex){}
    }
    if (o == jBX){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
      return;
    }
    if (o == jBOk){
      if (lcnew.isSelected()){
         CLocoEdit cle = new CLocoEdit ("Neu", new CLoco ());
         cle.show ();
      }
      if (lcedit.isSelected()){
        Iterator iter = CControlCenter.getLocoList ().iterator();
        while(iter.hasNext())
        {
          Object ob = iter.next();
          if (((CLoco)ob).getName ().equals (chc.getSelectedItem ())){
            if (((CLoco)ob).getLocoBusy ()== true){
              JOptionPane.showMessageDialog (
                this,
                "Achtung!\nKeine Bearbeitung möglich,\n"+
		"da die Lok in Benutzung ist!",
                "Warnung",
                JOptionPane.INFORMATION_MESSAGE);
            }
            else {
              CLocoEdit cle = new CLocoEdit (chc.getSelectedItem(), (CLoco)ob);
              cle.show ();
            }
          }
        }
      }
      if (lcdel.isSelected()){
        Iterator iter = CControlCenter.getLocoList ().iterator();
        while(iter.hasNext())
        {
          Object ob = iter.next();
          if (((CLoco)ob).getName ().equals (chc.getSelectedItem ()))
          {
            if (((CLoco)ob).getLocoBusy () == true)
            {
              JOptionPane.showMessageDialog(
                this,
                "Achtung!\nKeine Bearbeitung möglich,\n"+
		          "da die Lok in Benutzung ist!",
                "Warnung",
                JOptionPane.INFORMATION_MESSAGE);
            }
            else 
            {
            	iter.remove();
//              CControlCenter.getLocoList ().remove (ob);
            }
          }
          JTrain.mf.setEditmode (false);
          setVisible (false);
          dispose ();
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

