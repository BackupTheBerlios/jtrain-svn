/*
this class defines the switching-job a block has to

last modified: 2002 03 09
author: Werner Kunkel
*/
package de.jtrain.turnout;
import java.io.*;


public class CTurnoutJob implements Serializable{

  private CTurnout to;
  private boolean bToGreen;
  private boolean bHor;

  public CTurnoutJob (CTurnout to, boolean bToGreen){
    this.to = to;
    this.bToGreen = bToGreen;
    this.bHor = false;
  }

  public CTurnoutJob (CTurnout to, boolean bToGreen, boolean bHor){
    this.to = to;
    this.bToGreen = bToGreen;
    this.bHor = bHor;
  }

  public CTurnout getTurnout (){return to;}
  public boolean getToGreen () {return bToGreen;}
  public boolean getHor ()     {return bHor;}
}

