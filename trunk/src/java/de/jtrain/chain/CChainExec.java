/*
the class for execution of automatic train rides
last modified: 2002 04 22
author: Werner Kunkel
*/

package de.jtrain.chain;

import java.awt.*;
import javax.swing.*;

import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFeedbackportHandler;
import de.jtrain.control.CLoco;
import de.jtrain.control.CLocoAccClock;
import de.jtrain.control.CLocoEvent;
import de.jtrain.control.CLocoEventHandler;
import de.jtrain.event.CFeedbackportEvent;
import de.jtrain.event.CFeedbackportListener;
import de.jtrain.gb.CGbExec;
import de.jtrain.gb.CStep;

import java.util.*;

public class CChainExec implements Runnable, CFeedbackportListener {

  private CChainFrame master            = null;
  private CGbExec parent                = null;
  private CStep actualStep              = null;
  private CChain chain                  = null;
  private CLoco loco                    = null;
  private boolean bNextStepCond         = false;
  private boolean bNextAlterStepCond    = false;
  private boolean bTimerIsOver          = false;
  public  final static int NOTRUNNING   = 0;
  public  final static int RUNNING      = 1;
  private int iState                    = NOTRUNNING;
  private int iFbWaiter                 = -1;
  private CLocoAccClock lac             = null;

  CChainExec (CChainFrame master, CChain chain, CGbExec parent){
    super ();
    this.master         = master;
    this.chain          = chain;
    this.parent         = parent;
    this.loco           = chain.getLoco ();
    this.actualStep     = chain.getFirstStep ();
    chain.setStop (false);
    for (Enumeration en = chain.getSteps (); en.hasMoreElements ();)
      ((CStep) en.nextElement ()).setBackground (Color.white);
    CFeedbackportHandler.getFeedbackportHandler().addFeedbackportListener (this);
  }

  public void setState (int iState) {this.iState = iState;}

  public void run (){
    bNextStepCond = false;
    if (loco != null) loco.setLocoBusy (true);
    actualStep.setBackground (Color.yellow);
    actualStep.repaint ();
    doStepJobs (actualStep);
    iState = RUNNING;
    while (iState == RUNNING){
      while (!bNextStepCond && !bNextAlterStepCond && iState == RUNNING){
        try{
          Thread.sleep (200L);
          checkNextStepCond ();
        }
        catch (InterruptedException e){
          iState = NOTRUNNING;
        }
      }
      if (iState != RUNNING) break;
      actualStep.setBackground (Color.white);
      actualStep.repaint ();
      if (bNextStepCond)
        actualStep = actualStep.getStepTrue ();
      else if (bNextAlterStepCond)
        actualStep = actualStep.getStepFalse();
      else{
        showError ();
        iState = NOTRUNNING;
        return ;
      }
      if (actualStep == null){
        showError ();
        iState = NOTRUNNING;
        return ;
      }
      //if stop is pressed by the user
      if (actualStep.getNumber () == 1 && chain.getStop () == true){
        if (loco != null) loco.setLocoBusy (false);
        break;
      }
      //update the steparea
      master.getScrollPane ().getViewport ().setViewPosition
      (new Point (1, (actualStep.getY () - 100) > 0 ? actualStep.getY () -100 : 1));
      actualStep.setBackground (Color.yellow);
      actualStep.repaint ();
      bNextStepCond = false;
      bNextAlterStepCond = false;
      doStepJobs (actualStep);
    }
  }

  public final void checkNextStepCond (){
    if (actualStep == null || actualStep.getNextStepCondition() == null){
      showError ();
      iState = NOTRUNNING;
      return ;
    }
    if (actualStep.getNextStepCondition ().startsWith ("w")){
      bNextStepCond = true;
      return;
    }
    else if (actualStep.getNextStepCondition ().startsWith ("t")){
      if (bTimerIsOver){
        bNextStepCond = true;
        bTimerIsOver = false;
        return;
      }
      else bNextStepCond = false;
    }
    else if (actualStep.getNextStepCondition ().startsWith ("b")){
      int iParsed = -1;
      StringTokenizer stok = new StringTokenizer (actualStep.getNextStepCondition());
      if (stok.hasMoreTokens ()) stok.nextToken ();
      if (stok.hasMoreTokens ()){
        String s = stok.nextToken ();
        try{
          iParsed = Integer.parseInt (s);
        }
        catch (Exception e){}
        if (iParsed <= 0) {
          showError ();
          iState = NOTRUNNING;
          return;
        }
        else {
          Iterator iter = CControlCenter.getBlockList ().iterator();
          while(iter.hasNext())
          {
            CBlock bl = (CBlock)iter.next();
            if (bl.getBlNumber () == iParsed){
              if (bl.getOwner ().equals (chain.getName ())
              && bl.getState() == CBlock.BLOCK_EXECUTED)
                bNextStepCond = true;
              break;
            }
          }
        }
      }
    }
    else if (actualStep.getNextStepCondition ().startsWith ("c")){
      int iParsed = -1;
      StringTokenizer stok = new StringTokenizer (actualStep.getNextStepCondition());
      if (stok.hasMoreTokens ()) stok.nextToken ();
      if (stok.hasMoreTokens ()){
        String s = stok.nextToken ();
        try{
          iParsed = Integer.parseInt (s);
        }
        catch (Exception e){}
        if (iParsed <= 0) {
          showError ();
          iState = NOTRUNNING;
          return;
        }
        else {
          if (iParsed == iFbWaiter){
            bNextStepCond = true;
            iFbWaiter = -1;
          }
          else bNextStepCond = false;
        }
      }
    }
    if (actualStep.getStepFalseExists())
      bNextAlterStepCond = true;
  }


