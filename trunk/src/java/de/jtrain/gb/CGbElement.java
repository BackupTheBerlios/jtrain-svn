/*
this class defines the trackelements and how to paint them

last modified : 2002 04 05
author : Werner Kunkel
*/

package de.jtrain.gb;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class CGbElement extends Canvas{

  protected int iTyp;
  private boolean bIsFree = true;
  private int iXPosOnGb, iYPosOnGb;
  protected Color activeColor = Color.red;


  final static int [] smXpointsLm = {0,-2,-14,-16,-14,-2,0};     //x-links-Mitte
  final static int [] smXpointsLc = {0,-2,-12,-16,-16,-2,0};      //x-links-Ecken
  final static int [] smXpointsRc = {0,2,12,16,16,2,0};          //x-rechts-Ecken
  final static int [] smXpointsRm = {0,2,14,16,14,2,0};         //x-rechts-Mitte
  final static int [] smYpointsLu = {0,-3,-7,-7,-5,1,0};           //y-links oben>Mitte
  final static int [] smYpointsLm = {0,2,2,0,-2,-2,0};            //y-links-Mitte
  final static int [] smYpointsLd = {0,3,7,7,5,-1,0};              //y-links unten>Mitte
  final static int [] smYpointsRu = {0,-3,-7,-7,-5,1,0};          //y-Mitte>rechts oben
  final static int [] smYpointsRm = {0,2,2,0,-2,-2,0};           //y-rechts-Mitte
  final static int [] smYpointsRd = {0,3,7,7,5,-1,0};             //y-Mitte>rechts unten

  final static int [] medXpointsLm = {0,-5,-18,-23,-19,-4,0};
  final static int [] medXpointsLc = {0,0,-17,-23,-23,-6,0};       //
  final static int [] medXpointsRm = {0,5,18,23,19,4,0};
  final static int [] medXpointsRc = {0,0,17,23,23,6,0};           //
  final static int [] medYpointsLu = {0,-3,-10,-10,-7,0,0};         //
  final static int [] medYpointsLm = {0,-3,-3,0,2,2,0};
  final static int [] medYpointsLd = {0,3,10,10,7,0,0};
  final static int [] medYpointsRu = {0,-3,-10,-10,-7,0,0};
  final static int [] medYpointsRm = {0,-3,-3,0,2,2,0};
  final static int [] medYpointsRd = {0,3,10,10,7,0,0};

  protected boolean bLu = false;
  protected boolean bLm = false;
  protected boolean bLd = false;
  protected boolean bRu = false;
  protected boolean bRm = false;
  protected boolean bRd = false;
  protected boolean end = false;

  CGbElement (int iTyp){
    super ();
    this.iTyp = iTyp;
    setGraph (iTyp);
    this.setBackground (new Color (254, 252, 152));
  }

  public boolean  getLu ()   {return bLu;}
  public boolean  getLm ()   {return bLm;}
  public boolean  getLd ()   {return bLd;}
  public boolean  getRu ()   {return bRu;}
  public boolean  getRm ()   {return bRm;}
  public boolean  getRd ()   {return bRd;}
  public boolean getIsFree (){return bIsFree;}
  public void setIsFree (boolean b)   {this.bIsFree = b;}
  public void setActiveColor (Color c){this.activeColor = c;}

  public void setGraph (int iTyp){

    if (iTyp == 1){    //gerade
      bLm = true;
      bRm = true;
    }

    else if (iTyp == 2){    //links gerade > rechts oben
      bLm = true;
      bRu = true;
    }

    else if (iTyp == 3){    //links gerade > rechts unten
      bLm = true;
      bRd = true;
    }

    else if (iTyp == 4){    //links oben > rechts gerade
      bLu = true;
      bRm = true;
    }

    else if (iTyp == 5){    //links unten > rechts gerade
      bLd = true;
      bRm = true;
    }

    else if (iTyp == 6){    //nach unten
      bLu = true;
      bRd = true;
    }

    else if (iTyp == 7){    //nach oben
      bLd = true;
      bRu = true;
    }

    else if (iTyp == 8){    //X mitte / nach unten
      bLm = true;
      bRm = true;
      bLu = true;
      bRd = true;
    }

    else if (iTyp == 9){    //X mitte / nach oben
      bLm = true;
      bRm = true;
      bLd = true;
      bRu = true;
    }

    else if (iTyp == 10){   //X nach oben / nach unten
      bLu = true;
      bRd = true;
      bLd = true;
      bRu = true;
    }

    else if (iTyp == 11){   //links mitte > ende
      bLm = true;
      end = true;
    }

    else if (iTyp == 12){   //links oben > ende
      bLu = true;
      end = true;
    }

    else if (iTyp == 13){   //links unten > ende
      bLd = true;
      end = true;
    }

    else if (iTyp == 14){   //rechts mitte > ende
      bRm = true;
      end = true;
    }

    else if (iTyp == 15){   //rechts oben > ende
      bRu = true;
      end = true;
    }

    else if (iTyp == 16){   //rechts unten > ende
      bRd = true;
      end = true;
    }
  }

  public void paint (Graphics g){

    if (CGb.elementSize == CGb.smSize){
      g.translate (16,7);
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
      if (end == true){
        g.setColor (Color.lightGray);
        g.fillOval(-4,-4,8,8);
      }
    }
    if (CGb.elementSize == CGb.medSize){
      g.translate (23,10);
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
      if (end == true){
        g.setColor (Color.lightGray);
        g.fillOval(-5,-5,10,10);
      }
    }
  }

  public void setTyp (int iTyp){
    iTyp =iTyp;
    return;
  }

  public int getTyp (){
    return iTyp;
  }

  public void setPosOnGb (int x,int y){
    iXPosOnGb = x;
    iYPosOnGb = y;
  }

  public  int getXPosOnGb (){
    return iXPosOnGb;
  }

  public  int getYPosOnGb (){
    return iYPosOnGb;
  }

  public Dimension getPreferredSize (){
    return CGb.smSize;
  }

  public Dimension getMinimumSize (){
    return CGb.smSize;
  }

}

