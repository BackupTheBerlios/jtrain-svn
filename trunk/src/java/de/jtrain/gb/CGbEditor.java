/*
this class is the editor for track-layout

last modified: 2004 10 06
author: Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFbElement;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;

import java.net.*;

class CGbEditor extends CGbEdit implements ActionListener,
  MouseListener, MouseMotionListener {

  private MyPanel panCenter     = new MyPanel ();
  private static String sHead   = new String (": J-Train Gleisbildeditor");
  private static  String sName  = new String ("Neu");
  private static boolean bNewGb = true;
  private static int xMulti     = 5 + (int)elementSize.getWidth ();
  final static int iY           = 5;
  final static int iXStart      = 0;
  final static int iNOfGbEl     = 40;
  private CGbElement eltmp      = null;
  private CFbElement fbtmp      = null;
  private int tmpX, tmpY;
  private boolean bLinux        = false;

  //available symbols
  private CGbElement symList [] = new CGbElement [iNOfGbEl];
  private CFbElement fbmodel    = null;
  private Component mouseChoice = null;

  CGbEditor (String s){
    super (sName + sHead);
    setResizable (false);
    this.setLocation (1, 20);
    this.setIconImage (CControlCenter.getIcon ());
    addWindowListener (new GbWindowAdapter ());
    addMouseListener (this);
    this.setBackground (Color.lightGray);
    this.getContentPane ().setLayout (bl);
    panNorth.setSize (735, 80);
    panNorth.setBackground (new Color (100, 0, 0));
    panNorth.setLocation (0, 0);
    panNorth.setVisible (true);
    panNorth.setLayout (null);
    this.getContentPane ().add (panNorth, bl.NORTH);
    panCenter.setBackground (new Color (128, 0, 0));
    panCenter.setSize ((int)getGbSize ().getWidth () > 735 ?
      (int)getGbSize ().getWidth () : 735,
      (int)getGbSize ().getHeight () - 80);
    panCenter.setLayout (null);
    panCenter.setVisible (true);
    this.getContentPane ().add (panCenter, bl.CENTER);
    if (System.getProperty ("os.name").equalsIgnoreCase ("Linux")) bLinux = true;

    addElements ();
    showElements ();
  }

  class MyPanel extends Panel {

    public void paint (Graphics g){

      int iXStop, iYStop;
      //paint the net
      iXStop = (int) (this.getSize ().getWidth ());
      iYStop = (int) (this.getSize().getHeight () + 10);
      iDeltaX = (int) (elementSize.getWidth () - 1);
      iDeltaY = (int) (elementSize.getHeight () - 1);
      g.setColor (Color.yellow);
      //horizontal lines
      for (int i = 0;  i <= iYStop; i += iDeltaY)
        g.drawLine (iXStart, i, iXStop , i);
      //vertical lines
      for (int i = iXStart; i <= iXStop; i += iDeltaX)
        g.drawLine (i, 1, i, iYStop);
      showElements ();
    }
  }

  public final void showElements (){

    xMulti = 5 + (int)elementSize.getWidth ();
    for (int i = 0; i < iNOfGbEl; i++){
      symList [i].setSize (elementSize);
      if (i < 14) symList [i].setLocation (5 + xMulti * i, iY);
      if (i >= 14 && i < 28)
        symList [i].setLocation (5 + xMulti * (i - 14), iY + 24);
      if (i >= 28)
        symList [i].setLocation (5 + xMulti * (i - 28), iY + 48);
      symList [i].setVisible (true);
    }

    fbmodel.setLocation (5 + xMulti * 12, iY + 48);
    if (this.elementSize == smSize) fbmodel.setSize (CFbElement.getSmSize ());
    if (this.elementSize == medSize) fbmodel.setSize (CFbElement.getMedSize ());
    fbmodel.setVisible (true);
  }

  public final void setGbSize (){
    int iX, iY;
    if (sGbDim != "" && bNewGb){
      if (sGbDim.startsWith ("maximum"))
        setGbSize ((int) CControlCenter.getScreenSize ().getWidth (),
        (int) CControlCenter.getScreenSize ().getHeight () - 60);
      else{
        try {
          iX = Integer.parseInt (sGbDim.substring (0, sGbDim.indexOf ('X') - 1));
          iY = Integer.parseInt (sGbDim.substring (sGbDim.indexOf ('X') +2 ));
          setGbSize (iX, iY);
        }
        catch (Exception e){
        }
      }
    }
  }

  public final void saveGb (){
    if (sName.equals("Neu")){
      sGbName = null;
      do{
        sGbName = (String) JOptionPane.showInputDialog(
          this,
          "Bitte geben Sie einen Namen ein: ",
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
        this.sName = sGbName;
        this.setTitle (sName + sHead);
      }
    }
    if (sGbName == null) sGbName = sName;
    this.sName = sGbName;
    eltmp = null;
    for (Enumeration en = elList.elements (); en.hasMoreElements ();){
      CGbElement eltmp = (CGbElement)en.nextElement ();
      eltmp.removeMouseListener (this);
      eltmp.removeMouseMotionListener (this);
    }
    eltmp = null;
    save (sGbName);
    for (Enumeration en = elList.elements (); en.hasMoreElements ();){
      CGbElement eltmp = (CGbElement)en.nextElement ();
      eltmp.addMouseListener (this);
      eltmp.addMouseMotionListener (this);
    }
    eltmp = null;
    return;
  }

  public final void addElements (){
    for (int i = 0; i < iNOfGbEl; i++){
      if (i < 16) symList [i] = new CGbElement (i + 1);
      if (i > 25) symList [i] = new CGbSignal (i - 25);
      if (i > 15 && i < 26) symList [i] = new CGbSwitchEl (i-15);
      symList [i].addMouseListener (this);
      symList [i].addMouseMotionListener (this);
      panNorth.add (symList [i]);
    }
    fbmodel = new CFbElement (this);
    fbmodel.addMouseListener (this);
    fbmodel.addMouseMotionListener (this);
    panNorth.add (fbmodel);
  }

  public final boolean openGb (){
    boolean bReturn = false;
    panNorth.removeAll ();
    addElements ();
    showElements ();
    panCenter.removeAll ();
    fbList.removeAllElements ();
    elList.removeAllElements ();
    blList.removeAllElements ();
    if (load () == true){
      bReturn = true;
      this.setResizable(true);
      panCenter.setSize ((int) getGbSize ().getWidth () > 735 ?
      (int) getGbSize ().getWidth () : 735, (int) getGbSize ().getHeight () - 120);
      elementSize = getElementSize ();
      fbtmp = null;
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        fbtmp.addMouseListener (this);
        fbtmp.addMouseMotionListener (this);
        panCenter.add (fbtmp);
      }
      fbtmp = null;
      eltmp = null;
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        CGbElement eltmp = (CGbElement)en.nextElement ();
        eltmp.addMouseListener (this);
        eltmp.addMouseMotionListener (this);
        panCenter.add (eltmp);
      }
      eltmp = null;
      if (elementSize.equals(smSize)){
        elementSize = medSize;
        elementSize = smSize;
      }
      else if (elementSize.equals(medSize)){
        elementSize = smSize;
        elementSize = medSize;
      }
      this.sName = sTitle;
      this.setTitle (sName + sHead);
      showListElements ();
      panCenter.repaint ();
      refresh ();
      repaint ();
    }
    return bReturn;
  }

  private final void setToSmSize (){
    if (this.elementSize == medSize){
      panNorth.removeAll ();
      setSmSize ();
      elementSize = getElementSize ();
      addElements ();
      showElements ();
      panCenter.repaint ();
      showListElements ();
      translateFbElements ();
      translateBlElements ();
      refresh ();
      repaint ();
    }
  }

  private final void setToMedSize (){
    if (this.elementSize == smSize){
      if (!elList.isEmpty () || !fbList.isEmpty ()){
        int iAnswer = JOptionPane.showConfirmDialog(
            this,
            "Achtung, Gleisbild nicht leer! Gefahr von Elementverlust!",
            "Warnung",
            JOptionPane.OK_CANCEL_OPTION);
        if (iAnswer == JOptionPane.OK_OPTION){
          panNorth.removeAll ();
          setMedSize ();
          elementSize = getElementSize ();
          addElements ();
          showElements ();
          panCenter.repaint ();
          showListElements ();
          translateFbElements ();
          translateBlElements ();
          refresh ();
          repaint ();
        }
      }
      else {
        panNorth.removeAll ();
        setMedSize ();
        elementSize = getElementSize ();
        addElements ();
        showElements ();
        panCenter.repaint ();
        repaint ();
      }
    }
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_33.htm#gbuse"));
    hlp.hyperlinkUpdate (he);
  }


  public void actionPerformed (ActionEvent evt){

    Object o = evt.getSource ();

    if (o == sm) {
      setToSmSize ();
    }
    
    else if (o == med) {
      setToMedSize ();
    }
    
    else if (o == neo){
      panCenter.removeAll ();
      addElements();
      fbList.removeAllElements ();
      elList.removeAllElements ();
      blList.removeAllElements ();
      sName = "Neu";
      this.setTitle (sName + sHead);
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
    
    else if (o == hlp){
      try{
        showHelp ();
      }
      catch (MalformedURLException ex){}
    }
  }

  public void mouseEntered (MouseEvent e){}
  public void mouseExited (MouseEvent e){}
  public void mouseMoved (MouseEvent e){}
  public void mouseClicked (MouseEvent e){}

  public void mousePressed (MouseEvent e){
    eltmp = null;
    fbtmp = null;
    mouseChoice = e.getComponent ();
    //create new element
    if (mouseChoice instanceof CGbElement
    && e.getComponent().getParent() == panNorth){
      if (mouseChoice instanceof CGbSwitchEl)
        eltmp = new CGbSwitchEl (((CGbSwitchEl)mouseChoice).getTyp ());
      else if (mouseChoice instanceof CGbSignal)
        eltmp = new CGbSignal (((CGbSignal)mouseChoice).getTyp ());
      else eltmp = new CGbElement (((CGbElement)mouseChoice).getTyp ());
      eltmp.setLocation (1, 0);
      eltmp.setSize (elementSize);
      eltmp.setVisible (true);
      eltmp.addMouseListener (this);
      eltmp.addMouseMotionListener (this);
      panCenter.add (eltmp, 0);
      elList.add (eltmp);
      eltmp = null;
    }
    else if (mouseChoice instanceof CFbElement
    && e.getComponent().getParent() == panNorth){
      fbtmp = new CFbElement (this);
      fbtmp.setLocation (1, 0);
      if (this.elementSize == smSize) fbtmp.setSize (CFbElement.getSmSize ());
      if (this.elementSize == medSize) fbtmp.setSize (CFbElement.getMedSize ());
      fbtmp.setVisible (true);
      fbtmp.addMouseListener (this);
      fbtmp.addMouseMotionListener (this);
      fbList.add (fbtmp);
      panCenter.add (fbtmp,0);
      fbtmp = null;
    }
    if (mouseChoice instanceof CGbElement
    && e.getComponent().getParent() == panCenter
    && !isEditable ((CGbElement) mouseChoice)){
      JOptionPane.showMessageDialog(
            this,
            "Element kann nicht editiert werden, gehört zu einem Block.",
            "Warnung",
            JOptionPane.OK_OPTION);
      return;
    }
    //if right-clicked: change digital adress/number
    if (e.getModifiers () == 4){
      eltmp = null;
      if ((mouseChoice instanceof CGbSignal ||
           mouseChoice instanceof CGbSwitchEl)
      && e.getComponent().getParent() == panCenter){
        eltmp = (CGbElement)mouseChoice;
        if (eltmp instanceof CGbSwitchEl)
          ((CGbSwitchEl)eltmp).setId (getMaId ());
        if (eltmp instanceof CGbSignal)
          ((CGbSignal)eltmp).setId (getMaId ());
        eltmp = null;
      }
      else if (mouseChoice instanceof CFbElement
//      e.getComponent ().getLocation ().getY () >= iYStart -10){
      && e.getComponent ().getParent () == panCenter){
        fbtmp = (CFbElement) mouseChoice;
        ((CFbElement) fbtmp).setId (fbtmp.getFbId (this));
        fbtmp = null;
      }
      //remove on double-click
      if (e.getComponent().getParent() == panCenter
      && e.getClickCount() == 2){
        if (e.getComponent() instanceof CGbElement){
          eltmp = (CGbElement)e.getComponent ();
          eltmp.removeMouseListener (this);
          eltmp.removeMouseMotionListener (this);
          eltmp.setVisible (false);
          elList.remove (eltmp);
        }
        if (e.getComponent () instanceof CFbElement) {
          fbtmp = (CFbElement) e.getComponent ();
          fbtmp.removeMouseListener (this);
          fbtmp.removeMouseMotionListener (this);
          fbtmp.setVisible (false);
          fbList.remove (fbtmp);
        }
      }
    }
    tmpX = (int) e.getComponent ().getLocation ().getX ();
    tmpY = (int) e.getComponent ().getLocation ().getY ();
  }


  public void mouseDragged (MouseEvent e){

    if (e.getComponent().getParent() == panCenter){
      mouseChoice = e.getComponent();
      if (mouseChoice instanceof CGbElement){
        if (isEditable ((CGbElement) mouseChoice)){
          /*if (!bLinux){  (guido)*/
            tmpX = (int) e.getComponent ().getLocation ().getX ();
            tmpY = (int) e.getComponent ().getLocation ().getY ();
          /*}*/
          mouseChoice.setLocation
            (tmpX + e.getX() - (int) elementSize.getWidth () / 2,
            tmpY + e.getY() - (int) elementSize.getHeight () / 2 );
        }
      }

      if (mouseChoice instanceof CFbElement){
        /*if (!bLinux){*/
            tmpX = (int) e.getComponent ().getLocation ().getX ();
            tmpY = (int) e.getComponent ().getLocation ().getY ();
        /*  }*/
        mouseChoice.setLocation(tmpX + e.getX() - 4, tmpY + e.getY () - 4);
      }
    }
  }

  public void mouseReleased (MouseEvent e){
    if (e.getComponent().getParent() == panCenter){
      mouseChoice = e.getComponent ();
      if (mouseChoice instanceof CGbElement
      && isEditable ((CGbElement)mouseChoice)){
        int corrX = 0, corrY = 0;
        iDeltaX = (int) (elementSize.getWidth () -1);
        iDeltaY = (int) (elementSize.getHeight () -1);
        if (mouseChoice.getX () % iDeltaX < iDeltaX / 2)
          corrX = - mouseChoice.getX () % iDeltaX ;
        if (mouseChoice.getX () % iDeltaX >= iDeltaX / 2)
          corrX = iDeltaX - mouseChoice.getX () % iDeltaX;
        if (mouseChoice.getY () % iDeltaY < iDeltaY / 2)
          corrY = - mouseChoice.getY () % iDeltaY ;
        if (mouseChoice.getY () % iDeltaY >= iDeltaY / 2)
          corrY = iDeltaY - mouseChoice.getY () % iDeltaY;
        mouseChoice.setLocation (mouseChoice.getX () + corrX,
          mouseChoice.getY () + corrY);
        if (! inGb (mouseChoice)){
          mouseChoice.setVisible (false);
          panCenter.remove ((CGbElement)mouseChoice);
          elList.remove((CGbElement)mouseChoice);
        }
        else {
          ((CGbElement)mouseChoice).setPosOnGb (
          mouseChoice.getX () / ((int)elementSize.getWidth () - 1),
          (mouseChoice.getY ()) / ((int)elementSize.getHeight () - 1));
          if (mouseChoice instanceof CGbSwitchEl){
            if (((CGbSwitchEl)mouseChoice).getId () == 0)
              ((CGbSwitchEl)mouseChoice).setId (getMaId());
          }
          if (mouseChoice instanceof CGbSignal){
            if (((CGbSignal)mouseChoice).getId () == 0)
              ((CGbSignal)mouseChoice).setId (getMaId());
          }
        }
      }
      else if (mouseChoice instanceof CFbElement){
        if (! inGb (mouseChoice)){
          mouseChoice.setVisible (false);
          panCenter.remove ((CFbElement) mouseChoice);
          fbList.remove((CFbElement) mouseChoice);
        }
        else {
          if (((CFbElement) mouseChoice).getId () == 0){
            int id = ((CFbElement) mouseChoice).getFbId (this);
            ((CFbElement) mouseChoice).setId (id);
          }
        }
      }
    }
  }

  private final int getMaId (){
    int id = 0;
    do{
      sId = (String) JOptionPane.showInputDialog(
        this,
        "Bitte geben Sie die MA-Adresse (1-255) ein:",
        "Magnetartikeladresse eingeben",
        JOptionPane.QUESTION_MESSAGE);

      if (sId == null) sId = "";
      try{
        id = Integer.parseInt (sId);
      }
      catch (Exception ex){};
    }
    while (id < 1 || id > 255);
    return id;
  }
}

