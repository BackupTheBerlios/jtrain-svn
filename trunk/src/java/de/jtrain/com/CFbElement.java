/**/

package de.jtrain.com;
import java.awt.*;
import java.io.*;
import javax.swing.*;

import de.jtrain.gb.CGb;

public class CFbElement extends Canvas implements Serializable {

  final static int [] smXpoints = {0,2,4,6,6,4,2,0};
  final static int [] smYpoints = {-2,0,0,-2,-4,-6,-6,-4,-2};
  final static int [] medXpoints = {0,3,6,9,9,6,3,0};
  final static int [] medYpoints = {-3,0,0,-3,-6,-9,-9,-6,-3};

  private boolean active                  = false;
  private int id                          = 0;
  private static Dimension elementSize    = null;
  private CGb owner                       = null;
  final static Color inactiveColor        = Color.white;
  final static Color activeColor          = Color.black;

  public CFbElement (CGb owner) {
    super ();
    this.owner = owner;
    this.setBackground (new Color (254,252,152));
  }

  public void setId (int nr){
    this.id = nr;
  }

  public int getId (){
    return id;
  }

  public void setActive (boolean state){
    active = state;
    repaint();
  }

  public boolean getActive (){
    return active;
  }

  public static Dimension getMedSize (){
    return new Dimension (10,10);
  }

  public static Dimension getSmSize (){
    return new Dimension (7,7);
  }

  public final int getFbId (JFrame owner){
    String ids = null;
    int id = 0;
    do{
      ids = (String) JOptionPane.showInputDialog(
        owner,
        "Bitte geben Sie die Rückmeldekontaktnummer (1-1024) ein:",
        "Rückmeldekontakt eingeben",
        JOptionPane.QUESTION_MESSAGE);

      if (ids == null) ids = "";
      try{
        id = Integer.parseInt (ids);
      }
      catch (Exception ex){};
    }
    while (id < 1 || id > 1024);
    return id;
  }

  public void paint (Graphics g){
    if (CGb.getElementSize() == CGb.medSize){
      g.translate (0,9);
      if (active) g.setColor (activeColor);
      else g.setColor(inactiveColor);
      g.fillPolygon (medXpoints, medYpoints, 8);
      g.setColor (Color.black);
      g.drawPolygon (medXpoints, medYpoints, 8);
    }
    else{
      g.translate (0,6);
      if (active) g.setColor (activeColor);
      else g.setColor(inactiveColor);
      g.fillPolygon (smXpoints, smYpoints, 8);
      g.setColor (Color.black);
      g.drawPolygon (smXpoints, smYpoints, 8);
    }
  }
}

