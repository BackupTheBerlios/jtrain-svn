package de.jtrain.config;
import de.jtrain.com.CControlCenter;

/*
This class "translates" all String
last modified: 2002 04 25
author: Werner Kunkel
*/

public class CLang {

  static CLang lang = null;

  public static String s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14,s15,s16,s17,s18,
  s19,s20,s21,s22,s23,s24,s25,s26,s27,s28,s29,s30,s31,s32,s33,s34,s35,s36,s37,
  s38,s39,s40,s41,s42,s43,s44,s45,s46,s47,s48,s49,s50,s51,s52,s53,s54,s55,s56,
  s57,s58,s59,s60,s61,s62,s63,s64,s65,s66,s67,s68,s69,s70,s71,s72,s73,s74,s75;

  public CLang (){
    initLang ();
  }

  public static CLang getLang (){
    if (lang == null) lang = new CLang ();
    return lang;
  }
  private final void initLang () {
    if (CControlCenter.getSetting("language").equals ("deutsch")){
    //CChain
      s1 = "Schrittnummer:";
      s2 = "Schrittbedingung:";
      s3 = "nächster Schritt:";
      s4 = "Verzweigung zu Step:";
      s5 = "Jobs:";
      s6 = "Eigenschaften von Step ";
    //CChainEditor
      s7 = "Neuer Step";
      s8 = "Abbrechen";
      s9 = "Speichern";
      s10 = "Ende";
      s11 = "Bitte Namen (< 7 Buchstaben) vergeben:";
      s12 = "Neuer Name";
      s13 = ": JTrain - Stepfläche";
      s14 = ": Automatikfahrt- Editor";
      s15 = "Kette ";
      s16 = " Lok ";
      s17 = "Editor für Step ";
      s18 = "< keine Bedingung >";
      s19 = "Rückmelder erreicht";
      s20 = "Kontakt-Nr.";
      s21 = "Timer abgelaufen";
      s22 = "Block zugeteilt";
      s23 = "Block-Nr.";
      s24 = "Anforderung";
      s25 = "Rückgabe";
      s26 = "Timer ";
      s27 = "Magnetartikel ";
      s28 = "Lok ";
      s29 = "Nächster Step:";
      s30 = "Bedingung:";
      s31 = "Nummer:";
      s32 = "Blöcke anfordern";
      s33 = "Block freigeben";
      s34 = "Lokbefehl";
      s35 = "Timer setzen";
      s36 = "Magnetartikel stellen";
      s37 = "Das kann ich leider nicht: Diesen Step/Block kenne ich nicht!";
      s38 = "Falsche Eingabe";
      s39 = "Rückmelder:";
      s40 = "Timer-> ohne Nr.";
      s41 = "Block:";
      s42 = "Bitte Nummern der anzuforderden Blöcke durch Komma getrennt eingeben:";
      s43 = "Stepjobs eingeben";
      s44 = "Geht leider nicht! Block ist mir unbekannt oder Tippfehler!";
      s45 = "Warnung!";
      s46 = "Anforderung Block ";
      s47 = "Bitte Nummer des zurückzugebenden Blocks eingeben:";
      s48 = "Rückgabe Block ";
      s49 = "Geht leider nicht! Block ist mir unbekannt!";
      s50 = "Bitte Zeit in ms für Timer eingeben:";
      s51 = "Leider keine sinnvolle Zeit eingegeben!";
      s52 = "Sinnlos, da Kette ohne Lok!";
      s53 = "Stepjobs: Lokbefehl erzeugen";
      s54 = "Stepjobs: Magnetartikelbefehl erzeugen";
    }
    else {
      s1 = "number of step:";
      s2 = "stepcondition:";
      s3 = "next step:";
      s4 = "next alter step:";
      s5 = "jobs:";
      s6 = "properties of step ";
      s7 = "new step";
      s8 = "abort";
      s9 = "save";
      s10 = "the end";
      s11 = "please enter a name:";
      s12 = "new name";
      s13 = "JTrain -steparea";
      s14 = ":automatic-ride-editor";
      s15 = "chain ";
      s16 = " loco ";
      s17 = "editor for step ";
      s18 = "without condition";
      s19 = "feedback-sensor reached";
      s20 = "sensor-nr.";
      s21 = "time`s over";
      s22 = "block assigned";
      s23 = "block-nr.";
      s24 = "request";
      s25 = "cancel";
      s26 = "timer ";
      s27 = "turnout ";
      s28 = "loco ";
      s29 = "next step";
      s30 = "condition";
      s31 = "number";
      s32 = "request blocks";
      s33 = "release block";
      s34 = "loco order";
      s35 = "set timer";
      s36 = "set turnout";
      s37 = "Sorry, it`s impossible: i don`t know this step/block!";
      s38 = "input error";
      s39 = "feedback-contact:";
      s40 = "timer-> no number";
      s41 = "block: ";
      s42 = "Please enter the numbers of the blocks to request, separated by point!";
      s43 = "enter stepjobs";
      s44 = "Sorry, impossible: block unknown or input error!";
      s45 = "Warning!";
      s46 = "request block";
      s47 = "Please enter number of the block to release:";
      s48 = "release block ";
      s49 = "Sorry, impossible! Block is unknown!";
      s50 = "Please enter time for timer in ms:";
      s51 = "Sorry, no usefull time entered!";
      s52 = "No use: chain without loco!";
      s53 = "stepjobs: create loco command";
      s54 = "Stepjobs: create turnout command";
    }
  }
}

