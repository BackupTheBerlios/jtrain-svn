/*
the editor for automatic train rides
last modified: 2002 05 05
author: Werner Kunkel
*/

package de.jtrain.chain;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;

import de.jtrain.block.CBlock;
import de.jtrain.com.CControlCenter;
import de.jtrain.config.CLang;
import de.jtrain.config.Config;
import de.jtrain.control.CLoco;
import de.jtrain.control.CLocoCommandDialog;
import de.jtrain.gb.CStep;
import de.jtrain.help.CHelp;
import de.jtrain.main.JTrain;
import de.jtrain.turnout.CTurnoutCommandDialog;

public class CChainEditor extends JFrame implements ActionListener {

  private JMenu menu                = null;
  private JMenuItem open            = null;
  private JMenuItem save            = null;
  private JMenuItem quit            = null;
  private JMenuItem locochange      = null;
  private JMenuItem rename          = null;
  private JMenuItem copy            = null;
  private JMenuItem output          = null;
  private JMenuItem MIdelete          = null;
  private JMenuItem help            = null;
  private JPanel jPNorth            = new JPanel ();
  private JLabel jLNorth            = new JLabel ();
  private JFrame jChain             = new JFrame ();
  private JPanel jPEast             = new JPanel ();
  private JPanel jPSouth            = new JPanel ();
  private JPanel jPWest             = new JPanel ();
  private StepEditorPane jPCenter   = null;
  private JButton jBNewStep         = new JButton (CLang.s7);
  private JButton jBAbort           = new JButton (CLang.s8);
  private JButton jBSave            = new JButton (CLang.s9);
  private JButton jBExit            = new JButton (CLang.s10);
  private JScrollPane scrollPane    = null;
  private Container c               = null;
  private CStep tStep               = null;
  private CChain chain              = new CChain ();
  private int iStepCounter          = 1;
  private int tmpX, tmpY;
  private BorderLayout bl           = new BorderLayout (20,20);
  private boolean bLinux            = false;
  private boolean bIgnore           = false;
  private String sName              = null;
  private MyMouseAdapter mma        = new MyMouseAdapter ();
  private MyMouseMotionAdapter mmma = new MyMouseMotionAdapter ();

  CChainEditor (){
    super ();
    start (true);
  }

  CChainEditor (CChain chain){
    super ();
    this.chain = chain;
    this.sName = chain.getName ();
    setStepCounter ();
    start (false);
  }

  CChainEditor (CLoco loco){
    super ();
    this.chain.setLoco (loco);
    start (true);
  }

  public final void start (boolean bNew){
    this.setSize (380, 450);
    this.setResizable (false);
    this.setLocation (350, 100);
    this.setIconImage (CControlCenter.getIcon ());
    JMenuBar menubar = new JMenuBar ();
    menubar.add (createFileMenu1 ());
    menubar.add (createFileMenu2 ());
    menubar.add (createFileMenu3 ());
    menubar.add (createFileMenu4 ());
    this.setJMenuBar (menubar);
    c = this.getContentPane ();
    jLNorth.setHorizontalAlignment (SwingConstants.CENTER);
    jPNorth.add (jLNorth);
    c.setLayout (bl);
    c.add (jPNorth, BorderLayout.NORTH);
    c.add (jPEast, BorderLayout.EAST);
    chain.setEdit (true);
    if (bNew){
      chain.setFirstStep (new CStep ());
      chain.getFirstStep().setNumber (1);
      chain.add (chain.getFirstStep ());
      chain.putStep (chain.getFirstStep ());
    }
    jPCenter = new StepEditorPane (chain.getFirstStep (), this);
    bIgnore = true;
    changeStep (chain.getFirstStep ());
    bIgnore = false;
    chain.getFirstStep().setLocation (130, 10);
    c.add (jPCenter, BorderLayout.CENTER);
    c.add (jPWest, BorderLayout.WEST);
    jBNewStep.addActionListener (this);
    jBNewStep.setMnemonic ('n');
    jPSouth.add (jBNewStep);
    jBAbort.addActionListener (this);
    jBAbort.setMnemonic ('a');
    jPSouth.add (jBAbort);
    jBSave.addActionListener (this);
    jBSave.setMnemonic ('s');
    jPSouth.add (jBSave);
    jBExit.addActionListener (this);
    jBExit.setMnemonic ('e');
    jPSouth.add (jBExit);
    c.add (jPSouth, BorderLayout.SOUTH);

    //the graphic step-window
    scrollPane = new JScrollPane ();
    scrollPane.setViewportView (chain);
    scrollPane.getVerticalScrollBar ().setUnitIncrement (10);
    jChain.getContentPane ().add (scrollPane, "Center");
    jChain.setSize (330, 450);
    jChain.setLocation (20, 100);
    jChain.setResizable (false);
    jChain.setIconImage (CControlCenter.getIcon ());
    jChain.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
    jChain.show();

    chain.getFirstStep().repaint();
    for (Enumeration en = chain.getSteps(); en.hasMoreElements ();){
      tStep = (CStep)en.nextElement ();
      if (tStep != null){
        tStep.removeMouseListener (mma);
        tStep.removeMouseMotionListener (mmma);
        tStep.addMouseListener (mma);
        tStep.addMouseMotionListener (mmma);
      }
    }
    if (System.getProperty ("os.name").equalsIgnoreCase ("linux"))bLinux = true;
    this.addWindowListener (new MyWindowAdapter ());
    if (sName == null)
      sName = JOptionPane.showInputDialog(
      this,
      CLang.s11,
      CLang.s12,
      JOptionPane.OK_OPTION );
    jChain.setTitle (sName + CLang.s13);
    this.setTitle (sName + CLang.s14);
    chain.setName (sName);
    setHeader ();
  }

