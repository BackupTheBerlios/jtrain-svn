/*
A class to hold the vector with the blockelements, the switching jobs,
the blocknumber and the blockbox

last modified: 2002 04 22
author: Werner Kunkel
*/

package de.jtrain.block;
import java.util.*;
import javax.swing.*;

import de.jtrain.turnout.CTurnoutJob;

import java.io.*;

public class CBlContainer implements Serializable{
  private Vector vBlElements  = null;
  private Vector vBlJobs      = new Vector ();
  private Integer iBlNumber   = null;
  private JButton blBox       = null;
  private String sDir         = null;

  public CBlContainer (Vector v, int i){
    super ();
    this.iBlNumber = new Integer (i);
    this.vBlElements = v;
  }

  public Vector getBlElements ()              {return vBlElements ;}
  public int getBlNumber ()                   {return iBlNumber.intValue();}
  public void setBlNumber (int iNr)           {this.iBlNumber = new Integer (iNr);}
  public JButton getBlBox ()                  {return blBox;}
  public void setBlBox (JButton jl)           {this.blBox = jl;}
  public String getDir ()                     {return sDir;}
  public void setDir (String s)               {this.sDir = s;}
  public void addBlJob (CTurnoutJob toj)      {vBlJobs.add (toj);}
  public void removeBlJob (CTurnoutJob toj)   {vBlJobs.remove (toj);}
  public boolean blJobsIsEmpty ()             {return vBlJobs.isEmpty();}
  public Enumeration getEnBlJobs (){
    if (!vBlJobs.isEmpty ()){
      Enumeration en = vBlJobs.elements();
      return en;
    }
    else return null;
  }
}

