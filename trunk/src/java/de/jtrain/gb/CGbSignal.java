/*
this class defines the signals and how to paint them

last modified : 2002 03 13
author : Werner Kunkel
*/

package de.jtrain.gb;
import java.awt.*;
import java.io.*;

public class CGbSignal extends CGbElement {

  private int id;   //Die Nummer in der Magnetartikel-Liste
  protected boolean bIsGreen = false;
  protected boolean bSig_rd = false;
  protected boolean bSig_md = false;
  protected boolean bSig_ld = false;
  protected boolean bSig_ru = false;
  protected boolean bSig_mu = false;
  protected boolean bSig_lu = false;
  protected boolean bSig_ldru_u = false;
  protected boolean bSig_ldru_d = false;
  protected boolean bSig_lurd_u = false;
  protected boolean bSig_lurd_d = false;

  public CGbSignal (int iTyp) {
    super (iTyp);
    this.iTyp = iTyp;
    this.setBackground (new Color (254, 254, 200));
    setGraph (iTyp);
  }

  public void setId (int id){
    this.id=id;
  }

  public int getId (){
    return id;
  }

  public void setIsGreen (boolean mode){
    bIsGreen = mode;
    this.repaint();
  }

  public boolean getIsGreen (){
    return bIsGreen;
  }

  public void setGraph (int iTyp){

    if (iTyp == 1 || iTyp == 2){    //straight
      bLm = true;
      bRm = true;
      if (iTyp == 1) bSig_md = true;
      if (iTyp == 2) bSig_mu = true;
    }
    if (iTyp == 3 || iTyp == 4){    //left straight > right up
      bLm = true;
      bRu = true;
      if (iTyp == 3) bSig_lu = true;
      if (iTyp == 4) bSig_ld = true;
    }
    if (iTyp == 5 || iTyp == 6){    //left straight > right down
      bLm = true;
      bRd = true;
      if (iTyp == 5) bSig_lu = true;
      if (iTyp == 6) bSig_ld = true;
    }
    if (iTyp == 7 || iTyp == 8){    //left up > right straight
      bLu = true;
      bRm = true;
      if (iTyp == 7) bSig_rd = true;
      if (iTyp == 8) bSig_ru = true;
    }
    if (iTyp == 9 || iTyp == 10){    //left down > right straight
      bLd = true;
      bRm = true;
      if (iTyp == 9) bSig_rd = true;
      if (iTyp == 10) bSig_ru = true;
    }
    if (iTyp == 11){
      bLd = true;
      bRu = true;
      bSig_ldru_u = true;
    }
    if (iTyp == 12){
      bLd = true;
      bRu = true;
      bSig_ldru_d = true;
    }
    if (iTyp == 13){
      bLu = true;
      bRd = true;
      bSig_lurd_u = true;
    }
    if (iTyp == 14){
      bLu = true;
      bRd = true;
      bSig_lurd_d = true;
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
      if (bSig_rd == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (9,4,7,5);
          g.setColor (Color.green);
          g.fillRect (1,4,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (9,4,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (1,4,7,5);
        }
      }
      if (bSig_md == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (1,4,7,5);
          g.setColor (Color.green);
          g.fillRect (-7,4,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (1,4,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-7,4,7,5);
        }
      }
      if (bSig_ld == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (-15,4,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-7,4,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (-15,4,7,5);
          g.setColor (Color.red);
          g.fillRect (-7,4,7,5);
        }
      }
      if (bSig_lu == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-15,-8,7,5);
          g.setColor (Color.green);
          g.fillRect (-7,-8,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-15,-8,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-7,-8,7,5);
        }
      }
      if (bSig_mu == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (1,-8,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-7,-8,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (1,-8,7,5);
          g.setColor (Color.red);
          g.fillRect (-7,-8,7,5);
        }
      }
      if (bSig_ru == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (9,-8,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (1,-8,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (9,-8,7,5);
          g.setColor (Color.red);
          g.fillRect (1,-8,7,5);
        }
      }
      if (bSig_ldru_u == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-15,-4,7,5);
          g.setColor (Color.green);
          g.fillRect (-7,-7,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-15,-4,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-7,-7,7,5);
        }
      }
      if (bSig_ldru_d == true){
         if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (9,0,7,5);
          g.setColor (Color.green);
          g.fillRect (1,3,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (9,0,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (1,3,7,5);
        }
      }
      if (bSig_lurd_d == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (1,-7,7,5);
          g.setColor (Color.green);
          g.fillRect (9,-4,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (1,-7,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (9,-4,7,5);
        }
      }
      if (bSig_lurd_u == true){
         if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-7,3,7,5);
          g.setColor (Color.green);
          g.fillRect (-15,0,7,5);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-7,3,7,5);
          g.setColor (Color.lightGray);
          g.fillRect (-15,0,7,5);
        }
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
      if (bSig_rd == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (13,4,9,6);
          g.setColor (Color.green);
          g.fillRect (2,4,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (13,4,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (2,4,9,6);
        }
      }
      if (bSig_md == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (1,4,9,6);
          g.setColor (Color.green);
          g.fillRect (-10,4,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (1,4,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-10,4,9,6);
        }
      }
      if (bSig_ld == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (-21,4,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-10,4,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (-21,4,9,6);
          g.setColor (Color.red);
          g.fillRect (-10,4,9,6);
        }
      }
      if (bSig_lu == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-21,-10,9,6);
          g.setColor (Color.green);
          g.fillRect (-10,-10,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-21,-10,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-10,-10,9,6);
        }
      }
      if (bSig_mu == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (1,-10,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-10,-10,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (1,-10,9,6);
          g.setColor (Color.red);
          g.fillRect (-10,-10,9,6);
        }
      }
      if (bSig_ru == true){
        if (bIsGreen == true){
          g.setColor (Color.green);
          g.fillRect (13,-10,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (2,-10,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.lightGray);
          g.fillRect (13,-10,9,6);
          g.setColor (Color.red);
          g.fillRect (2,-10,9,6);
        }
      }
       if (bSig_ldru_u == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-21,-5,9,6);
          g.setColor (Color.green);
          g.fillRect (-10,-9,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-21,-5,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-10,-9,9,6);
        }
      }
      if (bSig_ldru_d == true){
         if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (12,0,9,6);
          g.setColor (Color.green);
          g.fillRect (1,4,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (12,0,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (1,4,9,6);
        }
      }
      if (bSig_lurd_d == true){
        if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (1,-9,9,6);
          g.setColor (Color.green);
          g.fillRect (12,-5,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (1,-9,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (12,-5,9,6);
        }
      }
      if (bSig_lurd_u == true){
         if (bIsGreen == true){
          g.setColor (Color.lightGray);
          g.fillRect (-10,4,9,6);
          g.setColor (Color.green);
          g.fillRect (-21,0,9,6);
        }
        if (bIsGreen == false){
          g.setColor (Color.red);
          g.fillRect (-10,4,9,6);
          g.setColor (Color.lightGray);
          g.fillRect (-21,0,9,6);
        }
      }
    }
  }
}



