/*
the class defining the step of an automatic ride
last modified: 2002 04 05
author: Werner Kunkel
*/

package de.jtrain.gb;
import java.awt.*;
import java.util.*;
import java.io.*;

import de.jtrain.chain.CChain;

public class CStep extends Panel implements Serializable{
  private int iNumber                 = 0;
  private Vector vJobs                = null;
  private CStep nextStepTrue          = null;
  private CStep nextStepFalse         = null;
  private boolean bStepFalse          = false;
  private String sNextStepCondition   = null;
  private final Dimension stepSize    = new Dimension (32, 32);
  private CChain chain                = null;

  public CStep () {
    super ();
    this.setSize (stepSize);
    this.setBackground (Color.white);
    this.vJobs = new Vector ();
    this.setVisible (true);
  }

  public int getNumber () {return iNumber;}
  public void setNumber (int iNumber) {this.iNumber = iNumber;}
  public void putJob (String sJob) {vJobs.add (sJob); }
  public Vector getJobs () {return vJobs;}
  public CStep getStepTrue () {return nextStepTrue;}
  public void setStepTrue (CStep step) {this.nextStepTrue = step;}
  public CStep getStepFalse () {return nextStepFalse;}
  public void setStepFalse (CStep step) {this.nextStepFalse = step;}
  public boolean getStepFalseExists () {return bStepFalse;}
  public void setStepFalseExists (boolean bExists) {this.bStepFalse = bExists;}
  public String getNextStepCondition () {return sNextStepCondition;}
  public void setNextStepCondition (String sCondition)
    {this.sNextStepCondition = sCondition;}

  public void paint (Graphics g){
    int iX = 0;
    if (iNumber < 10) iX = 12;
    else if (iNumber >=10 && iNumber < 100) iX = 8;
    else if (iNumber >= 100) iX = 4;
    g.setColor (Color.black);
    g.drawString (Integer.toString (iNumber), iX, 20);
    g.fillRect (14, 0, 5, 5);
    g.setColor (Color.green);
    g.fillRect (14, 27, 5, 5);
    g.setColor (Color.red);
    if (bStepFalse == false){
      g.drawRect (27, 27, 4, 4);
      g.drawRect (0, 27, 4, 4);
      }
    else{
      if (nextStepFalse.getX() > this.getX ()){
        g.fillRect (27, 27, 5, 5);
        g.drawRect (0, 27, 5, 5);
      }
      else {
        g.drawRect (27, 27, 5, 5);
        g.fillRect (0, 27, 5, 5);
      }
    }
  }
}
