/*
the class to hold all the steps and their visualization
last modified: 2002 04 05
author: Werner Kunkel
*/

package de.jtrain.chain;

import java.awt.*;
import javax.swing.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.config.CLang;
import de.jtrain.control.CLoco;
import de.jtrain.gb.CStep;

import java.util.*;
import java.io.*;

public class CChain extends Panel implements Serializable{
  private String sName      = null;
  private CStep firstStep   = null;
  private boolean bStart    = false;
  private boolean bStop     = false;
  private CLoco loco        = null;
  private Vector vSteps     = new Vector ();
  private boolean bEdit     = false;
  public final static int iSizeX    = 280;
  public final static int iSizeY    = 4000;
  public final static int RENAME    = 1;
  public final static int COPY      = 2;
  public final static int OUTPUT    = 3;
  public final static int DELETE    = 4;
  public final static int CHANGE    = 5;
  public final static int LOCOCHG   = 6;

  CChain (){
    super ();
    this.setBackground (new Color (254, 252, 152));
    this.setSize (iSizeX, iSizeY);
    this.setLayout (null);
  }

  public String getName (){return sName;}
  public void setName (String s){this.sName = s;}
  public void setFirstStep (CStep first) {this.firstStep = first;}
  public CStep getFirstStep () {return firstStep;}
  public void setStart (boolean b) {this.bStart = b;}
  public boolean getStart () {return this.bStart;}
  public void setStop (boolean b) {this.bStop = b;}
  public boolean getStop () {return this.bStop;}
  public void setLoco (CLoco loco) {this.loco = loco;}
  public CLoco getLoco () {return loco;}
  public void putStep (CStep step) {vSteps.add (step);}
  public void setSteps (Vector v){this.vSteps = v;}
  public Vector getVSteps (){return vSteps;}
  public Enumeration getSteps () {return vSteps.elements();}
  public void removeStep (CStep step) {vSteps.remove (step);}
  public void setEdit (boolean b) {bEdit = b;}

  public final void showPropWindow (CStep step){
    propWindow pw = new propWindow (step);
    pw.show ();
  }

  class propWindow extends JFrame {
    CStep step          = null;
    Container c         = null;
    JPanel p            = new JPanel ();
    JPanel p1           = new JPanel ();
    JPanel p2           = new JPanel ();
    JPanel p3           = new JPanel ();
    JPanel p4           = new JPanel ();
    JPanel pw           = new JPanel ();
    JPanel pe           = new JPanel ();
    JPanel pn           = new JPanel ();
    JPanel ps           = new JPanel ();
    JPanel psw          = new JPanel ();
    JPanel pse          = new JPanel ();
    BorderLayout bl     = new BorderLayout (10, 10);
    GridLayout gl       = new GridLayout (4, 1, 10, 5);
    GridLayout gl1      = new GridLayout (1, 1, 10, 5);
    JLabel jL1          = new JLabel (CLang.s1);
    JLabel jL2          = new JLabel (CLang.s2);
    JLabel jL3          = new JLabel (CLang.s3);
    JLabel jL4          = new JLabel (CLang.s4);
    JLabel jL5          = new JLabel (CLang.s5);
    JLabel jL1a         = new JLabel ();
    JLabel jL2a         = new JLabel ();
    JLabel jL3a         = new JLabel ();
    JLabel jL4a         = new JLabel ();
    JList list          = new JList ();

