/*
this class defines the turnouttrackelements and how to paint them

last modified : 2002 03 15
author : Werner Kunkel
*/

package de.jtrain.gb;
import java.awt.*;

public class CGbSwitchEl extends CGbElement {

  private int id;   //
  private int iTyp;
  private boolean bIsGreen = true;
  private boolean bSwlu = false;
  private boolean bSwlm = false;
  private boolean bSwld = false;
  private boolean bSwru = false;
  private boolean bSwrm = false;
  private boolean bSwrd = false;
  private boolean bHorizontal = false;

  public boolean  getSwLu (){return bSwlu;}
  public boolean  getSwLm (){return bSwlm;}
  public boolean  getSwLd (){return bSwld;}
  public boolean  getSwRu (){return bSwru;}
  public boolean  getSwRm (){return bSwrm;}
  public boolean  getSwRd (){return bSwrd;}
  public boolean  getHorizontal (){return bHorizontal;}
  public void setHorizontal (boolean b){this.bHorizontal = b;}

  public CGbSwitchEl (int iTyp) {
    super (iTyp);
    this.setBackground (new Color (254, 254, 200));
    this.iTyp = iTyp;
    setGraph (iTyp);
  }

  public void setId (int id){
    this.id = id;
  }

  public int getId (){return id;}

  public final void setIsGreen (boolean bMode){
    this.bIsGreen = bMode;
    this.setGraph (iTyp);
    this.repaint();
  }

  public final void setGraph (int iTyp){
    if (iTyp == 1){   //Turnout left_middle -right_middle/up
      bLm = true;
      if (bIsGreen == true){
        bSwrm = false;
        bRm = true;
        bRu = false;
        bSwru = true;
      }
      else {
        bRm = false;
        bSwrm = true;
        bSwru = false;
        bRu = true;
      }
    }
    else if (iTyp == 2){   //Turnout left_middle -right_middle/down
      bLm = true;
      if (bIsGreen == true){
        bSwrm = false;
        bRm = true;
        bRd = false;
        bSwrd = true;
      }
      else {
        bRm = false;
        bSwrm = true;
        bSwrd = false;
        bRd = true;
      }
    }
    else if (iTyp == 3){   //Turnout left_up -right_down/middle
      bLu = true;
      if (bIsGreen == true){
        bSwrd = false;
        bRd = true;
        bRm = false;
        bSwrm = true;
      }
      else {
        bRd = false;
        bSwrd = true;
        bSwrm = false;
        bRm = true;
      }
    }
    else if (iTyp == 4){   //Turnout left_down -right_up/middle
      bLd = true;
      if (bIsGreen == true){
        bSwru = false;
        bRu = true;
        bRm = false;
        bSwrm = true;
      }
      else {
        bRu = false;
        bSwru = true;
        bSwrm = false;
        bRm = true;
      }
    }
    else if (iTyp == 5){   //Turnout left_middle/up -right_middle
      bRm = true;
      if (bIsGreen == true){
        bLu = false;
        bSwlu = true;
        bSwlm = false;
        bLm = true;
      }
      else {
        bSwlu = false;
        bLu = true;
        bLm = false;
        bSwlm = true;
      }
    }
    else if (iTyp == 6){   //Turnout left_middle/down -right_middle
      bRm = true;
      if (bIsGreen == true){
        bSwlm = false;
        bLm = true;
        bLd = false;
        bSwld = true;
      }
      else {
        bLm = false;
        bSwlm = true;
        bSwld = false;
        bLd = true;
      }
    }
    else if (iTyp == 7){   //Turnout left_middle/up -right_down
      bRd = true;
      if (bIsGreen == true){
        bSwlu = false;
        bLu = true;
        bLm = false;
        bSwlm = true;
      }
      else {
        bLu = false;
        bSwlu = true;
        bSwlm = false;
        bLm = true;
      }
    }
    else if (iTyp == 8){   //Turnout left_middle/down -right_up
      bRu = true;
      if (bIsGreen == true){
        bSwld = false;
        bLd = true;
        bLm = false;
        bSwlm = true;
      }
      else {
        bLd = false;
        bSwld = true;
        bSwlm = false;
        bLm = true;
      }
    }
    else if (iTyp == 9){   //Xcross left_middle/up -right_middle/down
      if (getHorizontal ()){
        if (bIsGreen == true){
          bLm = true;
          bLu = false;
          bSwlm = false;
          bSwlu = true;
          bRm = true;
          bRd = false;
          bSwrm = false;
          bSwrd = true;
        }
        else {
          bLm = true;
          bLu = false;
          bSwlm = false;
          bSwlu = true;
          bRd = true;
          bRm = false;
          bSwrm = true;
          bSwrd = false;
        }
      }
      else {
        if (bIsGreen == true){
          bLm = false;
          bLu = true;
          bSwlm = true;
          bSwlu = false;
          bRm = false;
          bRd = true;
          bSwrm = true;
          bSwrd = false;
        }
        else{
          bLm = false;
          bLu = true;
          bSwlm = true;
          bSwlu = false;
          bRm = true;
          bRd = false;
          bSwrm = false;
          bSwrd = true;
        }
      }
    }
    else if (iTyp == 10){   //Xcross left_middle/down -right_middle/up
      if (getHorizontal()){
        if (bIsGreen == true){
          bLm = true;
          bLd = false;
          bSwlm = false;
          bSwld = true;
          bRm = true;
          bRu = false;
          bSwrm = false;
          bSwru = true;
        }
        else {
          bLm = true;
          bLd = false;
          bSwlm = false;
          bSwld = true;
          bRu = true;
          bRm = false;
          bSwrm = true;
          bSwru = false;
        }
      }
      else {
        if (bIsGreen == true){
          bLm = false;
          bLd = true;
          bSwlm = true;
          bSwld = false;
          bRm = false;
          bRu = true;
          bSwrm = true;
          bSwru = false;
        }
        else {
          bLm = false;
          bLd = true;
          bSwlm = true;
          bSwld = false;
          bRm = true;
          bRu = false;
          bSwrm = false;
          bSwru = true;
        }
      }
    }
  }