  public void setHeader () {
    if (chain.getLoco() != null)
      jLNorth.setText(CLang.s15 + sName + CLang.s16 +
        chain.getLoco().getName ());
    else jLNorth.setText (CLang.s15 + sName );
  }

  private JMenu createFileMenu1(){
    menu = new JMenu ("Datei");
    menu.setMnemonic ('D');
    //open
    open = new JMenuItem ("Öffnen", 'n');
    setCtrlAccelerator (open, 'O');
    open.addActionListener (this);
    menu.add (open);
    //save
    save = new JMenuItem ("Speichern", 's');
    setCtrlAccelerator (save, 'S');
    save.addActionListener (this);
    menu.add (save);
    //separator
    menu.addSeparator ();
    //quit
    quit = new JMenuItem ("Beenden", 'e');
    setCtrlAccelerator (quit, 'Q');
    quit.addActionListener (this);
    menu.add (quit);
    return menu;
  }

  private JMenu createFileMenu2 (){
    menu = new JMenu ("Ketten");
    menu.setMnemonic ('K');
    //rename
    rename = new JMenuItem ("umbenennen", 'u');
    menu.add (rename);
    rename.addActionListener (this);
    //copy
    copy = new JMenuItem ("kopieren", 'p');
    menu.add (copy);
    copy.addActionListener (this);
    //output
    output = new JMenuItem ("ausgeben", 'a');
    menu.add (output);
    output.addActionListener (this);
    //delete
    MIdelete = new JMenuItem ("löschen", 'l');
    menu.add (MIdelete);
    MIdelete.addActionListener (this);
    return menu;
  }

  private JMenu createFileMenu3 (){
    menu = new JMenu ("Lok ");
    menu.setMnemonic ('l');
    //change loco
    locochange = new JMenuItem ("Lok wechseln", 'o');
    menu.add (locochange);
    locochange.addActionListener (this);
    return menu;
  }

   private JMenu createFileMenu4 (){
    menu = new JMenu ("Hilfe ");
    menu.setMnemonic ('h');
    //change loco
    help = new JMenuItem ("Hilfe", 'h');
    setCtrlAccelerator (help, 'H');
    menu.add (help);
    help.addActionListener (this);
    return menu;
  }


  private void setCtrlAccelerator (JMenuItem mi, char acc) {
    KeyStroke ks = KeyStroke.getKeyStroke (acc, Event.CTRL_MASK );
    mi.setAccelerator (ks);
  }

  public void setStepCounter (){
    if (chain != null && chain.getVSteps() != null){
      for (Enumeration en = chain.getSteps (); en.hasMoreElements ();){
        CStep tStep = (CStep ) en.nextElement ();
        if (tStep.getNumber () > iStepCounter)
          iStepCounter = tStep.getNumber ();
      }
    }
  }

