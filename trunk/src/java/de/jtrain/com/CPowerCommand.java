package de.jtrain.com;
/*
small extension of the command-class for emergency-stop/go

last modified: 2002 02 20
author: Werner Kunkel
*/

public class CPowerCommand extends CCommand {

  private static String sOn  = "SET POWER ON\n";
  private static String sOf  = "SET POWER OFF\n";
  boolean bOn                = false;

  CPowerCommand (){}

  public static void sendPowerCommand (boolean bOn){
    CPowerCommand pc = new CPowerCommand ();
    if (bOn == true ) pc.setCommand (sOn);
    if (bOn == false) pc.setCommand (sOf);
    pc.setTime (50);
    CCommandPipe.putLcCommand (pc);
  }
}