  public void paint (Graphics g){
    if (CGb.elementSize == CGb.smSize){
      g.translate (16,7);
      if (bSwlu == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsLc,smYpointsLu,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLc,smYpointsLu,6);
      }
      if (bSwlm == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsLm,smYpointsLm,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLm,smYpointsLm,6);
      }
      if (bSwld == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsLc,smYpointsLd,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLc,smYpointsLd,6);
      }
      if (bSwru == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsRc,smYpointsRu,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRc,smYpointsRu,6);
      }
      if (bSwrm == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsRm,smYpointsRm,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRm,smYpointsRm,6);
      }
      if (bSwrd == true){
        g.setColor (Color.white);
        g.fillPolygon (smXpointsRc,smYpointsRd,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRc,smYpointsRd,6);
      }
      if (bLu == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsLc,smYpointsLu,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLc,smYpointsLu,6);
      }
      if (bLm == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsLm,smYpointsLm,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLm,smYpointsLm,6);
      }
      if (bLd == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsLc,smYpointsLd,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsLc,smYpointsLd,6);
      }
      if (bRu == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsRc,smYpointsRu,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRc,smYpointsRu,6);
      }
      if (bRm == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsRm,smYpointsRm,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRm,smYpointsRm,6);
      }
      if (bRd == true){
        g.setColor (activeColor);
        g.fillPolygon (smXpointsRc,smYpointsRd,6);
        g.setColor (Color.black);
        g.drawPolygon (smXpointsRc,smYpointsRd,6);
      }
    }
    if (CGb.elementSize == CGb.medSize){
      g.translate (23,10);
      if (bSwlu == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsLc,medYpointsLu,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLc,medYpointsLu,6);
      }
      if (bSwlm == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsLm,medYpointsLm,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLm,medYpointsLm,6);
      }
      if (bSwld == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsLc,medYpointsLd,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLc,medYpointsLd,6);
      }
      if (bSwru == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsRc,medYpointsRu,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRc,medYpointsRu,6);
      }
      if (bSwrm == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsRm,medYpointsRm,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRm,medYpointsRm,6);
      }
      if (bSwrd == true){
        g.setColor (Color.white);
        g.fillPolygon (medXpointsRc,medYpointsRd,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRc,medYpointsRd,6);
      }
      if (bLu == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsLc,medYpointsLu,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLc,medYpointsLu,6);
      }
      if (bLm == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsLm,medYpointsLm,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLm,medYpointsLm,6);
      }
      if (bLd == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsLc,medYpointsLd,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsLc,medYpointsLd,6);
      }
      if (bRu == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsRc,medYpointsRu,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRc,medYpointsRu,6);
      }
      if (bRm == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsRm,medYpointsRm,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRm,medYpointsRm,6);
      }
      if (bRd == true){
        g.setColor (activeColor);
        g.fillPolygon (medXpointsRc,medYpointsRd,6);
        g.setColor (Color.black);
        g.drawPolygon (medXpointsRc,medYpointsRd,6);
      }
    }
  }
}
