/*
this is the parentclass for CGbEditor and CGbBlockEditor

last modified: 2002 03 25
author: Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.jtrain.block.CBlContainer;
import de.jtrain.com.CFbElement;
import de.jtrain.main.JTrain;

class CGbEdit extends CGb implements ActionListener, MouseListener, MouseMotionListener {

  protected JPanel panNorth           = new JPanel ();
  protected MyPanel panCenter         = new MyPanel ();
  protected BorderLayout bl           = new BorderLayout ();
  protected static String sGbDim      = new String ("");
  protected static String sHead       = new String (": J-Train Gleisbildeditor");
  protected static String sName       = new String ("Neu");
  protected static String sGbName;
  protected String sId                = new String ("");

  //Menue using awt, because otherwise the lightweight JMenu-Components
  //would be covered by GbElements
  protected MenuBar mbar              = new MenuBar ();
  protected Menu data                 = new Menu ("Datei");
  protected MenuItem neo              = new MenuItem ("Neu");
  protected MenuItem open             = new MenuItem ("Öffnen");
  protected MenuItem sav              = new MenuItem ("Sichern");
  protected MenuItem saun             = new MenuItem ("Sichern unter");
  protected MenuItem quit             = new MenuItem ("Beenden");
  protected Menu shape                = new Menu ("Symbolgrösse");
  protected MenuItem sm               = new MenuItem ("Kleine Symbole");
  protected MenuItem med              = new MenuItem ("Grössere Symbole");
  protected Menu help                 = new Menu ("Hilfe");
  protected MenuItem hlp              = new MenuItem ("Hilfe");
  //temporary helper
  protected CFbElement fbtmp          = null;
  protected CGbElement eltmp          = null;
  protected JButton bltmp             = null;

  CGbEdit (String s){
    super (sName + sHead);
    setResizable (false);
    this.setLocation (1, 20);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener (new GbWindowAdapter ());
    addMouseListener (this);
    mbar.add (data);
    data.add (neo);
    neo.addActionListener (this);
    data.add (open);
    open.addActionListener (this);
    data.add (sav);
    sav.addActionListener (this);
    data.add (saun);
    saun.addActionListener (this);
    data.addSeparator ();
    quit.addActionListener (this);
    data.add (quit);
    mbar.add (shape);
    shape.add (sm);
    sm.addActionListener (this);
    shape.add (med);
    med.addActionListener (this);
    hlp.addActionListener (this);
    help.add (hlp);
    mbar.add (help);
    this.setMenuBar (mbar);
    this.getContentPane ().setLayout (bl);
    panNorth.setPreferredSize (dim);
    panNorth.setMinimumSize (dim);
    panNorth.setBackground (new Color (100, 0, 0));
    panNorth.setLocation (0, 0);
    panNorth.setVisible (true);
    panNorth.setLayout (null);
    this.getContentPane ().add (panNorth, BorderLayout.NORTH);
    panCenter.setBackground (new Color (128, 0, 0));
    panCenter.setSize ((int) getGbSize ().getWidth (),
      (int) getGbSize ().getHeight ()-80);
    panCenter.setLayout (null);
    panCenter.setVisible (true);
    this.getContentPane ().add (panCenter, BorderLayout.CENTER);
  }

  class MyPanel extends Panel {
  }

  protected void showListElements (){
    eltmp = null;
    for (Enumeration enu = elList.elements (); enu.hasMoreElements ();){
      eltmp = (CGbElement) enu.nextElement();
      eltmp.setSize (elementSize);
      eltmp.setLocation (eltmp.getXPosOnGb () * ((int)elementSize.getWidth () - 1) + 1,
      eltmp.getYPosOnGb () * ((int)elementSize.getHeight () - 1));
    }
    eltmp = null;
    bltmp = null;
    for (Enumeration enu = blList.elements (); enu.hasMoreElements ();){
      CBlContainer blc = (CBlContainer) enu.nextElement();
      bltmp = blc.getBlBox();
      bltmp.setSize (3 * (int)(elementSize.getWidth() -1),
       (int)(elementSize.getHeight () - 1));
      bltmp.setLocation (bltmp.getX(), bltmp.getY ());
      bltmp.setVisible (true);
      bltmp.repaint ();
    }
    bltmp = null;
  }

  protected void translateFbElements (){
    fbtmp = null;
    for (Enumeration enu = fbList.elements (); enu.hasMoreElements ();){
      fbtmp = (CFbElement) enu.nextElement();
      if (CGb.elementSize == smSize){
        fbtmp.setSize(fbtmp.getSmSize ());
        fbtmp.setLocation ((int)(fbtmp.getX () / (medSize.getWidth() - 1)
        * (smSize.getWidth () - 1) ), (int)(fbtmp.getY () /
        (medSize.getHeight () - 1) * (smSize.getHeight () -1)));
      }
      if (CGb.elementSize == medSize){
        fbtmp.setSize(fbtmp.getMedSize());
        fbtmp.setLocation (1 + (int)(fbtmp.getX () / (smSize.getWidth() - 1)
        * (medSize.getWidth () - 1)), 1 + (int)(fbtmp.getY () /
        (smSize.getHeight () - 1) * (medSize.getHeight () -1)));
      }
    }
    fbtmp = null;
  }

  protected void translateBlElements (){
    bltmp = null;
    CBlContainer blc = null;
    for (Enumeration enu = blList.elements (); enu.hasMoreElements ();){
      blc = (CBlContainer) enu.nextElement();
      bltmp = blc.getBlBox ();
      if (CGb.elementSize == smSize){
        bltmp.setSize (3 * (int)(elementSize.getWidth() -1),
        (int)(elementSize.getHeight() - 1));
        bltmp.setLocation ((int)(bltmp.getX () / (medSize.getWidth() - 1)
        * (smSize.getWidth () - 1) ), (int)(bltmp.getY () /
        (medSize.getHeight () - 1) * (smSize.getHeight () -1)));
      }
      if (CGb.elementSize == medSize){
        bltmp.setSize (3 * (int)(elementSize.getWidth() -1),
        (int)(elementSize.getHeight() - 1));
        bltmp.setLocation (1 + (int)(bltmp.getX () / (smSize.getWidth() - 1)
        * (medSize.getWidth () - 1)), 1 + (int)(bltmp.getY () /
        (smSize.getHeight () - 1) * (medSize.getHeight () -1)));
      }
    }
    bltmp = null;
  }

  protected boolean inGb (Component o){

    boolean ret = false;
    if (o.getLocation().getX() >= 0 && 
       (o.getLocation().getX() <  getGbSize().width) && 
       (o.getLocation().getY() >= -10) &&
       (o.getLocation().getY() <  getGbSize().height))
      ret = true;
    return ret;
  }

  protected void saveGb (){
    if (sName.equals("Neu")){
      sGbName = null;
      do{
        sGbName = (String) JOptionPane.showInputDialog(
          this,
          "Bitte geben Sie einen Namen ein:",
          "Dateinnamen vergeben",
          JOptionPane.QUESTION_MESSAGE);
        if (sGbName == null) break;
      }
      while (check (sGbName)!= 0);
      if (sGbName == null){
        JOptionPane.showMessageDialog(
          this,
          "Speichern abgebrochen, da kein Name vergeben wurde!",
          "Warnung",
          JOptionPane.INFORMATION_MESSAGE);
      }
      else{
        CGbEdit.sName = sGbName;
        this.setTitle (sName + sHead);
      }
    }
    if (sGbName == null) sGbName = sName;
    CGbEdit.sName = sGbName;
    save (sGbName);
    return;
  }

  protected int check (String s){
    int error = 0;
    if (s != null){
      for (int i = 0; i < s.length(); i++)
        if ((s.charAt(i) < 'a' || s.charAt(i) > 'z')&&
        (s.charAt(i) < 'A' || s.charAt(i) > 'Z')
        && s.charAt(i) != '-' && s.charAt(i) != '_'){
          error = 1;
          JOptionPane.showMessageDialog(
            this,
            "Ungültige(s) Zeichen im Namen",
            "Warnung",
            JOptionPane.INFORMATION_MESSAGE);
          break;
        }
    }
    return error;
  }

  protected boolean openGb (){

    boolean bReturn = false;
    panCenter.removeAll ();
    fbList.removeAllElements ();
    elList.removeAllElements ();
    blList.removeAllElements ();
    if (load () == true){
      bReturn = true;
      elementSize = getElementSize ();
      fbtmp = null;
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        panCenter.add (fbtmp);
      }
      fbtmp = null;
      eltmp = null;
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        CGbElement eltmp = (CGbElement) en.nextElement ();
        panCenter.add (eltmp);
      }
      eltmp = null;
      bltmp = null;
      for (Enumeration en = blList.elements (); en.hasMoreElements ();){
        CBlContainer blc = (CBlContainer) en.nextElement ();
        bltmp = blc.getBlBox ();
        panCenter.add (bltmp, 0);
      }
      bltmp = null;
      if (elementSize.equals (smSize)){
        elementSize = medSize;
        elementSize = smSize;
      }
      else if (elementSize.equals (medSize)){
        elementSize = smSize;
        elementSize = medSize;
      }
      CGbEdit.sName = sTitle;
      this.setTitle (sName + sHead);
      showListElements ();
      refresh ();
      repaint ();
    }
    return bReturn;
  }

  public void actionPerformed (ActionEvent evt){

    Object o = evt.getSource ();
    if (o == sm) {
      if (CGb.elementSize == medSize){
        setSmSize ();
        elementSize = getElementSize ();
        panCenter.repaint ();
        showListElements ();
        translateFbElements ();
        translateBlElements ();
        repaint ();
      }
    }
    else if (o == med) {
      if (this.elementSize == smSize){
        if (!elList.isEmpty () || !fbList.isEmpty () || !blList.isEmpty ()){
          int iAnswer = JOptionPane.showConfirmDialog(
            this,
            "Achtung, Gleisbild nicht leer! Gefahr von Elementverlust!",
            "Warnung",
            JOptionPane.OK_CANCEL_OPTION);
          if (iAnswer == JOptionPane.OK_OPTION){
            setMedSize ();
            elementSize = getElementSize ();
            panCenter.repaint ();
            showListElements ();
            translateFbElements ();
            translateBlElements ();
            repaint ();
          }
        }
        else {
          setMedSize ();
          elementSize = getElementSize ();
          panCenter.repaint ();
          repaint ();
        }
      }
    }
    else if (o == sav){
      saveGb ();
    }
    else if (o == saun){
      sName = "Neu";
      saveGb ();
    }
    else if (o == quit){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    else if (o == open){
      openGb ();
      elementSize = getElementSize ();
      if (elementSize.equals (smSize)){
        setMedSize ();
        setSmSize ();
      }
      if (elementSize.equals (medSize)){
        setSmSize ();
        setMedSize ();
      }
    }
  }

  public void mouseEntered (MouseEvent e){}
  public void mouseExited (MouseEvent e){}
  public void mouseMoved (MouseEvent e){}
  public void mouseClicked (MouseEvent e){}

  public void mousePressed (MouseEvent e){}
  public void mouseDragged (MouseEvent e){}
  public void mouseReleased (MouseEvent e){}

  protected class GbWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      JTrain.mf.setEditmode (false);
    }
  }
  
}

