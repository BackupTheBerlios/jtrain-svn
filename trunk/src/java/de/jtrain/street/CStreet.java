/*
the class to define everything needed for streets as a combination of blocks

last modified : 2002 03 23
author : Werner Kunkel
*/

package de.jtrain.street;
import java.util.*;
import java.io.*;

import de.jtrain.block.CBlock;

public class CStreet implements Serializable{

  private Vector vBlocks  = new Vector ();
  private String sName    = null;
  private boolean bInUse  = false;

  public CStreet (){}

  public void setName (String s){this.sName = s;}
  public String getName (){return sName;}

  public void addBlock (CBlock bl){
    vBlocks.add (bl);
  }

  public Enumeration getBlocks () {
    return vBlocks.elements ();
  }

  public void setInUse (boolean b){
    this.bInUse = b;
  }

  public boolean getInUse (){
    return bInUse;
  }
}