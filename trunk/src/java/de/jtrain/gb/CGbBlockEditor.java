/*
this is the blockeditor

last modified: 2002 05 05
author: Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.block.CBlContainer;
import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFbElement;
import de.jtrain.config.Config;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;
import de.jtrain.turnout.CTurnoutJob;

import java.io.*;
import java.util.*;
import java.net.*;

public final class CGbBlockEditor extends CGbEdit implements ActionListener{

  private static String sHead         = new String (": J-Train Blockeditor");
  private static String sName         = new String ("Neu");
  private MyMouseAdapter mma          = new MyMouseAdapter ();
  private MyMouseMotionAdapter mmma   = new MyMouseMotionAdapter ();
  private MyKeyAdapter mka            = new MyKeyAdapter ();
  private CFbElement fbtmp            = null;
  private CGbElement eltmp            = null;
  private CGbElement gbStart          = null;
  private CGbElement gbStop           = null;
  private CBlContainer blc            = null;
  private CBlContainer blcont         = null;
  private CBlock bltmp                = null;
  private CBlock bl                   = null;
  private CBlock block                = null;
  private JLabel jLab                 = new JLabel ();
  private JLabel jLab2                = new JLabel ();
  private JButton blBox               = null;
  private boolean bStep2              = false;
  private boolean bStep3              = false;
  private boolean bStep4              = false;
  private boolean bStep5              = false;
  private boolean bEditStep1          = false;
  private boolean bEditStep2          = false;
  private boolean bEditStep3          = false;
  private boolean bEditStep4          = false;
  private boolean bForward            = false;
  private boolean bLinux              = false;
  //they should be manipulated from GbBlChangeDialog:
  public int iEditChoice              = 0;
  public int iBlNr                    = 0;

  private Vector vBlElements          = null;
  private LinkedList nodes            = null;
  private MenuItem bnew               = new MenuItem ("Neuer Block");
  private MenuItem bedit              = new MenuItem ("Block editieren");
  private MenuItem bdel               = new MenuItem ("Block löschen");
  private Menu blo                    = new Menu ("Block");
  private Menu trim                   = new Menu ("Gleisbildgröße");
  private MenuItem opt                = new MenuItem ("Größe optimieren");
  private int tmpX, tmpY;
  static final int LEFT_TO_RIGHT      = 1;
  static final int RIGHT_TO_LEFT      = -1;

  public CGbBlockEditor (String s){
    super (sName + sHead);
    this.setIconImage (CControlCenter.getIcon ());
    //the menu
    data.remove (neo);
    mbar.remove (help);
    blo.add (bnew);
    bnew.addActionListener (this);
    blo.add (bedit);
    bedit.addActionListener (this);
    blo.add (bdel);
    bdel.addActionListener (this);
    mbar.add (blo);
    mbar.add (trim);
    trim.add (opt);
    opt.addActionListener (this);
    mbar.add (help);
    //the frame
    panNorth.setBackground (Color.lightGray);
    panNorth.setLayout (new GridLayout (3, 1, 5, 5));
    panNorth.add (new JLabel ());
    jLab.setHorizontalAlignment (SwingConstants.CENTER);
    jLab.setVisible (true);
    jLab.setBackground (Color.white);
    panNorth.add (jLab, 0);
    jLab2.setHorizontalAlignment (SwingConstants.CENTER);
    jLab2.setVisible (true);
    jLab2.setBackground (Color.white);
    panNorth.add (jLab2, 0);
    panNorth.addMouseListener (mma);
    panCenter.setBackground (Color.lightGray);
    panCenter.addKeyListener (mka);
    panCenter.addMouseListener (mma);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.addWindowListener (new GbWindowAdapter ());
    this.addKeyListener (mka);
  }

  public final boolean openGb (){

    CBlContainer blcont = null;
    boolean bReturn = false;
    CControlCenter.updateBlockList ();
    fbList.removeAllElements ();
    elList.removeAllElements ();
    blList.removeAllElements ();
    if (load () == true){
      jLab.setText ("");
      jLab2.setText ("");
      bReturn = true;
      this.setSize (getGbSize ());
      panCenter.setSize ((int) getGbSize ().getWidth () > 735 ?
      (int) getGbSize ().getWidth () : 735, (int) getGbSize ().getHeight () - 120);
      elementSize = getElementSize ();
      fbtmp = null;
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        panCenter.add (fbtmp);
      }
      fbtmp = null;
      eltmp = null;
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        CGbElement eltmp = (CGbElement)en.nextElement ();
        eltmp.addMouseListener (mma);
        eltmp.addMouseMotionListener (mmma);
        panCenter.add (eltmp);
      }
      eltmp = null;
      blBox = null;
      for (Enumeration en = blList.elements (); en.hasMoreElements ();){
        blcont = (CBlContainer) en.nextElement ();
        blBox = blcont.getBlBox ();
        blBox.addMouseListener (mma);
        blBox.addMouseMotionListener (mmma);
        panCenter.add (blBox);
      }
      blBox = null;
      if (elementSize.equals(smSize)){
        elementSize = medSize;
        elementSize = smSize;
      }
      else if (elementSize.equals(medSize)){
        elementSize = smSize;
        elementSize = medSize;
      }
      CGbBlockEditor.sName = sTitle;
      this.setTitle (sName + sHead);
      showListElements ();
      panCenter.repaint ();
      refresh ();
      repaint ();
    }
    return bReturn;
  }

  private final void setToSmSize (){
    if (CGb.elementSize == medSize){
      setSmSize ();
      elementSize = getElementSize ();
      panCenter.repaint ();
      showListElements ();
      translateFbElements ();
      translateBlElements ();
      refresh ();
      repaint ();
    }
  }

  private final void setToMedSize (){
    if (elementSize == smSize){
      if (!elList.isEmpty () || !fbList.isEmpty ()){
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
          refresh ();
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

  private final void optimizeSize (){

    CGbElement eltmp        = null;
    JButton blBox           = null;
    CBlContainer blcont     = null;
    int iXMin = 100, iXMax = 0, iYMin = 100, iYMax = 0, iDeltaX = 0, iDeltaY = 0;
    int iAnswer = JOptionPane.showConfirmDialog (
      this,
      "Achtung! Schritt ist nicht umkehrbar! Trotzdem weiter?",
      "Warnung",
      JOptionPane.OK_CANCEL_OPTION);
    if (iAnswer == JOptionPane.OK_OPTION){
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        eltmp = (CGbElement) en.nextElement ();
        if (eltmp.getXPosOnGb () < iXMin) iXMin = eltmp.getXPosOnGb ();
        if (eltmp.getXPosOnGb () > iXMax) iXMax = eltmp.getXPosOnGb ();
        if (eltmp.getYPosOnGb () < iYMin) iYMin = eltmp.getYPosOnGb ();
        if (eltmp.getYPosOnGb () > iYMax) iYMax = eltmp.getYPosOnGb ();
      }
      for (Enumeration en = blList.elements (); en.hasMoreElements ();){
        blcont = (CBlContainer)en.nextElement ();
        blBox = blcont.getBlBox ();
        if (blBox.getX ()/(int)(elementSize.getWidth () -1) < iXMin)
          iXMin = blBox.getX ()/(int)(elementSize.getWidth () -1);
        else if (blBox.getX ()/(int)(elementSize.getWidth () -1) > iXMax)
          iXMax = blBox.getX ()/(int)(elementSize.getWidth () -1);
        if (blBox.getY ()/(int)(elementSize.getHeight() -1) < iYMin)
          iYMin = blBox.getY ()/(int)(elementSize.getHeight () -1);
        else if (blBox.getY ()/(int)(elementSize.getHeight () -1) > iYMax)
          iYMax = blBox.getY ()/(int)(elementSize.getHeight () -1);
      }
      blBox = null;
      iDeltaX = iXMax - iXMin + 3;
      iDeltaY = iYMax - iYMin + 3;
      //if there is some room to the left left or some room to the top
      if (iXMin > 1 || iYMin > 1){
        for (Enumeration en = elList.elements (); en.hasMoreElements ();){
          eltmp = (CGbElement)en.nextElement ();
          eltmp.setPosOnGb (eltmp.getXPosOnGb () - iXMin + 1,
                          eltmp.getYPosOnGb() - iYMin + 1);
          eltmp.setLocation (
            eltmp.getX () - (int)(CGb.elementSize.getWidth () - 1) * (iXMin -1) ,
            eltmp.getY () - (int)(CGb.elementSize.getHeight () - 1) * (iYMin -1));
        }
        fbtmp = null;
        for (Enumeration enu = fbList.elements (); enu.hasMoreElements ();){
          fbtmp = (CFbElement) enu.nextElement ();
          fbtmp.setLocation (
            fbtmp.getX () - (int)(CGb.elementSize.getWidth () - 1) * (iXMin -1) ,
            fbtmp.getY () - (int)(CGb.elementSize.getHeight () - 1) * (iYMin -1));
        }
        fbtmp = null;
        blBox = null;
        for (Enumeration enu = blList.elements (); enu.hasMoreElements ();){
          blcont = (CBlContainer)enu.nextElement ();
          blBox = blcont.getBlBox ();
          blBox.setLocation (
            blBox.getX () - (int)(CGb.elementSize.getWidth () - 1) * (iXMin -1) ,
            blBox.getY () - (int)(CGb.elementSize.getHeight () - 1) * (iYMin -1));
        }
        blBox = null;
      }
      setGbSize (iDeltaX * (int)elementSize.getWidth () ,
        iDeltaY * (int)elementSize.getHeight () + 120);
      panCenter.setSize(getGbSize());
      this.setSize (iDeltaX * (int)elementSize.getWidth () ,
        iDeltaY * (int)elementSize.getHeight () + 120);
      showListElements ();
      repaint ();

    }
  }

  private final void createNewBlock_1 (){
    int iFound = 0;
    boolean bFound = false;
    if (CControlCenter.getBlockList ().isEmpty ()){
      bl = new CBlock (1);
    }
    else{
      for (iFound = 1; !bFound; iFound ++){
        Iterator iter = CControlCenter.getBlockList ().iterator();
        while(iter.hasNext())
        {
          bFound = true;
          bltmp = (CBlock) iter.next();
          if (iFound == bltmp.getBlNumber ()){
            bFound = false;
            break;
          }
        }
      }
      bl = new CBlock (iFound -1);
    }
    iBlNr = bl.getBlNumber ();
    CControlCenter.getBlockList ().add (bl);
    getNewBlContainer ();
    jLab.setText ("neuer Block: Nummer " + bl.getBlNumber ());
    bl.setColor(JColorChooser.showDialog
      (this,
      "1. Schritt: Bitte Blockfarbe wählen",
      bl.getColor()));
    blBox.setBackground (bl.getColor());
    blBox.addMouseMotionListener (mmma);
    jLab2.setText ("2. Schritt: Wählen Sie das Startelement im Gleisbild");
    bStep2 = true;
  }

  private final void createNewBlock_2 (){
    gbStart.setActiveColor (bl.getColor ());
    gbStart.repaint ();
    if (! bEditStep2)
      jLab2.setText ("3. Schritt: Wählen Sie das Endelement");
    else
      jLab2.setText ("Wählen Sie das Endelement");
    bStep3 = true;
  }

  private final void createNewBlock_3 (){
    int iDiffX = 0, iDiffY = 0;
    nodes = new LinkedList ();
    iDiffX = gbStop.getXPosOnGb () - gbStart.getXPosOnGb ();
    if (iDiffX > 0){
      bForward = true;
      blc.setDir ("-> ");
    }
    else if (iDiffX < 0){
      bForward = false;
      blc.setDir ("<- ");
    }
    if (!bEditStep2) bl.setDescription ("B" + bl.getBlNumber () + " ");
    iDiffY = gbStop.getYPosOnGb () - gbStart.getYPosOnGb ();
    if (!checkReachable (iDiffX, iDiffY)) {
      showError ();
      return;
    }
    if (!autoroute ()){
      showError ();
      return ;
    }
    showRoute ();
    blBox.setText (blc.getDir () + bl.getDescription ());
    if (!bEditStep2) jLab2.setText
      ("4. Schritt: Wählen Sie das Endelement oder Ende mit Doppel- oder Rechtsclick");
    else  jLab2.setText
      ("Wählen Sie das Endelement oder Ende mit Doppel- oder Rechtsclick");
    bStep4 = true;
  }

  private final void createNewBlock_4 (){
    if (!bEditStep2){
      createTurnoutJobList (bl);
      bStep5 = true;
      jLab2.setText ("5. Schritt: Bewegen Sie mit Maus oder Pfeiltasten das Blockfeld an seinen Platz, Ende mit ESC");
      this.requestFocus();
    }
    else {
      bEditStep2 = false;
      if (! blList.contains (blc))
        blList.add (blc);
      jLab2.setText ("fertig!");
      save (sName);
      CControlCenter.saveBlockList();
    }
  }

  private final void createNewBlock_5 (){
    String s    = null;
    int iBlock  = 0;
    int iAnswer = 0;
    jLab2.setText ("6. Schritt");
    s = JOptionPane.showInputDialog(
      this,
      "Soll ein anderer Block mit diesem verknüpft werden, dann geben Sie bitte dessen Nummer ein:",
      "6. Schritt: Zusatzblock eingeben",
      JOptionPane.QUESTION_MESSAGE);
    if (s == null){
      iBlock = 0;
    }
    else {
      try{
        iBlock = Integer.parseInt (s);
      }
      catch (Exception e){
        iBlock = 0;
      }
    }
    if (iBlock > 0 && bl.setBlockNeeded (iBlock))
      jLab2.setText ("6. Schritt: Zusatzblock = " + iBlock);
    else if (iBlock > 0 && !bl.setBlockNeeded (iBlock)){
      JOptionPane.showMessageDialog(
        this,
        "Diesen Block kenne ich noch nicht. Bitte diesen Block später nachtragen!",
        "Warnung",
        JOptionPane.OK_OPTION);
      jLab2.setText ("7. Schritt");
    }
    iAnswer = JOptionPane.showConfirmDialog (
      this,
      "Ist dieser Block bei Programmstart belegt?",
      "7. Schritt: belegt/frei bei Programmstart",
      JOptionPane.YES_NO_OPTION);
    if (iAnswer == JOptionPane.YES_OPTION){
      bl.setBlockInUseOnStart (true);
      s = JOptionPane.showInputDialog(
        this,
        "Möchten Sie ein Lokkürzel für die Startbelegung eingeben?",
        "Namen vergeben",
        JOptionPane.OK_OPTION);
      if (s.length() > 5) s = s.substring(0,5);
      bl.setOwner (s);
      blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner ());
      bl.setState (CBlock.BLOCK_EXECUTED);
    }
    else{
      bl.setBlockInUseOnStart (false);
      bl.setState (CBlock.BLOCK_FREE);
    }
    save (sName);
    CControlCenter.saveBlockList();
    jLab2.setText ("Geschafft! Block ist fertig konfiguriert");
  }

  public final void editBlock (){
    if (iEditChoice == 1) editBlock_1 ();
    else if (iEditChoice == 2) editBlock_2 ();
    else if (iEditChoice == 3) editBlock_3 ();
    else if (iEditChoice == 4) editBlock_4 ();
    else if (iEditChoice == 5) editBlock_5 ();
    else if (iEditChoice == 6) editBlock_6 ();
    else if (iEditChoice == 7) editBlock_7 ();
  }

  private final boolean startEdit (){
    boolean bSuccess = true;
    block = getBlock (iBlNr);
    blcont = getBlContainer (iBlNr);
    if (block == null){
        JOptionPane.showMessageDialog(
          this,
          "Abbruch, da Block nicht gefunden",
          "Warnung",
          JOptionPane.OK_OPTION);
         bSuccess = false;
    }
    return bSuccess;
  }

  private final void editBlock_1 (){
    if (!startEdit () || blcont == null) return;
    if (blcont.getBlBox () != null)
      panCenter.remove (blcont.getBlBox ());
    if (blcont.getBlElements() != null)
      for (Enumeration en = blcont.getBlElements ().elements();
      en.hasMoreElements();){
        CGbElement gbtmp = (CGbElement) en.nextElement ();
        gbtmp.setActiveColor (Color.red);
        gbtmp.repaint();
      }
    blList.remove (blcont);
    panCenter.repaint ();
    save (sName);
    jLab.setText ("aus diesem Gleisbild entfernt wurde Block " + iBlNr);
    jLab2.setText ("");
  }

  private final void editBlock_2 () {
    if (!startEdit () || blcont == null) return;
    blc = blcont;
    bl.setColor (JColorChooser.showDialog
      (this,
      "Bitte neue Blockfarbe für Block " + iBlNr + " wählen",
      bl.getColor()));
    blc.getBlBox ().setBackground (bl.getColor());
    refresh (iBlNr);
  }

  private final void editBlock_3 () {
    if (!startEdit ()) return;
    blc = blcont;
    //if there is no matching container in this gb, we create one
    if (blc == null)
      getNewBlContainer ();
    blBox = blc.getBlBox ();
    blList.remove (blc);
    vBlElements = blc.getBlElements();
    if (blc != null && blc.getBlElements() != null)
      for (Enumeration en = blc.getBlElements ().elements (); en.hasMoreElements();){
        CGbElement gbtmp = (CGbElement) en.nextElement ();
        gbtmp.setActiveColor (Color.red);
        gbtmp.repaint ();
      }
    jLab.setText ("Block " + iBlNr + ": Elemente neu auswählen");
    jLab2.setText ("Wählen Sie das Startelement im Gleisbild");
    bEditStep1 = true;
  }

  private final void editBlock_4 () {
    if (!startEdit ()) return;
    bl = block;
    blc = blcont;
    if (blc == null)
      getNewBlContainer ();
    blBox = blc.getBlBox ();
    vBlElements = blc.getBlElements ();
    for (Enumeration e = vBlElements.elements ();e.hasMoreElements ();)
    ((CGbElement)e.nextElement ()).activeColor = bl.getColor ();
    jLab.setText ("Block " + iBlNr + ": Elemente hinzufügen/entfernen");
    jLab2.setText ("Wählen Sie das Element, das hinzugefügt/entfernt werden soll; Ende mit Rechtsclick");
    bEditStep3 = true;
    this.requestFocus();
  }

  private final void editBlock_5 () {
    if (!startEdit () || blcont == null) return;
    bl = block;
    blc = blcont;
    blBox = blc.getBlBox ();
    jLab.setText ("Blockbox bewegen");
    jLab2.setText ("Bewegen mit Maus oder Pfeiltaste (+Strg); Ende mit ESC");
    blBox.requestFocus ();
    bEditStep4 = true;
  }

  private final void editBlock_6 () {
    if (!startEdit ()) return;
    bl = block;
    int iNew = 0;
    jLab.setText ("Block " + iBlNr);
    jLab2.setText ("Zusatzblock neu eingeben");
    String s = JOptionPane.showInputDialog(
      this,
      "Bitte geben Sie den Zusatzblock (bisher:" + bl.getBlockNeeded () + ") ein",
      "Zusatzblock eingeben",
      JOptionPane.OK_OPTION);
    if (s != null){
      try{
        iNew = Integer.parseInt (s);
      }
      catch (Exception e){
        iNew = 0;
      }
    }
    bl.setBlockNeeded (iNew);
    jLab2.setText ("fertig!");
    save (sName);
    CControlCenter.saveBlockList();
  }

  private final void editBlock_7 () {
    if (!startEdit () || blcont == null) return;
    bl = block;
    blc = blcont;
    int iChoice = 0;
    jLab.setText ("Block "+ iBlNr);
    jLab2.setText ("Block belegt bei Start?");
    iChoice = JOptionPane.showConfirmDialog(
      this,
      "Ist dieser Block bei Programmstart belegt?",
      "Startbelegung",
      JOptionPane.YES_NO_OPTION);
    if (iChoice == JOptionPane.YES_OPTION){
      bl.setBlockInUseOnStart (true);
      String s = JOptionPane.showInputDialog(
        this,
        "Möchten Sie ein Lokkürzel für die Startbelegung eingeben?",
        "Namen vergeben",
        JOptionPane.OK_OPTION);
      if (s.length() > 5) s = s.substring (0, 5);
      bl.setOwner (s);
      blBox = blc.getBlBox ();
      blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner ());
      bl.setState (CBlock.BLOCK_EXECUTED);
      blBox.repaint ();
    }
    else{
      bl.setBlockInUseOnStart (false);
      bl.setState (CBlock.BLOCK_FREE);
      bl.setColor (bl.getColor ());
      bl.setDescription ("B" + bl.getBlNumber () + " ");
      blc = getBlContainer (iBlNr);
      blBox = blc.getBlBox ();
      blBox.setBackground (bl.getColor ());
      blBox.setText (blc.getDir () + bl.getDescription ());
      vBlElements = blc.getBlElements ();
      for (Enumeration e = vBlElements.elements ();e.hasMoreElements ();){
        CGbElement gbtmp = (CGbElement) e.nextElement ();
        gbtmp.activeColor = bl.getColor ();
        gbtmp.repaint();
      }
    }
    CControlCenter.saveBlockList ();
    save (sName);
    jLab2.setText ("fertig!");
  }

  private final boolean autoroute (){
    boolean bSuccess = false;
    boolean bFound = false;
    CGbElement [] gbarr = null;
    CGbElement gbtmp = gbStart;
    int iDiffX = gbStop.getXPosOnGb () - gbtmp.getXPosOnGb ();
    int iDiffY = gbStop.getYPosOnGb () - gbtmp.getYPosOnGb ();
    if (!vBlElements.contains (gbtmp))
      vBlElements.add (gbtmp);
    if (bForward){
      while (iDiffX > 0){
        bFound = false;
        if (gbtmp instanceof CGbSwitchEl && switchRight ((CGbSwitchEl)gbtmp)){
          //we try to create a new node and add it to the nodes-list
          if (createNode (gbtmp, LEFT_TO_RIGHT)){
            //we try tree_a first
            gbtmp = ((CNode)nodes.getLast ()).getTree_a ();
            //if tree_a leaded to nirvana, let`s try tree_b
            if (gbtmp == null)
              gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
          }
        }
        //"normal" element
        else{
          gbtmp = getNeighbour (gbtmp, LEFT_TO_RIGHT);
        }
        if (gbtmp != null){
          iDiffX = gbStop.getXPosOnGb () - gbtmp.getXPosOnGb ();
          iDiffY = gbStop.getYPosOnGb () - gbtmp.getYPosOnGb ();
          bFound = checkReachable (iDiffX, iDiffY);
          if (bFound && !vBlElements.contains(gbtmp))
            vBlElements.add (gbtmp);
        }
        //no matching element found
        if (gbtmp == null || !bFound){
          int iIndex;
          if (nodes.isEmpty()){
            return bSuccess;
          }
          else {
            iIndex = ((CNode)nodes.getLast ()).getRoot ().getXPosOnGb ();
            ((CNode)nodes.getLast ()).setTree_a (null);
            //remove elements in the wrong tree
            for (Enumeration en = vBlElements.elements (); en.hasMoreElements ();){
              CGbElement gbel = (CGbElement) en.nextElement ();
              if (iIndex < gbel.getXPosOnGb ())
                vBlElements.remove (gbel);
            }
            //let`s try the other tree
            gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
          }
          if (gbtmp != null){
            iDiffX = gbStop.getXPosOnGb () - gbtmp.getXPosOnGb ();
            iDiffY = gbStop.getYPosOnGb () - gbtmp.getYPosOnGb ();
            bFound = checkReachable (iDiffX, iDiffY);
            if (bFound && !vBlElements.contains(gbtmp))
              vBlElements.add (gbtmp);
            else gbtmp = null;
          }
          //whole node was wrong, so we delete it and go 1 node back
          if (gbtmp == null){
            vBlElements.remove(((CNode)nodes.getLast()).getRoot());
            nodes.removeLast();
            if (!nodes.isEmpty()){
              iIndex = ((CNode)nodes.getLast ()).getRoot ().getXPosOnGb ();
              ((CNode)nodes.getLast ()).setTree_a (null);
              for (Enumeration en = vBlElements.elements (); en.hasMoreElements ();){
                CGbElement gbel = (CGbElement) en.nextElement ();
                if (iIndex < gbel.getXPosOnGb ())
                  vBlElements.remove (gbel);
              }
              gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
              if (gbtmp != null && !vBlElements.contains(gbtmp))
                vBlElements.add (gbtmp);
            }
          }
        }
        if (gbtmp == null){
          return bSuccess;
        }
      }
      if (!vBlElements.contains(gbtmp))
        vBlElements.add (gbStop);
      bSuccess = true;
    }
    else if (!bForward){
      while (iDiffX < 0){
        bFound = false;
        if (gbtmp instanceof CGbSwitchEl && switchLeft ((CGbSwitchEl)gbtmp)){
          //we try to create a new node and add it to the nodes-list
          if (createNode (gbtmp, RIGHT_TO_LEFT)){
            //we try tree_a first
            gbtmp = ((CNode)nodes.getLast ()).getTree_a ();
            //if tree_a leaded to nirvana, let`s try tree_b
            if (gbtmp == null)
              gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
          }
        }
        else{
          gbtmp = getNeighbour (gbtmp, RIGHT_TO_LEFT);
        }
        if (gbtmp != null){
          iDiffX = gbStop.getXPosOnGb () - gbtmp.getXPosOnGb ();
          iDiffY = gbStop.getYPosOnGb () - gbtmp.getYPosOnGb ();
          bFound = checkReachable (iDiffX, iDiffY);
          if (bFound && !vBlElements.contains(gbtmp))
            vBlElements.add (gbtmp);
        }
        //no matching element found
        if (gbtmp == null || !bFound){
          int iIndex;
          if (nodes.isEmpty()){
            return bSuccess;
          }
          else {
            iIndex = ((CNode)nodes.getLast ()).getRoot ().getXPosOnGb ();
            ((CNode)nodes.getLast ()).setTree_a (null);
            for (Enumeration en = vBlElements.elements (); en.hasMoreElements ();){
              CGbElement gbel = (CGbElement) en.nextElement ();
              if (iIndex > gbel.getXPosOnGb ())
                vBlElements.remove (gbel);
            }
            gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
          }
          if (gbtmp != null ){
            iDiffX = gbStop.getXPosOnGb () - gbtmp.getXPosOnGb ();
            iDiffY = gbStop.getYPosOnGb () - gbtmp.getYPosOnGb ();
            bFound = checkReachable (iDiffX, iDiffY);
            if (bFound && !vBlElements.contains(gbtmp))
              vBlElements.add (gbtmp);
            else gbtmp = null;
          }
          //whole node was wrong, so we delete it and go 1 node back
          if (gbtmp == null){
            vBlElements.remove(((CNode)nodes.getLast()).getRoot());
            nodes.removeLast();
            if (!nodes.isEmpty()){
              iIndex = ((CNode)nodes.getLast ()).getRoot ().getXPosOnGb ();
              ((CNode)nodes.getLast ()).setTree_a (null);
              for (Enumeration e = vBlElements.elements (); e.hasMoreElements ();){
                CGbElement gbel = (CGbElement) e.nextElement ();
                if (iIndex > gbel.getXPosOnGb ())
                  vBlElements.remove (gbel);
              }
              gbtmp = ((CNode)nodes.getLast ()).getTree_b ();
              if (gbtmp != null && !vBlElements.contains(gbtmp))
                vBlElements.add (gbtmp);
            }
          }
        }
        if (gbtmp == null){
          return bSuccess;
        }
      }
      vBlElements.add (gbStop);
      bSuccess = true;
    }
    return bSuccess;
  }

  private final boolean createNode (CGbElement gbel, int iDir){
    boolean bOkay = false;
    CGbElement [] gbarr = null;
    CNode node = new CNode(gbel);
    gbarr = getNeighbours (gbel, iDir);
    node.setTree_a (gbarr[0]);
    if (gbarr[0] != null)
      node.setTree_b (gbarr[1]);
    if (node.getTree_a () instanceof CGbElement
      || node.getTree_b () instanceof CGbElement)
        bOkay = true;
    if (bOkay && nodes != null) nodes.addLast (node);
    return bOkay;
  }

  private final boolean switchRight (CGbSwitchEl swel){
    boolean bOkay = false;
    if (swel.getTyp () == 1 || swel.getTyp () == 2 || swel.getTyp () == 3
    || swel.getTyp () == 4 || swel.getTyp () == 9 || swel.getTyp () == 10 )
      bOkay = true;
    return bOkay;
  }

  private final boolean switchLeft (CGbSwitchEl swel){
    boolean bOkay = false;
    if (swel.getTyp () == 5 || swel.getTyp () == 6 || swel.getTyp () == 7
    || swel.getTyp () == 8 || swel.getTyp () == 9 || swel.getTyp () == 10 )
      bOkay = true;
    return bOkay;
  }

  private final CGbElement [] getNeighbours (CGbElement gbel, int iDir){
    CGbElement eltmp = null;
    boolean bFirst = true;
    CGbElement [] gbarr = new CGbElement [2];
    for (Enumeration en = elList.elements (); en.hasMoreElements ();){
      eltmp = (CGbElement) en.nextElement ();
      //right/left next to gbel
      if ((eltmp.getXPosOnGb () == gbel.getXPosOnGb () + iDir)
        //same horizontal line
        && (eltmp.getYPosOnGb () == gbel.getYPosOnGb ()
          //or 1 line lower
          || eltmp.getYPosOnGb () == gbel.getYPosOnGb () + 1
          //or 1 line higher
          || eltmp.getYPosOnGb () == gbel.getYPosOnGb () - 1)
        && checkFitting (gbel, eltmp, iDir)){
          if (bFirst) {
            gbarr[0] = eltmp;
            bFirst = false;
          }
          else gbarr[1] = eltmp;
      }
    }
    return gbarr;
  }

  private final CGbElement getNeighbour (CGbElement gbel, int iDir){
    CGbElement eltmp = null;
    CGbElement el = null;
    int iDeltaY = 0;
    //in case of a crossing we have to care for the other neighbour,
    //because they both have the same y-direction
    if (!(gbel instanceof CGbSwitchEl) && !(gbel instanceof CGbSignal)
    && (gbel.getTyp () == 8 || gbel.getTyp () == 9 || gbel.getTyp () == 10)){
      for (Enumeration e = vBlElements.elements (); e.hasMoreElements ();){
        el = (CGbElement) e.nextElement ();
        if (el.getXPosOnGb () == gbel.getXPosOnGb () - iDir)
          break;
      }
      iDeltaY = el.getYPosOnGb () - gbel.getYPosOnGb ();
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        eltmp = (CGbElement) en.nextElement ();
        //right next to gbel
        if ((eltmp.getXPosOnGb () == gbel.getXPosOnGb () + iDir)
          //same horizontal line
          && (eltmp.getYPosOnGb () == gbel.getYPosOnGb () - iDeltaY)
          && checkFitting (gbel, eltmp, iDir))
          return eltmp;
      }
    }
    else{
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        eltmp = (CGbElement) en.nextElement ();
        //right next to gbel
        if ((eltmp.getXPosOnGb () == gbel.getXPosOnGb () + iDir)
          //same horizontal line
          && (eltmp.getYPosOnGb () == gbel.getYPosOnGb ()
            //or 1 line lower
            || eltmp.getYPosOnGb () == gbel.getYPosOnGb () + 1
            //or 1 line higher
            || eltmp.getYPosOnGb () == gbel.getYPosOnGb () - 1)
          && checkFitting (gbel, eltmp, iDir))
          return eltmp;
      }
    }
    return null;
  }

  private final boolean checkFitting
    (CGbElement gbel_a, CGbElement gbel_b, int iDir){
    boolean bOkay = false;
    //if b is right to a
    if (iDir == LEFT_TO_RIGHT){
      //if on the same height
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb ()){
        if (gbel_a.getRm ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwRm ()
        && gbel_b.getLm ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwLm ())
          bOkay = true;
      }
      //if b is 1 line higher
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb () + 1){
        if (gbel_a.getRu ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwRu ()
        && gbel_b.getLd ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwLd ())
          bOkay = true;
      }
      //if b is 1 line lower
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb () - 1){
        if (gbel_a.getRd ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwRd ()
        && gbel_b.getLu ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwLu ())
          bOkay = true;
      }
    }
    //if b is left to a
    if (iDir == RIGHT_TO_LEFT){
      //if on the same height
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb ()){
        if (gbel_a.getLm ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwLm ()
        && gbel_b.getRm ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwRm ())
          bOkay = true;
      }
      //if b is 1 line higher
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb () + 1){
        if (gbel_a.getLu ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwLu ()
        && gbel_b.getRd ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwRd ())
          bOkay = true;
      }
      //if b is 1 line lower
      if (gbel_a.getYPosOnGb () == gbel_b.getYPosOnGb () - 1){
        if (gbel_a.getLd ()
          || gbel_a instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_a).getSwLd ()
        && gbel_b.getRu ()
          || gbel_b instanceof CGbSwitchEl && ((CGbSwitchEl)gbel_b).getSwRu ())
          bOkay = true;
      }
    }
    return bOkay;
  }

  private final void showRoute (){
    for (Enumeration en = vBlElements.elements (); en.hasMoreElements ();){
      CGbElement gbel = (CGbElement) en.nextElement ();
      gbel.setActiveColor (bl.getColor ());
      gbel.repaint ();
    }
  }

  private final CBlContainer createContainer (){
    CBlContainer blcont = null;
    if (vBlElements == null) vBlElements = new Vector ();
    blcont = new CBlContainer (vBlElements, bl.getBlNumber ());
    blcont.setBlBox (blBox);
    blBox.repaint ();
    if (! blList.contains (blcont)) blList.add (blcont);
    return blcont;
  }

  private final void createTurnoutJobList (CBlock block){
    CGbElement el_1 = null, el_2 = null, el_3 = null;
    int iXpos;
    if (! vBlElements.isEmpty ()){
      for (Enumeration en = vBlElements.elements (); en.hasMoreElements();){
        el_1 = (CGbElement) en.nextElement ();
        if (el_1 instanceof CGbSwitchEl){
          iXpos = el_1.getXPosOnGb ();
          if (switchRight ((CGbSwitchEl)el_1)){
            for (Enumeration enu = vBlElements.elements (); enu.hasMoreElements();){
              el_2 = (CGbElement) enu.nextElement ();
              //let´s fetch the right neighbour
              if (el_2.getXPosOnGb () == iXpos + 1) break;
            }
          }
          if (switchLeft ((CGbSwitchEl)el_1)){
            for (Enumeration enu = vBlElements.elements (); enu.hasMoreElements();){
              el_3 = (CGbElement) enu.nextElement ();
              //let´s fetch the left neighbour
              if (el_3.getXPosOnGb () == iXpos - 1) break;
            }
          }
          if ((el_1.getTyp () == 9 || el_1.getTyp () == 10)
          && (el_2!= null && el_3 != null)){
            //both have the same height
            if (el_1.getYPosOnGb () == el_3.getYPosOnGb ()
            && el_1.getYPosOnGb () == el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true, true));
            }
            if (el_1.getYPosOnGb () != el_3.getYPosOnGb ()
            && el_1.getYPosOnGb () != el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true, false));
            }
            if (el_1.getYPosOnGb () == el_3.getYPosOnGb ()
            && el_1.getYPosOnGb () != el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false, true));
            }
            if (el_1.getYPosOnGb () != el_3.getYPosOnGb ()
            && el_1.getYPosOnGb () == el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false, false));
            }
          }
          else if ((el_2 != null)
          && (el_1.getTyp () == 1 || el_1.getTyp () == 2)){
            //both have the same height
            if (el_1.getYPosOnGb () == el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true));
            }
            else blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false));
          }
          else if ((el_2 != null)
          && (el_1.getTyp () == 3 || el_1.getTyp () == 4)){
            if (el_1.getYPosOnGb () == el_2.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false));
            }
            else blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true));
          }
          else if ((el_3 != null)
          && (el_1.getTyp () == 5 || el_1.getTyp () == 6)){
            //both have the same height
            if (el_1.getYPosOnGb () == el_3.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true));
            }
            else blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false));
          }
          else if ((el_3 != null)
          && (el_1.getTyp () == 7 || el_1.getTyp () == 8)){
            if (el_1.getYPosOnGb () == el_3.getYPosOnGb ()){
              blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], false));
            }
            else blc.addBlJob (new CTurnoutJob
              (CControlCenter.getTurnoutList ()[((CGbSwitchEl)el_1).getId()], true));
          }
        }
      }
      for (Enumeration en = vBlElements.elements (); en.hasMoreElements ();){
        el_1 = (CGbElement) en.nextElement ();
        if (el_1 instanceof CGbSignal){
          int iTyp = el_1.getTyp ();
          //if left to right
          if (bForward){
            if (iTyp == 1 || iTyp == 4 || iTyp == 6 || iTyp == 7
                || iTyp == 9 || iTyp == 12 || iTyp == 13)
              blc.addBlJob (new CTurnoutJob (CControlCenter.getTurnoutList ()
                      [((CGbSignal)el_1).getId()], true));
            else blc.addBlJob (new CTurnoutJob (CControlCenter.getTurnoutList ()
                      [((CGbSignal)el_1).getId()], false));
          }
          //if right to left
          if (!bForward){
            if (iTyp == 1 || iTyp == 4 || iTyp == 6 || iTyp == 7
                || iTyp == 9 || iTyp == 12 || iTyp == 13)
              blc.addBlJob (new CTurnoutJob (CControlCenter.getTurnoutList ()
                      [((CGbSignal)el_1).getId()], false));
            else blc.addBlJob (new CTurnoutJob (CControlCenter.getTurnoutList ()
                      [((CGbSignal)el_1).getId()], true));
          }
        }
      }
    }
  }

  public final boolean checkReachable (int iX, int iY){
    boolean bOkay = false;
    if (iX < 0) iX = - iX;
    if (iY < 0) iY = - iY;
    if (iY <= iX) bOkay = true;
    return bOkay;
  }

  private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_34.htm#bluse"));
    hlp.hyperlinkUpdate (he);
  }

  public final void showError (){
    JOptionPane.showMessageDialog(
      panCenter.getParent (),
      "Abbruch, da Ziel nicht erreichbar!",
      "Fehler",
      JOptionPane.OK_OPTION);
    blc = null;
    panCenter.remove (blBox);
    blBox = null;
    nodes = null;
    panCenter.repaint ();
    refresh ();
    jLab.setText ("abgebrochen");
    jLab2.setText ("");
    CControlCenter.getBlockList ().remove (bl);
    bl = null;
  }

  private final void deleteBlock (){
    int iBlToDel = 0;
    String s = JOptionPane.showInputDialog(
      this,
      "Welcher Block soll gelöscht werden?",
      "Block löschen",
      JOptionPane.QUESTION_MESSAGE);
    if (s == null){
      iBlToDel = 0;
    }
    else {
      try{
        iBlToDel = Integer.parseInt (s);
      }
      catch (Exception e){
        iBlToDel = 0;
      }
    }
    if (iBlToDel > -1){
      CBlock block = getBlock (iBlToDel);
      CBlContainer blcont = getBlContainer (iBlToDel);
      if (block == null && blcont == null)
        JOptionPane.showMessageDialog(
          this,
          "Diesen Block finde ich nicht!",
          "Warnung",
          JOptionPane.OK_OPTION);
      else {
        int iConfirm = JOptionPane.showConfirmDialog(
          this,
          "Sicher, dass dieser Block in keinem Gleisbild, keiner Fahrstrasse und keiner Automatik verwendet wird?",
          "Achtung! Gefahr von Datenchaos!",
          JOptionPane.OK_CANCEL_OPTION);
        if (iConfirm != JOptionPane.OK_OPTION) return;
        if (blcont != null){
          if (blcont.getBlBox () != null)
            panCenter.remove (blcont.getBlBox ());
          if (blcont.getBlElements() != null){
            for (Enumeration en = blcont.getBlElements ().elements();
            en.hasMoreElements();){
              CGbElement gbtmp = (CGbElement) en.nextElement ();
              gbtmp.setActiveColor (Color.red);
              gbtmp.repaint();
            }
          }
        }
        panCenter.repaint ();
        blList.remove (blcont);
        CControlCenter.getBlockList ().remove (block);
        CControlCenter.saveBlockList ();
        save (sName);
        jLab.setText ("gelöscht wurde Block " + iBlToDel);
        jLab2.setText ("");
      }
    }
  }

  public final void getNewBlContainer (){
    blBox = new JButton (" ");
    blBox.setSize (3 * (int)(elementSize.getWidth () - 1),
    (int) elementSize.getHeight () - 1);
    blBox.setLocation (1, 1);
    blBox.setVisible (true);
    blBox.setBackground (bl.getColor ());
    panCenter.add (blBox);
    vBlElements = new Vector ();
    bl = getBlock (iBlNr);
    blc = createContainer ();
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == sm) {
      setToSmSize ();
    }
    else if (o == med) {
      setToMedSize ();
    }
    else if (o == sav){
      save (sName);
      CControlCenter.saveBlockList();
      panCenter.setSize (getGbSize ());
    }
    else if (o == saun){
      super.sName = "Neu";
      saveGb ();
      CControlCenter.saveBlockList();
      panCenter.setSize (getGbSize ());
    }
    else if (o == quit){
      JTrain.mf.setEditmode (false);
      setVisible (false);
      dispose ();
    }
    else if (o == open){
      panCenter.removeAll ();
      openGb ();
      this.setSize (getGbSize());
      elementSize = getElementSize ();
      //trick to display GbElements right under win
      if (elementSize.equals (smSize)){
        setMedSize ();
        setSmSize ();
      }
      else if (elementSize.equals (medSize)){
        setSmSize ();
        setMedSize ();
      }
    }
    else if (o == bnew){
      createNewBlock_1 ();
    }
    else if (o == bedit){
      CGbBlChangeDialog gbc = new CGbBlChangeDialog
      (this, "Block editieren", true);
      gbc.show();
      editBlock ();
    }
    else if (o == bdel){
      deleteBlock ();
    }
    else if (o == opt){
      optimizeSize ();
    }
    else if (o == hlp){
      try {showHelp ();}
      catch (MalformedURLException ex){}
    }
  }

  private class MyMouseAdapter extends MouseAdapter {
    public void mousePressed (MouseEvent e){
      Component c = e.getComponent ();
      if (c instanceof CGbElement && (bStep2 || bEditStep1)){
        gbStart = (CGbElement) e.getComponent ();
        if (bStep2) bStep2 = false;
        else if (bEditStep1){
          bEditStep1 = false;
          bEditStep2 = true;
        }
        createNewBlock_2 ();
      }
      else if (c instanceof CGbElement && bStep3){
        gbStop = (CGbElement) e.getComponent ();
        bStep3 = false;
        createNewBlock_3 ();
      }
      else if (bStep4 && (e.getClickCount() == 2
        || e.getModifiers () == MouseEvent.BUTTON3_MASK)){
           bStep4 = false;
           createNewBlock_4 ();
           return;
        }
      else if (c instanceof CGbElement && bStep4){
        gbStart = gbStop;
        gbStop = (CGbElement) e.getComponent ();
        createNewBlock_3 ();
      }
      else if (c instanceof CGbElement && bEditStep3){
        if (vBlElements.contains ((CGbElement)c)){
          vBlElements.remove ((CGbElement)c);
          ((CGbElement)c).activeColor = Color.red;
          ((CGbElement)c).repaint ();
        }
        else{
          vBlElements.add ((CGbElement)c);
          ((CGbElement)c).activeColor = bl.getColor ();
          ((CGbElement)c).repaint ();
        }
        panCenter.getParent().requestFocus();
      }
      else if (e.getModifiers () == MouseEvent.BUTTON3_MASK && bEditStep3){
        bEditStep3 = false;
        save (sName);
        CControlCenter.saveBlockList();
        jLab2.setText ("fertig!");
      }
      else if (c instanceof JButton && ((JButton)c).equals (blBox)
      && (bStep5 || bEditStep4)){
        tmpX = (int) e.getComponent ().getLocation ().getX ();
        tmpY = (int) e.getComponent ().getLocation ().getY ();
      }
    }
  }

  private class MyMouseMotionAdapter extends MouseMotionAdapter {
    public void mouseDragged (MouseEvent e){
      Component mouseChoice = e.getComponent ();
      if (mouseChoice instanceof JButton && ((JButton)mouseChoice).equals (blBox)
      && (bStep5 || bEditStep4)){
        /*if (!bLinux){*/
            tmpX = (int) e.getComponent ().getLocation ().getX ();
            tmpY = (int) e.getComponent ().getLocation ().getY ();
        /*  }*/
        mouseChoice.setLocation ( tmpX + e.getX () - 50, tmpY + e.getY () - 6);
      }
    }
  }

  private class MyKeyAdapter extends KeyAdapter {
    public void keyPressed (KeyEvent e){
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE && bEditStep4){
        bEditStep4 = false;
        save (sName);
        CControlCenter.saveBlockList();
        jLab2.setText ("fertig!");
      }
      else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && bStep5
      || e.getKeyCode() == KeyEvent.VK_ENTER && bStep5){
        bStep5 = false;
        createNewBlock_5 ();
      }
      //big steps with "ctrl"-pressed
      else if (e.getKeyCode() == KeyEvent.VK_RIGHT
      && e.getModifiers() == 2 && (bStep5 || bEditStep4)){
        if (blBox.getX () < panCenter.getWidth() - 4 * (int)(elementSize.getWidth() -1))
          blBox.setLocation (blBox.getX () + (int)elementSize.getWidth() - 1,
          blBox.getY ());
      }
      else if (e.getKeyCode() == KeyEvent.VK_LEFT
      && e.getModifiers() == 2 && (bStep5 || bEditStep4)){
        if (blBox.getX () > (int)elementSize.getWidth() - 1)
          blBox.setLocation (blBox.getX () - (int)elementSize.getWidth() + 1,
          blBox.getY ());
      }
      else if (e.getKeyCode() == KeyEvent.VK_DOWN
      && e.getModifiers() == 2 && (bStep5 || bEditStep4)){
        if (blBox.getY () < panCenter.getHeight ()
        - 2 * (int)(elementSize.getHeight() - 1))
          blBox.setLocation (blBox.getX (),
          blBox.getY () + (int)(elementSize.getHeight() - 1));
      }
      else if (e.getKeyCode() == KeyEvent.VK_UP
      && e.getModifiers() == 2 && (bStep5 || bEditStep4)){
        if (blBox.getY () > (int)(elementSize.getHeight() - 1))
          blBox.setLocation (blBox.getX (),
          blBox.getY () - (int)(elementSize.getHeight() - 1));
      }
      //small steps without modifier key
      else if (e.getKeyCode() == KeyEvent.VK_RIGHT && (bStep5 || bEditStep4)){
        if (blBox.getX () < panCenter.getWidth() - 3 * (int)(elementSize.getWidth() -1))
          blBox.setLocation (blBox.getX () + 1,
          blBox.getY ());
      }
      else if (e.getKeyCode() == KeyEvent.VK_LEFT && (bStep5 || bEditStep4)){
        if (blBox.getX () > 1)
          blBox.setLocation (blBox.getX () - 1,
          blBox.getY ());
      }
      else if (e.getKeyCode() == KeyEvent.VK_DOWN && (bStep5 || bEditStep4)){
        if (blBox.getY () < panCenter.getHeight ()
        - (int)(elementSize.getHeight() - 1))
          blBox.setLocation (blBox.getX (),
          blBox.getY () + 1);
      }
      else if (e.getKeyCode() == KeyEvent.VK_UP && (bStep5 || bEditStep4)){
        if (blBox.getY () > (int)(elementSize.getHeight() - 1))
          blBox.setLocation (blBox.getX (),
          blBox.getY () - 1);
      }
    }
  }

  private class GbWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      CControlCenter.saveBlockList ();
      JTrain.mf.setEditmode (false);
    }
  }
}

