package de.jtrain.event;
/*
the interface for feedbackportevents

last modified: 2002 02 14
author: Werner Kunkel
adapted from Thorsten Vogt
*/


public interface CFeedbackportListener extends java.util.EventListener {
   public void handleEvent (CFeedbackportEvent e);
}

