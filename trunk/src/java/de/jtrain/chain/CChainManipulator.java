/*
a dialog to handle chains for handy usage

last modified 2002 05 05
serialClone is adapted from Krüger, Go To Java 2
author: Werner Kunkel
*/

package de.jtrain.chain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.control.CLoco;
import de.jtrain.gb.CStep;

import java.util.*;
import java.io.*;

public class CChainManipulator extends JDialog
implements ActionListener, ItemListener{

  JLabel jLHead             = new JLabel ("Bitte wählen Sie die Kette:");
  JPanel jPCenter           = new JPanel ();
  JPanel jPCNorth           = new JPanel ();
  JPanel jPCSouth           = new JPanel ();
  JLabel jLCSouth           = new JLabel ();
  JTextField jTF1           = new JTextField ();
  JPanel jPSouth            = new JPanel ();
  JButton jBAbort           = new JButton ("Abbrechen");
  JButton jBOk              = new JButton ("OK");
  Choice chc                = new Choice ();
  Choice chloco             = null;
  Checkbox cbAll            = new Checkbox ("alle Ketten ");
  String sChain             = "Kette ";
  String sJob               = null;
  String sChc               = null;
  String sInp               = null;
  Container c               = null;
  BorderLayout bl           = new BorderLayout (10, 10);
  GridLayout gl             = new GridLayout (2, 1, 10, 10);
  CChainEditor owner        = null;
  int iJob                  = 0;
  CChain chain              = null;

  CChainManipulator (Frame owner, String title, boolean modal, int iJob){
    super (owner, title, modal);
    this.iJob = iJob;
    this.owner = (CChainEditor)owner;
    this.setSize (300, 200);
    this.setResizable (false);
    this.setLocation (CControlCenter.getOrigin ());
    c = this.getContentPane ();
    c.setLayout (bl);
    c.setBackground (Color.yellow);
    if (iJob == CChain.RENAME){
      sJob = "umbenennen";
      jLCSouth.setText ("neuer Name:");
      jPCSouth.add (jLCSouth);
      jTF1.setPreferredSize (new Dimension (60, 20));
      jTF1.setMinimumSize (new Dimension (60, 20));
      jTF1.addActionListener (this);
      jPCSouth.add (jTF1);
    }
    else if (iJob == CChain.COPY){
      sJob = "kopieren";
      jLCSouth.setText ("neue Kette: Name:");
      jPCSouth.add (jLCSouth);
      jTF1.setPreferredSize (new Dimension (60, 20));
      jTF1.setMinimumSize (new Dimension (60, 20));
      jTF1.addActionListener (this);
      jPCSouth.add (jTF1);
    }
    else if (iJob == CChain.OUTPUT){
      sJob = "ausgeben";
      jPCSouth.add (cbAll);
    }
    else if (iJob == CChain.DELETE){
      sJob = "löschen";
    }
    else if (iJob == CChain.CHANGE){
      sJob = "wechseln";
    }
    else if (iJob == CChain.LOCOCHG){
      sJob = "Lok wechseln";
      chloco = new Choice ();
      for (Enumeration en = CControlCenter.getLocoList().elements();
      en.hasMoreElements();)
        chloco.add (((CLoco) en.nextElement ()).getName ());
      jLCSouth.setText ("Neue Lok:");
      jPCSouth.add (jLCSouth);
      jPCSouth.add (chloco);
    }
    this.setTitle (sChain + sJob);
    jLHead.setHorizontalAlignment (SwingConstants.CENTER);
    c.add (jLHead, BorderLayout.NORTH);
    for (Enumeration en = CControlCenter.getChainList ().elements ();
    en.hasMoreElements();){
      CChain tchain = (CChain) en.nextElement ();
      chc.add (tchain.getName ());
    }
    chc.addItemListener (this);
    jPCNorth.setBackground (Color.yellow);
    jPCNorth.add (new JLabel ("Kette "));
    jPCNorth.add (chc);
    jPCenter.setBackground (Color.yellow);
    jPCenter.setLayout (gl);
    jPCenter.add (jPCNorth);
    jPCSouth.setBackground (Color.yellow);
    jPCenter.add (jPCSouth);
    c.add (jPCenter, bl.CENTER);
    jBAbort.addActionListener (this);
    jBAbort.setMnemonic ('a');
    jPSouth.add (jBAbort);
    jBOk.addActionListener (this);
    jBOk.setMnemonic ('o');
    jPSouth.add (jBOk);
    jPSouth.setBackground (Color.yellow);
    c.add (jPSouth, BorderLayout.SOUTH);
    this.show ();
  }

  private CChain getChain (String s){
    for (Enumeration en = CControlCenter.getChainList ().elements ();
    en.hasMoreElements ();){
      CChain tchain = (CChain) en.nextElement ();
      if (tchain.getName ().equals (s))
        return tchain;
      }
    return null;
  }

  private final void copyChain (){
    CChain nChain = new CChain ();
    try {
      nChain = (CChain) serialClone (chain);
    }
    catch (Exception ex){
      JOptionPane.showMessageDialog (
      this,
      "Kopie hat leider nicht geklappt!",
      "Warnung",
      JOptionPane.OK_OPTION);
      setVisible (false);
      dispose ();
    }
    nChain.setName (jTF1.getText ());
    CControlCenter.getChainList ().add (nChain);
    CControlCenter.saveChainList ();
  }

  public static Object serialClone (Object o)
   throws IOException, ClassNotFoundException   {
    //Serialization of object
    ByteArrayOutputStream out = new ByteArrayOutputStream ();
    ObjectOutputStream os = new ObjectOutputStream (out);
    os.writeObject (o);
    os.flush ();
    //Deserialization of the object
    ByteArrayInputStream in = new ByteArrayInputStream (out.toByteArray ());
    ObjectInputStream is = new ObjectInputStream (in);
    Object ret = is.readObject ();
    is.close ();
    os.close ();
    return ret;
  }

  private final void newEditor (){
    owner.cleanUp ();
    CChainEditor che = new CChainEditor (chain);
    che.show () ;
  }

  private void showError (){
    JOptionPane.showMessageDialog (
      this,
      "Keine gültige Kette gewählt",
      "Warnung!",
      JOptionPane.OK_OPTION);
  }

  public void itemStateChanged (ItemEvent e){
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jTF1){
      sInp = jTF1.getText ();
      jBOk.requestFocus ();
    }
    else if (o == jBAbort){
      this.setVisible (false);
      dispose ();
    }
    else if (o == jBOk){
      sChc = chc.getSelectedItem ();
      chain = getChain (sChc);
      if (chain == null) {
        showError ();
        this.setVisible (false);
        dispose ();
        return;
      }
      if (iJob == CChain.RENAME){
        chain.setName (jTF1.getText ());
      }
      else if (iJob == CChain.COPY){
        copyChain ();
      }
      else if (iJob == CChain.OUTPUT){
        if (cbAll.getState() == false)
          chain.printChain ();
        else {
          for (Enumeration en = CControlCenter.getChainList ().elements ();
          en.hasMoreElements ();)
            ((CChain) en.nextElement ()).printChain ();
        }
      }
      else if (iJob == CChain.DELETE){
        if (chain.equals (owner.getChain())){
          newEditor ();
        }
        CControlCenter.getChainList ().remove (chain);
        CControlCenter.saveChainList ();
      }
      else if (iJob == CChain.CHANGE){
        newEditor ();
      }
      else if (iJob == CChain.LOCOCHG){
        CLoco oldLoco = chain.getLoco();
        String sOldName = oldLoco.getName ();
        String s = chloco.getSelectedItem ();
        CLoco loco = CControlCenter.getLocoByName (s);
        if (loco != null){
          for (Enumeration en = chain.getSteps(); en.hasMoreElements ();){
            CStep tstep = (CStep)en.nextElement ();
            for (Enumeration enu = tstep.getJobs ().elements ();
            enu.hasMoreElements ();){
              String strg = (String) enu.nextElement ();
              if (strg.startsWith ("Lok")){
                tstep.getJobs ().remove (strg);
                //cut away "Lok" + sOldName, just for optical reasons
                String s1 = strg.substring (4 + sOldName.length ());
                strg = "Lok " + loco.getName () + s1;
                tstep.getJobs ().add (strg);
              }
            }
          }
          chain.setLoco (loco);
          if (chain.equals (owner.getChain())){
            owner.setHeader ();
          CControlCenter.saveChainList();
          }
        }
      }
      this.setVisible (false);
      dispose ();
    }
  }
}

