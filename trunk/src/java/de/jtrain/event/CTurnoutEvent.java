/*
This class defines the turnoutevents and functions to get infos from them.
last modified: 2002 03 13
author: Werner Kunkel
*/

package de.jtrain.event;
import java.util.*;

public class CTurnoutEvent extends EventObject {

  private int iTurnoutId;
  private boolean bIsGreen;
  private boolean bHorizontal;

  public CTurnoutEvent (Object source, int iTurnoutId, boolean bIsGreen, boolean bHor){
    super (source);
    this.iTurnoutId = iTurnoutId;
    this.bIsGreen = bIsGreen;
    this.bHorizontal = bHor;
  }

  public int getTurnoutId (){
    return iTurnoutId;
  }

  public boolean getIsGreen (){
    return bIsGreen;
  }

  public boolean getHorizontal (){
    return bHorizontal;
  }
}