  public final StepEditorPane getPane (){return this.jPCenter;}
  public final JFrame getStepFrame () {return jChain;}
  public final CChain getChain (){return this.chain;}
  public void setChain (CChain chain){this.chain = chain;}

  public final void readStep (){
    jPCenter.read (jPCenter.jTfCond);
    jPCenter.read (jPCenter.jTfFalse);
    jPCenter.read (jPCenter.jTfTrue);
  }

  private final void changeStep (CStep step){
    jPCenter.step.setBackground (Color.white);
    readStep ();
    //first we reset everything
    jPCenter.step = step;
    jPCenter.step.setBackground (Color.yellow);
    jPCenter.jLHead.setText (CLang.s17 + jPCenter.step.getNumber ());
    jPCenter.jTfTrue.setText("");
    jPCenter.jTfFalse.setText("");
    jPCenter.jTfCond.setText("");
    jPCenter.cbBlRel.setState (false);
    jPCenter.cbBlReq.setState (false);
    jPCenter.cbLoco.setState (false);
    jPCenter.cbMa.setState (false);
    jPCenter.cbTimer.setState (false);
    //now we show the actual settings
    if (step.getStepTrue () != null)
      jPCenter.jTfTrue.setText ("" + step.getStepTrue ().getNumber ());
    if (step.getNextStepCondition () != null){
      StringTokenizer stok = new StringTokenizer (
        step.getNextStepCondition()," ");
      String s = stok.nextToken ();
      if (s.startsWith ("w")){
        jPCenter.ch.select (CLang.s18);
        jPCenter.jLCond3.setText ("");
      }
      else if (s.startsWith ("c")){
        jPCenter.ch.select (CLang.s19);
        jPCenter.jLCond3.setText (CLang.s20);
      }
      else if (s.startsWith ("t")){
        jPCenter.ch.select (CLang.s21);
        jPCenter.jLCond3.setText ("");
      }
      else if (s.startsWith ("b")){
        jPCenter.ch.select (CLang.s22);
        jPCenter.jLCond3.setText (CLang.s23);
      }
      if (stok.hasMoreTokens ()){
        s = stok.nextToken ();
        jPCenter.jTfCond.setText (s);
      }
    }
    if (step.getStepFalse () != null)
      jPCenter.jTfFalse.setText ("" + step.getStepFalse ().getNumber ());
    jPCenter.list.setListData (jPCenter.step.getJobs ());
    String s;
    for (Enumeration en = jPCenter.step.getJobs ().elements ();
    en.hasMoreElements ();){
      s = (String) en.nextElement ();
      if (s.startsWith (CLang.s24))
        jPCenter.cbBlReq.setState (true);
      else if (s.startsWith (CLang.s25))
        jPCenter.cbBlRel.setState (true);
      else if (s.startsWith (CLang.s26))
        jPCenter.cbTimer.setState (true);
      else if (s.startsWith (CLang.s27))
        jPCenter.cbMa.setState (true);
      else if (s.startsWith (CLang.s28))
        jPCenter.cbLoco.setState (true);
    }
  }

   private final void showHelp ()throws MalformedURLException {
    HyperlinkEvent he = null;
    CHelp hlp = CHelp.getHelp ();
    hlp.show ();
    he = new HyperlinkEvent (this, HyperlinkEvent.EventType.ACTIVATED,
    new URL ("file:"+ Config.HTMLDIR + File.separator +"jt_36.htm#autouse"));
    hlp.hyperlinkUpdate (he);
  }