    propWindow (CStep step){
      super ();
      this.step = step;
      this.setTitle(CLang.s6 + step.getNumber());
      this.setSize (new Dimension (300, 250));
      this.setResizable (false);
      this.setLocation (CControlCenter.getOrigin ());
      c = this.getContentPane();
      c.setBackground (Color.white);
      c.setLayout (bl);
      p.setLayout (gl);
      p1.add (jL1);
      jL1a.setText (Integer.toString (step.getNumber()));
      p1.add (jL1a);
      p2.add (jL2);
      jL2a.setText (step.getNextStepCondition ());
      p2.add (jL2a);
      p3.add (jL3);
      if (step.getStepTrue () != null)
        jL3a.setText (Integer.toString (step.getStepTrue ().getNumber ()));
      p3.add (jL3a);
      p4.add (jL4);
      if (step.getStepFalse () != null)
        jL4a.setText (Integer.toString (step.getStepFalse ().getNumber ()));
      p4.add (jL4a);
      p.add (p1);
      p.add (p2);
      p.add (p3);
      p.add (p4);
      c.add (p, BorderLayout.CENTER);
      c.add (pw, BorderLayout.WEST);
      c.add (pe, BorderLayout.EAST);
      c.add (pn, BorderLayout.NORTH);
      psw.add (jL5);
      psw.setBackground (Color.white);
      list.setListData (step.getJobs());
      pse.setLayout (gl1);
      pse.add (new JScrollPane (list));
      pse.setPreferredSize (new Dimension (200, 80));
      ps.add (psw);
      ps.add (pse);
      c.add (ps, BorderLayout.SOUTH);
      for (int i = 0; i < c.getComponentCount(); i++)
        c.getComponent (i).setBackground (Color.white);
      for (int i = 0; i < p.getComponentCount(); i++)
        p.getComponent (i).setBackground (Color.white);
      this.repaint ();
    }
  }

  public void paint (Graphics g){
    CStep tmpStep;
    if (bEdit){
      //painting the points
      g.setColor (Color.lightGray);
      for (int iX = 0; iX < this.getWidth (); iX += 10)
        for (int iY = 0; iY < this.getHeight (); iY += 10)
          g.drawRect (iX, iY, 1, 1);
    }
    //green connections
    g.setColor (Color.green);
    for (Enumeration en = vSteps.elements (); en.hasMoreElements ();){
      tmpStep = (CStep) en.nextElement ();
      if (tmpStep != null &&tmpStep.getStepTrue () != null){
        g.drawLine ((int)tmpStep.getLocation ().getX () + 16,
          (int)tmpStep.getLocation().getY () + 31,
          (int)tmpStep.getStepTrue ().getLocation ().getX() + 16,
          (int)tmpStep.getStepTrue ().getLocation ().getY() + 1);
      }
    }
    //red connections
    g.setColor (Color.red);
    for (Enumeration en = vSteps.elements (); en.hasMoreElements ();){
      tmpStep = (CStep) en.nextElement ();
      if (tmpStep != null && tmpStep.getStepFalse () != null){
        if (tmpStep.getStepFalse ().getX () > tmpStep.getX ()){
          g.drawLine ((int)tmpStep.getLocation ().getX () + 30,
            (int)tmpStep.getLocation().getY () + 31,
            (int)tmpStep.getStepFalse ().getLocation ().getX() + 16,
            (int)tmpStep.getStepFalse ().getLocation ().getY() + 1);
        }
        else {
          g.drawLine ((int)tmpStep.getLocation ().getX () + 1,
            (int)tmpStep.getLocation().getY () + 31,
            (int)tmpStep.getStepFalse ().getLocation ().getX() + 16,
            (int)tmpStep.getStepFalse ().getLocation ().getY() + 1);
        }
      }
    }
  }

  public void printChain (){
    System.out.print ("Name: ");
    System.out.println (sName);
    System.out.print ("Lok: ");
    System.out.println (loco.getName ());
    System.out.print ("Name: ");
    System.out.println (sName);
    for (Enumeration en = vSteps.elements (); en.hasMoreElements ();){
      CStep step = (CStep) en.nextElement ();
      System.out.println ("Step " + step.getNumber());
      for (Enumeration enu = step.getJobs().elements(); enu.hasMoreElements ();)
        System.out.println ("Job: " + (String)enu.nextElement ());
      System.out.println ("Nächster Schritt: " + step.getStepTrue().getNumber());
      System.out.println ("Schrittbedingung: " + step.getNextStepCondition ());
      if (step.getStepFalseExists ())
        System.out.println ("Bedingter Schritt: "
        + step.getStepFalse ().getNumber ());
    }
  }
}

