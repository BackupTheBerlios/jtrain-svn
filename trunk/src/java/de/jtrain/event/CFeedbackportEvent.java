package de.jtrain.event;
/*
this class gives access to the feedbackportevents

last modified: 2002 02 14
author: Werner Kunkel, adapted from Torsten Vogt
*/


public class CFeedbackportEvent extends java.util.EventObject {

   private int iPort      = 0;
   private int iState     = 0;

   public CFeedbackportEvent (Object source, int iPort, int iState) {
      super (source);
      this.iPort    = iPort;
      this.iState   = iState;
   }

   public int getPort()    { return iPort; }
   public int getState()   { return iState;   }
}