  public final void doStepJobs (CStep step){
    if (step.getJobs() == null || step.getJobs().isEmpty()) return;
    for (Enumeration en = step.getJobs ().elements (); en.hasMoreElements ();)
      parseStepJob ((String)en.nextElement ());
  }

  public final void parseStepJob (String sJob){
    String s = null;
    int iParsed = -1;
    StringTokenizer stok = new StringTokenizer (sJob, " ,");
    if (stok.hasMoreTokens ()){
      s = stok.nextToken ();
      if (s.startsWith ("Anforderung")){
        if (stok.hasMoreTokens ())
          s = stok.nextToken ();
        while (stok.hasMoreTokens ()){
          s = stok.nextToken ();
          try {
            iParsed = Integer.parseInt (s);
          }
          catch (Exception ex){}
          if (iParsed > 0){
            parent.requestBlock (iParsed, chain.getName ());
          }
        }
      }
      //weiter
      else if (s.startsWith ("Rückgabe")){
        if (stok.hasMoreTokens ())
          s = stok.nextToken ();
        if (stok.hasMoreTokens ()){
          s = stok.nextToken ();
          try {
            iParsed = Integer.parseInt (s);
          }
          catch (Exception ex){}
          if (iParsed > 0){
            parent.releaseBlock (iParsed, chain.getName ());
          }
        }
      }
      else if (s.startsWith ("Timer")){
        if (stok.hasMoreTokens ()){
          s = stok.nextToken ();
          try {
            iParsed = Integer.parseInt (s);
          }
          catch (Exception ex){}
          if (iParsed > 0){
            CTimer timer = new CTimer (iParsed);
            timer.start ();
          }
        }
      }
      else if (s.startsWith ("Magnetartikel")){
        if (stok.hasMoreTokens ()){
          s = stok.nextToken ();
          try {
            iParsed = Integer.parseInt (s);
          }
          catch (Exception ex){}
          if (iParsed > 0){
            if (stok.hasMoreTokens()){
              s = stok.nextToken();
              if (s.equals ("grün"))
                parent.sendCommand (iParsed, true, false);
              else if (s.equals ("rot"))
                parent.sendCommand (iParsed, false, false);
            }
          }
        }
      }
      else if (s.startsWith ("Lok") && loco != null){
        String sDir = "", sF = "", sF1 = "", sF2 = "", sF3 = "", sF4 = "";
        while (stok.hasMoreTokens ()){
          if (stok.nextToken ().equals("SPEED")){
            s = stok.nextToken ();
            try {
              iParsed = Integer.parseInt (s);
            }
            catch (Exception ex){}
            if (iParsed > 0){
              sDir = stok.nextToken();
              sF   = stok.nextToken();
              sF1  = stok.nextToken();
              sF2  = stok.nextToken();
              sF3  = stok.nextToken();
              sF4  = stok.nextToken();
            }
          }
        }
        if (loco.getAccByJBahn ()){
          if (lac != null && lac.getIState () == CLocoAccClock.NOTRUNNING
          && loco.getSpeed() != iParsed){
            lac.start ();
          }
          if (lac == null || !lac.isAlive()){
            lac = new CLocoAccClock (loco);
            lac.start ();
          }
          lac.setStartSpeed (loco.getSpeed ());
          lac.setEndSpeed (iParsed);
        }
        else loco.setSpeed (iParsed);
        if (sDir.equals ("FORWARD"))
          loco.setDirection (1);
        else if (sDir.equals ("BACKWARD"))
          loco.setDirection (0);
        if (sF.equals("ON"))
          loco.setFunc (1);
        else if (sF.equals("OFF"))
          loco.setFunc (0);
        if (sF1.equals("ON"))
          loco.setF1 (1);
        else if (sF1.equals("OFF"))
          loco.setF1 (0);
        if (sF2.equals("ON"))
          loco.setF2 (1);
        else if (sF2.equals("OFF"))
          loco.setF2 (0);
        if (sF3.equals("ON"))
          loco.setF3 (1);
        else if (sF3.equals("OFF"))
          loco.setF3 (0);
        if (sF4.equals("ON"))
          loco.setF4 (1);
        else if (sF4.equals("OFF"))
          loco.setF4 (0);
        loco.sendLcString();
        int iFF = loco.getF1() + loco.getF2() << 1
        + loco.getF3() << 2 + loco.getF4() << 3;
        CLocoEventHandler.fireEvent (new CLocoEvent
        (this, loco.getAddr (), loco.getDirection (), loco.getSpeed (),
         loco.getSpeedsteps(), loco.getFunc(), loco.getNrOF (), iFF));
      }
    }
  }

  class CTimer extends Thread{
    int iTime ;

    CTimer (int iTime){
      super ();
      this.iTime = iTime;
    }

    public void run (){
      bTimerIsOver = false;
      try{
        Thread.sleep ((long) iTime);
        bTimerIsOver = true;
      }
      catch (InterruptedException e){
        bTimerIsOver = true;
      }
    }
  }

  public void handleEvent (CFeedbackportEvent e){
    int iPort;
    int iState;
    iPort = e.getPort();
    iState = e.getState();
    if (iState == 1){
      iFbWaiter = iPort;
//      System.out.println("fb "+iFbWaiter);
    }
  }

  public final void showError (){
    JOptionPane.showMessageDialog(
    null,
    "Fehler in Kette bei Step " + actualStep.getNumber (),
    "Kettenfehler",
    JOptionPane.OK_OPTION);
  }
}

