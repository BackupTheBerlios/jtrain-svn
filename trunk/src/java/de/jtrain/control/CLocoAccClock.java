package de.jtrain.control;
/**/

public class CLocoAccClock extends Thread {

  private CLoco loco            = null;
  private int iStartSpeed       = 0;
  private int iEndSpeed         = 0;
  public final static int NOTRUNNING   = 0;
  public final static int RUNNING      = 1;
  private int iState            = NOTRUNNING;

  public CLocoAccClock (CLoco loco){
    this.loco        = loco;
  }

  public void setStartSpeed (int iStartSpeed){this.iStartSpeed = iStartSpeed;}
  public void setEndSpeed (int iEndSpeed){this.iEndSpeed = iEndSpeed;}
  public void setIState (int iState){this.iState = iState;}
  public int getIState (){return iState;}

  public void run (){
    iState = RUNNING;
    while (loco.getSpeed() != iEndSpeed){
      try{
        if (loco.getSpeed () < iEndSpeed){
          loco.setSpeed (loco.getSpeed () + 1);
          Thread.sleep (loco.getAccTime());
        }
        else if (loco.getSpeed () > iEndSpeed){
          loco.setSpeed (loco.getSpeed () - 1);
          Thread.sleep (loco.getDecTime ());
        }
        loco.sendLcString ();
      }
      catch (InterruptedException e){
        iState = NOTRUNNING;
      }
    }
    iState = NOTRUNNING;
  }
}

