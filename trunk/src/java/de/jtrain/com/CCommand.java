package de.jtrain.com;
/*
This class defines the structure of a command; + acces to the components

last modified: 2002 02 20
author: Werner Kunkel
*/

public class CCommand {

  private String sCommand;
  private int iTime;

  public CCommand (String s, int i){
    super ();
    this.sCommand = s;
    this.iTime = i;
  }

  public CCommand (){
    super ();
  }


  public String getCommand (){
    return sCommand;
  }

  public void setCommand (String s){
    sCommand = s;
  }

  public int getTime (){
    return iTime;
  }

  public void setTime (int i){
    iTime = i;
  }
}