  public final void cleanUp (){
    for (Enumeration en = chain.getSteps(); en.hasMoreElements ();){
      tStep = (CStep)en.nextElement ();
      if (tStep != null){
        tStep.removeMouseListener (mma);
        tStep.removeMouseMotionListener (mmma);
      }
    }
    JTrain.mf.setEditmode (false);
    jChain.setVisible (false);
    jChain.dispose ();
    setVisible (false);
    dispose ();
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == jBNewStep){
      tStep = new CStep ();
      iStepCounter ++;
      tStep.setNumber (iStepCounter);
      chain.add (tStep);
      chain.putStep (tStep);
      tStep.setLocation (20, (int)(scrollPane.getViewport ().
      getViewPosition().getY() + 100));
      tStep.addMouseListener (mma);
      tStep.addMouseMotionListener (mmma);
    }
    else if (o == jBAbort){
      JTrain.mf.setEditmode (false);
      jChain.setVisible (false);
      jChain.dispose ();
      setVisible (false);
      dispose ();
    }
    else if (o == jBSave || o == save){
      readStep ();
      if (!CControlCenter.getChainList ().contains (chain))
        CControlCenter.getChainList ().add (chain);
      CControlCenter.saveChainList ();
    }
    else if (o == jBExit || o == quit){
      readStep ();
      if (!CControlCenter.getChainList ().contains (chain))
        CControlCenter.getChainList ().add (chain);
      CControlCenter.saveChainList ();
      JTrain.mf.setEditmode (false);
      jChain.setVisible (false);
      jChain.dispose ();
      setVisible (false);
      dispose ();
    }
    else if (o == open){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.CHANGE);
    }
    else if (o == locochange){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.LOCOCHG);
    }
    else if (o == rename){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.RENAME);
    }
    else if (o == output){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.OUTPUT);
    }
    else if (o == copy){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.COPY);
    }
    else if (o == MIdelete){
      CChainManipulator cm = new CChainManipulator (this, "", true,
        CChain.DELETE);
    }
    else if (o == help){
      try {showHelp ();}
      catch (MalformedURLException ex) {}
    }
  }

  class MyMouseAdapter extends MouseAdapter {
    public void mousePressed (MouseEvent e){
      if (e.getComponent() instanceof CStep){
        //on right-click
        if (e.getModifiers() == MouseEvent.BUTTON3_MASK){
          changeStep ((CStep) e.getComponent());
        }
        tmpX = (int) e.getComponent ().getLocation ().getX ();
        tmpY = (int) e.getComponent ().getLocation ().getY ();
      }
    }

    public void mouseReleased (MouseEvent e){
      Component mouseChoice = e.getComponent ();
      if (mouseChoice instanceof CStep){
        //move to raster
        Point p = mouseChoice.getLocation();
        int iX = (int) p.getX();
        if (iX > 250) iX = 250;
        else if (iX < 1) iX = 1;
        int iY = (int) p.getY();
        if (iY < 1) iY = 1;
        if (iY > 1970) iY = 1970;
        if (iX % 10 > 4)
          iX = (iX / 10 + 1) * 10;
        else iX = (iX / 10) * 10;
        if (iY % 10 > 4)
          iY = (iY / 10 + 1) * 10;
        else iY = (iY / 10) * 10;
        mouseChoice.setLocation (iX, iY);
        repaint ();
      }
    }
  }

  class MyMouseMotionAdapter extends MouseMotionAdapter {
    public void mouseDragged (MouseEvent e){
      Component mouseChoice = e.getComponent ();
      if (mouseChoice instanceof CStep){
        /*if (!bLinux){*/
            tmpX = (int) e.getComponent ().getLocation ().getX ();
            tmpY = (int) e.getComponent ().getLocation ().getY ();
        /*  }*/
        mouseChoice.setLocation ( tmpX + e.getX () - 15, tmpY + e.getY () - 15);
      }
    }
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      if (e.getID() == WindowEvent.WINDOW_CLOSING){
        cleanUp ();
      }
    }
  }


//I think it`s okay to put the editorPane in an extra class
//to make things more clear
public class StepEditorPane extends JPanel implements ItemListener {
    private CStep step          = null;
    private JFrame owner        = null;
    private GridLayout gl       = new GridLayout (4, 2, 20, 5);
    private GridLayout gl1      = new GridLayout (1, 2, 10, 10);
    private BorderLayout blp    = new BorderLayout (10, 5);
    private JLabel jLHead       = new JLabel ();
    private JPanel jPSouth      = new JPanel ();
    private JLabel jLTrue;
    private JTextField jTfTrue;
    private JLabel jLCond       = new JLabel (CLang.s30);
    private Choice ch           = new Choice ();
    private JLabel jLCond2      = new JLabel (CLang.s31);
    private JPanel jPCond       = new JPanel ();
    private JLabel jLCond3      = new JLabel ();
    private JTextField jTfCond  = new JTextField ();
    private JLabel jLFalse      = new JLabel (CLang.s4);
    private JTextField jTfFalse = new JTextField ();
    private JPanel jPCen        = new JPanel ();
    private JLabel jLJobs       = new JLabel (CLang.s5, SwingConstants.CENTER);
    private JPanel jPJobs       = new JPanel ();
    private JPanel jPS          = new JPanel ();
    private GridLayout glj      = new GridLayout (3, 2, 5, 5);
    private BorderLayout blj    = new BorderLayout (10, 5);
    private Checkbox cbBlReq    = new Checkbox (CLang.s32, false);
    private Checkbox cbBlRel    = new Checkbox (CLang.s33, false);
    private Checkbox cbLoco     = new Checkbox (CLang.s34, false);
    private Checkbox cbTimer    = new Checkbox (CLang.s35, false);
    private Checkbox cbMa       = new Checkbox (CLang.s36, false);
    private JList list          = new JList ();
    private MyKeyAdapter mka    = new MyKeyAdapter ();

