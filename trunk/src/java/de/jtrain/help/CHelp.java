/*
The class for the help-viewer
last modified: 2002 04 02
author: Werner Kunkel
*/

package de.jtrain.help;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.jtrain.com.CControlCenter;
import de.jtrain.config.Config;

import java.io.*;
import java.util.*;

public class CHelp extends JFrame implements ActionListener, HyperlinkListener{

  private JEditorPane editorPane  = new JEditorPane ();
  private Stack urlStack          = new Stack ();
  private JPanel jPan             = new JPanel ();
  private JButton jBBack          = new JButton ("Zurück");
  private JButton jBEnd           = new JButton ("Ende");
  private Container content       = null;
  private static CHelp help       = null;

  private CHelp (){
    super ();
    this.setTitle ("JTrain- Modellbahnsteuerung: Hilfe");
    this.setSize (600, 400);
    this.setIconImage (CControlCenter.getIcon ());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    content = this.getContentPane ();
    editorPane.setEditable (false);
    editorPane.addHyperlinkListener (this);
    content.add (new JScrollPane (editorPane), "Center" );
    jBBack.addActionListener (this);
    jPan.add (jBBack);
    jBEnd.addActionListener (this);
    jPan.add (jBEnd);
    content.add (jPan, "South");
    start ();
  }

  public static CHelp getHelp (){
    if (help == null) help = new CHelp ();
    return help;
  }


  private final void start (){
    try {
      urlStack.push("file:"+ Config.HTMLDIR + File.separator +"helpstart.htm");
      editorPane.setPage("file:"+ Config.HTMLDIR + File.separator +"helpstart.htm");
    }
      catch (IOException e){
        editorPane.setText (e.toString());
      }
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jBBack){
      if (urlStack.size () <= 1) return;
      try {
        urlStack.pop ();
        String urlString = (String) urlStack.peek ();
        editorPane.setPage (urlString);
      }
      catch (IOException ex){
        editorPane.setText ("Fehler: " + e.toString());
      }
    }
    if (o == jBEnd){
      setVisible (false);
      dispose ();
    }
  }

  public void hyperlinkUpdate (HyperlinkEvent ev){
    if (ev.getEventType () == HyperlinkEvent.EventType.ACTIVATED){
      try {
        urlStack.push (ev.getURL ().toString ());
        editorPane.setPage (ev.getURL ());
      }
      catch (IOException e){
        editorPane.setText (e.toString());
      }
    }
  }

}

