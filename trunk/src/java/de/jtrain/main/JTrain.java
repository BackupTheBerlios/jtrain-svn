package de.jtrain.main;

/*JTrain vers. 0.1- software for controlling digital modell-railroads
    Copyright (C) 2002  Werner Kunkel

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation; either version 2 of
    the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

import javax.swing.UIManager;

import de.jtrain.com.CControlCenter;

public class JTrain 
{
  public static CMainFrame mf;


  private JTrain()
  {
   CControlCenter.getControlCenter ();
   try {
     UIManager.setLookAndFeel (System.getProperty
     ("lookandfeel", UIManager.getSystemLookAndFeelClassName()));
   }
   catch(Exception e) {
     e.printStackTrace();
   }
   boolean warning = false;
   mf = new CMainFrame("J-Train Modellbahnsteuerung");
   mf.show ();
  }
  
  public static void main (String[] args) 
  {
  	  new JTrain();
  }
}