    StepEditorPane (CStep step, JFrame owner){
      super ();
      this.step = step;
      this.setLayout (blp);
      this.owner = owner;
      jLHead.setText(CLang.s17 + step.getNumber ());
      jLHead.setHorizontalAlignment (SwingConstants.CENTER);
      this.add (jLHead, BorderLayout.NORTH);
      jPSouth.setLayout (gl);
      
      jLTrue = new JLabel(CLang.s29); //"Nächster Step:"
      jPSouth.add(jLTrue);
      jTfTrue = new JTextField();
      jTfTrue.setHorizontalAlignment (SwingConstants.CENTER);
      jTfTrue.addKeyListener (mka);
      jPSouth.add (jTfTrue);
      /* deprecated: */
      /*jTfTrue.requestDefaultFocus ();*/

      jPSouth.add (jLCond);
      ch.add (CLang.s18);
      ch.add (CLang.s19);
      ch.add (CLang.s21);
      ch.add (CLang.s22);
      ch.addItemListener (this);
      jPSouth.add (ch);
      jPSouth.add (jLCond2);
      jPCond.add (jLCond3);
      jTfCond.setPreferredSize (new Dimension (30, 20));
      jTfCond.addKeyListener (mka);
      jPCond.add (jTfCond);
      jPSouth.add (jPCond);
      jPSouth.add (jLFalse);
      jTfFalse.setHorizontalAlignment (SwingConstants.CENTER);
      jTfFalse.addKeyListener (mka);
      jPSouth.add (jTfFalse);
      this.add (jPSouth, BorderLayout.SOUTH);
      jPCen.setLayout (blj);
      jPCen.add (jLJobs, BorderLayout.NORTH);
      jPJobs.setLayout (gl);
      cbBlReq.addItemListener (this);
      jPJobs.add (cbBlReq);
      cbBlRel.addItemListener (this);
      jPJobs.add (cbBlRel);
      cbLoco.addItemListener (this);
      jPJobs.add (cbLoco);
      cbTimer.addItemListener (this);
      jPJobs.add (cbTimer);
      cbMa.addItemListener (this);
      jPJobs.add (cbMa);
      jPJobs.setPreferredSize (new Dimension (250, 120));
      jPCen.add (jPJobs, BorderLayout.CENTER);
      jPS.setLayout (gl1);
      jPS.setPreferredSize(new Dimension(250,50));
      list.setListData (step.getJobs ());
      jPS.add (new JScrollPane (list,
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
      jPCen.add (jPS, BorderLayout.SOUTH);
      this.add (jPCen, BorderLayout.CENTER);
    }

    public Checkbox getCbLoco (){return cbLoco;}
    public Checkbox getCbMa (){return cbMa;}
    public CStep getStep (){return step;}
    public JList getList (){return list;}

    public final void read (JTextField jtf){
      int iInp = 0;
      try{
        iInp = Integer.parseInt (jtf.getText ());
      }
      catch (Exception e){
      }
      boolean bFound = false;
      if (jtf.equals (jTfCond)){
        if (ch.getSelectedIndex () == 0){
          bFound = true;
          step.setNextStepCondition ("without");
        }
        if (ch.getSelectedIndex () == 1){
          bFound = true;
          step.setNextStepCondition ("contakt " + iInp);
        }
        if (ch.getSelectedIndex () == 2){
          bFound = true;
          step.setNextStepCondition ("timer");
        }
        if (ch.getSelectedIndex () == 3){
          Iterator iter = CControlCenter.getBlockList().iterator();
          while(iter.hasNext())
          {
              CBlock tblock = (CBlock) iter.next();
              if (tblock.getBlNumber () == iInp){
                bFound = true;
                step.setNextStepCondition ("block " + iInp);
                break;
              }
          }
        }
        jTfFalse.requestFocus();
      }
      if (jtf.equals (jTfTrue)){
        for (Enumeration en = chain.getSteps (); en.hasMoreElements ();){
          CStep tstep = (CStep) en.nextElement();
          if (tstep != null && tstep.getNumber () == iInp){
            step.setStepTrue (tstep);
            bFound = true;
            chain.repaint ();
            step.repaint ();
            tstep.repaint ();
            repaint ();
            break;
          }
        }
        ch.requestFocus();
      }
      if (jtf.equals (jTfFalse)){
        if (iInp == 0){
          step.setStepFalse (null);
          step.setStepFalseExists (false);
          step.repaint ();
          chain.repaint ();
          jTfFalse.setText("");
          return;
        }
        for (Enumeration en = chain.getSteps (); en.hasMoreElements ();){
          CStep tstep = (CStep) en.nextElement();
          if (tstep != null && tstep.getNumber () == iInp){
            step.setStepFalse (tstep);
            step.setStepFalseExists (true);
            tstep.repaint ();
            chain.repaint ();
            step.repaint ();
            repaint ();
            bFound = true;
            break;
          }
        }
      }
      if (!bFound && !bIgnore){
        JOptionPane.showMessageDialog(
          this,
          CLang.s37,
          CLang.s38,
          JOptionPane.OK_OPTION );
      }
    }

    class MyKeyAdapter extends KeyAdapter {

      public void keyPressed (KeyEvent e){
        if (e.getKeyChar () == KeyEvent.VK_ENTER){
          read ((JTextField)e.getSource ());
        }
      }
    }

    public void itemStateChanged (ItemEvent e){
      String s = "";
      if (e.getItem ().equals (CLang.s18)){
        jLCond3.setText ("");
        jBNewStep.requestFocus ();
        step.setNextStepCondition ("without");
        return;
      }
      else if (e.getItem ().equals (CLang.s19)) {
        s = CLang.s39;
        jLCond3.setText (s);
        jTfCond.requestFocus ();
        return;
      }
      else if (e.getItem ().equals (CLang.s21)) {
        s = CLang.s40;
        step.setNextStepCondition ("timer");
        jLCond3.setText (s);
        jTfFalse.requestFocus ();
        return;
      }
      else if (e.getItem ().equals (CLang.s22)) {
        s = CLang.s41;
        jLCond3.setText (s);
        jTfCond.requestFocus ();
        return;
      }
      else if (e.getItem ().equals (CLang.s32)
      && e.getStateChange () == ItemEvent.SELECTED){
        int i = 0;
        boolean bFound = false;
        String str = JOptionPane.showInputDialog(
          null,
          CLang.s42,
          CLang.s43,
          JOptionPane.OK_OPTION);
        if (str == null) {
          cbBlReq.setState (false);
          return;
        }
        StringTokenizer st = new StringTokenizer (str, ",.");
        while (st.hasMoreTokens ()){
          bFound = false;
          try{
            i = Integer.parseInt (st.nextToken ());
          }
          catch (Exception ex){
            //This Block should never be found!
            i = -1;
          }
          Iterator iter = CControlCenter.getBlockList().iterator();
          {
            CBlock block = (CBlock) iter.next();
            if (block.getBlNumber () == i){
              bFound = true;
              break;
            }
          }
          if (!bFound){
            JOptionPane.showMessageDialog(
              null,
              CLang.s44,
              CLang.s45,
              JOptionPane.OK_OPTION);
            cbBlReq.setState (false);
            break;
          }
        }
        if (bFound ){
          step.getJobs ().add (CLang.s46 + str);
          list.setListData (step.getJobs ());
        }
      }
      else if (e.getItem ().equals (CLang.s32)
      && e.getStateChange () == ItemEvent.DESELECTED){
        for (Enumeration en = step.getJobs().elements ();
	  en.hasMoreElements ();){
          String strg = (String) en.nextElement ();
          if (strg.startsWith (CLang.s24)){
            step.getJobs().remove (strg);
            list.setListData (step.getJobs ());
            return;
          }
        }
      }
      else if (e.getItem().equals (CLang.s33)
      && e.getStateChange () == ItemEvent.SELECTED){
        int i = 0;
        boolean bFound = false;
        String st = JOptionPane.showInputDialog(
          null,
          CLang.s47,
          CLang.s43,
          JOptionPane.OK_OPTION);
        try{
          i = Integer.parseInt (st);
        }
        catch (Exception ex){}
        Iterator iter = CControlCenter.getBlockList ().iterator();
        while(iter.hasNext())
        {
          CBlock block = (CBlock) iter.next();
          if (block.getBlNumber () == i){
            bFound = true;
            step.getJobs ().add (CLang.s48 + i);
            list.setListData (step.getJobs ());
            break;
          }
        }
        if (!bFound){
          JOptionPane.showMessageDialog(
            null,
            CLang.s49,
            CLang.s45,
            JOptionPane.OK_OPTION);
          cbBlRel.setState (false);
        }
      }
      else if (e.getItem ().equals (CLang.s33)
      && e.getStateChange () == ItemEvent.DESELECTED){
        for (Enumeration en = step.getJobs().elements ();
	     en.hasMoreElements ();){
          String strg = (String) en.nextElement ();
          if (strg.startsWith (CLang.s25)){
            step.getJobs().remove (strg);
            list.setListData (step.getJobs ());
            return;
          }
        }
      }
      else if (e.getItem ().equals (CLang.s35)
      && e.getStateChange () == ItemEvent.SELECTED){
        int i = 0;
        String st = JOptionPane.showInputDialog(
          null,
          CLang.s50,
          CLang.s43,
          JOptionPane.OK_OPTION);
        try{
          i = Integer.parseInt (st);
        }
        catch (Exception ex){}
        if (i == 0){
          JOptionPane.showMessageDialog(
            null,
            CLang.s51,
            CLang.s45,
            JOptionPane.OK_OPTION);
          cbTimer.setState (false);
        }
        else{
          step.getJobs ().add (CLang.s26 + i + " ms");
          list.setListData (step.getJobs ());
        }
      }
      else if (e.getItem ().equals (CLang.s35)
      && e.getStateChange () == ItemEvent.DESELECTED){
        for (Enumeration en = step.getJobs().elements ();
	     en.hasMoreElements ();){
          String strg = (String) en.nextElement ();
          if (strg.startsWith (CLang.s26)){
            step.getJobs().remove (strg);
            list.setListData (step.getJobs ());
            return;
          }
        }
      }
      else if (e.getItem ().equals ("Lokbefehl")
      && e.getStateChange () == ItemEvent.SELECTED){
        if (chain.getLoco () == null){
          JOptionPane.showMessageDialog(
            this,
            CLang.s52,
            CLang.s45,
            JOptionPane.OK_OPTION);
          cbLoco.setState (false);
        }
        else{
          String sName = chain.getLoco ().getName ();
          JDialog dlg = new CLocoCommandDialog (owner,
          CLang.s53, true, sName );
          dlg.show ();
        }
      }
      else if (e.getItem ().equals (CLang.s34)
      && e.getStateChange () == ItemEvent.DESELECTED){
        for (Enumeration en = step.getJobs().elements ();
	     en.hasMoreElements ();){
          String strg = (String) en.nextElement ();
          if (strg.startsWith (CLang.s28)){
            step.getJobs().remove (strg);
            list.setListData (step.getJobs ());
            return;
          }
        }
      }
      else if (e.getItem().equals(CLang.s36)
      && e.getStateChange () == ItemEvent.SELECTED){
        JDialog dlg = new CTurnoutCommandDialog (owner,
        CLang.s54, true);
        dlg.show ();
      }
      else if (e.getItem ().equals (CLang.s36)
      && e.getStateChange () == ItemEvent.DESELECTED){
        for (Enumeration en = step.getJobs().elements ();
	     en.hasMoreElements ();){
          String strg = (String) en.nextElement ();
          if (strg.startsWith (CLang.s27)){
            step.getJobs().remove (strg);
            list.setListData (step.getJobs ());
            return;
          }
        }
      }
    }
  }

}

