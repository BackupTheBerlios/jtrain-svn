/*
This class defines the blockevents and functions to get infos from them.
last modified: 2002 03 16
author: Werner Kunkel
*/

package de.jtrain.event;
import java.util.*;

public class CBlockEvent extends EventObject {

  private int iBlockNumber;
  private int iBlockState;
  private String sRequestor;

  public CBlockEvent (Object source, int iBlNr, int iBlState, String sRequestor){
    super (source);
    this.iBlockNumber = iBlNr;
    this.iBlockState = iBlState;
    this.sRequestor = sRequestor;
  }

  public int getBlNumber (){
    return iBlockNumber;
  }

  public int getBlState (){
    return iBlockState;
  }

  public String getRequestor (){
    return sRequestor;
  }
}

