/*
This class defines the locoevents and the functions to deal with them

last modified : 2002 02 14
author : Werner Kunkel
*/

package de.jtrain.control;
import java.util.EventObject;

public class CLocoEvent extends EventObject 
{

  protected int     iAddr      = 0;
  protected int     iSpeed     = 0;
  protected int     iVmax      = 0;
  protected int     iDirection = 1;
  protected int     iFunc      = 0;
  protected int     iNrOF      = 0;
  protected int     iFF        = 0;

  public CLocoEvent (Object source, int iAddr, int iDirection, int iSpeed,
   int iVmax, int iFunc, int iNrOF, int iFF) 
  {
    super (source);
    this.iAddr              = iAddr;
    this.iSpeed             = iSpeed;
    this.iVmax              = iVmax;
    this.iDirection         = iDirection;
    this.iFunc              = iFunc;
    this.iNrOF              = iNrOF;
    this.iFF                = iFF;
  }

  public int getAddr ()         {return iAddr;}
  public int getSpeed ()        {return iSpeed;}
  public int getVmax ()         {return iVmax;}
  public int getDirection ()    {return iDirection;}
  public int getFunc ()         {return iFunc;}
  public int getNrOF ()         {return iNrOF;}
  public int getFF ()           {return iFF;}
}

