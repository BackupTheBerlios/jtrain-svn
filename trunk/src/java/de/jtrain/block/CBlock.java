/*
This class defines the blocks needed to control safe train-driving

last modified: 2002 04 22
author: Werner Kunkel
*/

package de.jtrain.block;
import java.util.*;
import java.io.*;
import java.awt.*;

import de.jtrain.com.CControlCenter;

public class CBlock implements Serializable {

  private int iBlNumber               = 0;
  private boolean bBlockInUseOnStart  = false;
  private Color color                 = new Color (100, 100, 100);
  private int iBlockNeeded            = 0;
  private Vector vRequestors          = new Vector ();
  private String sDescription         = null;
  private String sOwner               = "";
  private int iState                  = 0;
  public final static int BLOCK_FREE         = 1;
  public final static int BLOCK_REQUESTED    = 2;
  public final static int BLOCK_EXECUTED     = 3;

  public CBlock (int iNumber){
    this.iBlNumber = iNumber;
  }

  public void setBlNumber (int iNr)           {this.iBlNumber = iNr;}
  public int getBlNumber ()                   {return iBlNumber;}
  public void setState (int iState)           {this.iState = iState;}
  public int getState ()                      {return iState;}
  public void setBlockInUseOnStart (boolean b){bBlockInUseOnStart = b;}
  public boolean getBlockInUseOnStart ()      {return bBlockInUseOnStart;}
  public void setColor (Color c)              {color = c;}
  public Color getColor ()                    {return color;}
  public void setRequestors (Vector v)        {this.vRequestors = v;}
  public Vector getRequestors ()              {return vRequestors;}
  public void setDescription (String sDes)    {this.sDescription = sDes;}
  public String getDescription ()             {return sDescription;}
  public void setOwner (String sOwner)        {this.sOwner = sOwner;}
  public String getOwner ()                   {return sOwner;}
  public int getBlockNeeded ()                {return iBlockNeeded;}

  public boolean setBlockNeeded (int iNr){
    boolean bOkay = false;
    CBlock bltmp;
    Iterator iter = CControlCenter.getBlockList().iterator();
    while(iter.hasNext())
    {
      bltmp = (CBlock) iter.next();
      if (bltmp.getBlNumber() == iNr){
        bOkay = true;
        break;
      }
    }
    this.iBlockNeeded = iNr;
    return bOkay;
  }

}

