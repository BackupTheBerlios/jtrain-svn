/*
the class to control the tracklayout

last modified : 2002 03 30
author : Werner Kunkel
*/

package de.jtrain.gb;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;

import de.jtrain.block.CBlContainer;
import de.jtrain.block.CBlock;
import de.jtrain.block.CBlockEventHandler;
import de.jtrain.chain.CChainFrameDialog;
import de.jtrain.com.CControlCenter;
import de.jtrain.com.CFbElement;
import de.jtrain.com.CFeedbackportHandler;
import de.jtrain.event.CBlockEvent;
import de.jtrain.event.CBlockListener;
import de.jtrain.event.CFeedbackportEvent;
import de.jtrain.event.CFeedbackportListener;
import de.jtrain.event.CMyKeyAdapter;
import de.jtrain.event.CTurnoutEvent;
import de.jtrain.event.CTurnoutListener;
import de.jtrain.main.JTrain;
import de.jtrain.street.CStreet;
import de.jtrain.street.CStreetDlg;
import de.jtrain.turnout.CTurnout;
import de.jtrain.turnout.CTurnoutHandler;
import de.jtrain.turnout.CTurnoutJob;

public class CGbExec extends CGb implements CTurnoutListener, CBlockListener,
  CFeedbackportListener, ActionListener {

  private MenuBar mbar              = new MenuBar ();
  private Menu fs                   = new Menu ("Fahrstraßen");
  private MenuItem fsset            = new MenuItem ("Fahrstraße anfordern");
  private MenuItem fsrel            = new MenuItem ("Fahrstraße freigeben");
  private Menu auto                 = new Menu ("Automatik");
  private MenuItem autostart        = new MenuItem ("Automatik starten");
  private boolean mode;
  private Panel pan                 = new Panel ();
  private MyMouseAdapter mma        = new MyMouseAdapter ();

  public CGbExec (String name){
    super (name);
    this.getContentPane ().setLayout (null);
    this.setResizable (true);
    this.setLocation (1, 60);
    this.setIconImage (CControlCenter.getIcon ());
    if (load () == false){
      JTrain.mf.setControlmode (false);
      this.setVisible (false);
      dispose ();
    }
    else{
      pan.setSize ((int) getGbSize ().getWidth(),
      (int) getGbSize ().getHeight ()  - 120);
      pan.setBackground (new Color (254, 254, 152));
      pan.setLayout (null);
      this.setSize ((int) getGbSize ().getWidth (),
      (int) getGbSize ().getHeight () - 100);
      this.setTitle (sTitle);
      this.setBackground (new Color (254, 254, 152));
      fsset.addActionListener (this);
      fs.add (fsset);
      fsrel.addActionListener (this);
      fs.add (fsrel);
      mbar.add (fs);
      autostart.addActionListener (this);
      auto.add (autostart);
      mbar.add (auto);
      this.setMenuBar (mbar);
      for (Enumeration en = elList.elements (); en.hasMoreElements ();){
        CGbElement tmp = (CGbElement) en.nextElement ();
        tmp.setLocation (tmp.getX () , tmp.getY ());
        tmp.setVisible (true);
        if (tmp instanceof CGbSwitchEl || tmp instanceof CGbSignal){
          tmp.addMouseListener(mma);
        }
        pan.add (tmp);
      }
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        fbtmp.setLocation (fbtmp.getX (), fbtmp.getY ());
        fbtmp.setVisible (true);
        pan.add (fbtmp,0);
      }
      for (Enumeration en = blList.elements (); en.hasMoreElements ();){
        CBlContainer blc = (CBlContainer) en.nextElement ();
        JButton bltmp = blc.getBlBox ();
        CBlock bl = getBlock (blc.getBlNumber ());
        bltmp.setText (blc.getDir () + bl.getDescription () + bl.getOwner ());
        bltmp.removeMouseListener(mma);
        bltmp.setLocation (bltmp.getX (), bltmp.getY ());
        bltmp.setVisible (true);
        bltmp.addMouseListener (mma);
        bltmp.setMargin (new Insets (0,0,0,0));
        bltmp.setFocusPainted (false);
        pan.add (bltmp);
      }
      if (elementSize.equals(smSize)){
        elementSize = medSize;
        elementSize = smSize;
      }
      else if (elementSize.equals(medSize)){
        elementSize = smSize;
        elementSize = medSize;
      }
      this.getContentPane ().add (pan);
      this.setVisible (true);
      pan.repaint ();
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.addWindowListener (new MyWindowAdapter ());
      this.addKeyListener (new CMyKeyAdapter ());
      CTurnoutHandler.addTurnoutListener (this);
      CBlockEventHandler.addBlockListener (this);
      CFeedbackportHandler.getFeedbackportHandler().addFeedbackportListener (this);
      refresh ();
      init ();
    }
  }

  private final void init (){
    CGbElement gbtmp = null;
    boolean bGreen = false;
    boolean bHor = false;
    for (Enumeration en = elList.elements (); en.hasMoreElements ();){
      gbtmp = (CGbElement) en.nextElement ();
      if (gbtmp instanceof CGbSwitchEl){
        bGreen = CControlCenter.getTurnoutList ()
	  [((CGbSwitchEl) gbtmp).getId()].getIsGreen();
        bHor = CControlCenter.getTurnoutList ()
	  [((CGbSwitchEl) gbtmp).getId()].getHorizontal();
        ((CGbSwitchEl) gbtmp).setIsGreen (bGreen);
        ((CGbSwitchEl) gbtmp).setHorizontal (bHor);
      }
      else if (gbtmp instanceof CGbSignal){
        bGreen = CControlCenter.getTurnoutList ()
	  [((CGbSignal) gbtmp).getId()].getIsGreen();
        ((CGbSignal) gbtmp).setIsGreen (bGreen);
      }
      gbtmp.repaint ();
    }
    CBlock bl = null;
    Iterator iter = CControlCenter.getBlockList ().iterator();
    while(iter.hasNext())
    {
      bl = (CBlock) iter.next();
      if (bl.getBlockInUseOnStart ())
        doBlJobs (bl.getBlNumber ());
    }
  }

  public void handleEvent (CBlockEvent e){
    CBlock bl = getBlock (e.getBlNumber());
    CBlContainer blc = getBlContainer (e.getBlNumber ());
    JButton blBox = blc.getBlBox ();
    boolean bMore = false;
    String sMore = "++";
    if (e.getBlState () == CBlock.BLOCK_FREE){
      setFree (e.getBlNumber(), true);
      doUnsetBlJobs (e.getBlNumber ());
      blBox.setText (blc.getDir () + bl.getDescription ());
      blBox.setBackground (bl.getColor ());
      refresh (e.getBlNumber());
    }
    else if (e.getBlState () == CBlock.BLOCK_EXECUTED){
      if (bl.getRequestors ().size () > 0) bMore = true;
      setFree (e.getBlNumber(), false);
      doBlJobs (e.getBlNumber ());
      if (bMore)
        blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner () + sMore);
      else
        blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner ());
      blBox.setBackground (Color.red);
      refresh (e.getBlNumber ());
    }
    else if (e.getBlState () == CBlock.BLOCK_REQUESTED){
      if (bl.getRequestors ().size () > 1) bMore = true;
      if (bMore)
        blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner () + sMore);
      else
        blBox.setText (blc.getDir () + bl.getDescription () + bl.getOwner ());
      blBox.setBackground (bl.getColor ());
    }
  }

  public void handleEvent (CTurnoutEvent e){
    for (Enumeration en = elList.elements (); en.hasMoreElements ();){
      CGbElement tmp = (CGbElement)en.nextElement ();
      if (tmp instanceof CGbSwitchEl ){
        if (((CGbSwitchEl)tmp).getId() == e.getTurnoutId()){
          ((CGbSwitchEl)tmp).setHorizontal (e.getHorizontal());
          ((CGbSwitchEl)tmp).setIsGreen (e.getIsGreen());
          tmp.repaint ();
          break;
        }
      }
      else if (tmp instanceof CGbSignal ){
        if (((CGbSignal)tmp).getId() == e.getTurnoutId()){
          ((CGbSignal)tmp).setIsGreen (e.getIsGreen());
          tmp.repaint ();
          break;
        }
      }
    }
  }

  public void handleEvent (CFeedbackportEvent e){
    int port;
    int state;
    port = e.getPort();
    state = e.getState();
    if (state == 1){
      for (Enumeration en = fbList.elements (); en.hasMoreElements ();){
        CFbElement fbtmp = (CFbElement) en.nextElement ();
        if (fbtmp.getId () == port && fbtmp.getActive() == false){
          CWaiter w = new CWaiter (5000, fbtmp);
          w.start ();
        }
      }
    }
  }

  private final void doBlJobs (int iBlNr){
    CTurnoutJob tj = null;
    CBlContainer blc = getBlContainer (iBlNr);
    if (blc != null && !blc.blJobsIsEmpty ()){
      for (Enumeration en = blc.getEnBlJobs (); en.hasMoreElements ();){
        tj = (CTurnoutJob) en.nextElement ();
        sendCommand (tj.getTurnout().getId(), tj.getToGreen(), tj.getHor ());
      }
    }
  }

  private final void doUnsetBlJobs (int iBlNr){
    CGbElement el = null;
    CBlContainer blc = getBlContainer (iBlNr);
    for (Enumeration enu = blc.getBlElements ().elements (); enu.hasMoreElements ();){
      if ((el = (CGbElement) enu.nextElement ()) instanceof CGbSignal
      && ((CGbSignal) el).getIsGreen ())
        sendCommand (((CGbSignal)el).getId (), false, false);
    }
  }

  private final boolean checkFree (int iBlNr){
    boolean bIsFree = true;
    CBlContainer blc = getBlContainer (iBlNr);
    if (blc != null){
      Vector v = blc.getBlElements ();
      for (Enumeration en = v.elements (); en.hasMoreElements ();){
        CGbElement el = (CGbElement) en.nextElement ();
        if (!el.getIsFree ()){
        bIsFree = false;
        break;
        }
      }
    }
    return bIsFree;
  }

   private final void setFree (int iBlNr, boolean bFree){
    CBlContainer blc = getBlContainer (iBlNr);
    Vector v = blc.getBlElements ();
    for (Enumeration en = v.elements (); en.hasMoreElements ();){
      CGbElement el = (CGbElement) en.nextElement ();
      el.setIsFree (bFree);
    }
  }

  private final void checkBlockWaiting (int iBlNr){
    CBlock bl       = null;
    Iterator iter = CControlCenter.getBlockList ().iterator();
    while(iter.hasNext())
    {
      bl = (CBlock) iter.next();
      if (bl.getState () == CBlock.BLOCK_REQUESTED){
        if (checkFree (bl.getBlNumber ())){
          bl.setState (CBlock.BLOCK_FREE);
          if (!bl.getRequestors ().isEmpty ()){
            bl.setOwner ((String) bl.getRequestors ().firstElement());
            bl.getRequestors ().remove (bl.getRequestors ().firstElement ());
          }
          requestBlock (bl.getBlNumber (), bl.getOwner ());
        }
      }
    }
  }

  public class CWaiter extends Thread {
    CFbElement fb;
    int time;

    CWaiter (int millis, CFbElement fb){
      this.fb = fb;
      this.time = millis;
    }

    public void run (){
      this.setPriority (Thread.NORM_PRIORITY -4);
      fb.setActive (true);
      repaint ();
      try{
        Thread.sleep (5000L);
        fb.setActive (false);
        repaint ();
      }
      catch (InterruptedException e){}
    }
  }

  public final JButton getBlBox (int iBlNr){

    for (Enumeration en = blList.elements (); en.hasMoreElements ();){
      CBlContainer blc = (CBlContainer) en.nextElement ();
      if (blc.getBlNumber () == iBlNr)
        return blc.getBlBox();
    }
    return null;
  }

  public final boolean requestBlock (int iBlNr, String sRequestor){

    boolean bAvailable  = true;
    CBlock bl           = getBlock (iBlNr);
    CBlock b            = getBlock (bl.getBlockNeeded ());
    JButton blBox       = getBlContainer (iBlNr).getBlBox ();
    if (b != null && getBlContainer (bl.getBlockNeeded ()) != null){
      JButton blBox2      = getBlContainer (bl.getBlockNeeded ()).getBlBox ();
    }
    String sMore        = "++";

    //we don't need an undefined state
    if (bl.getState () == 0) bl.setState (CBlock.BLOCK_FREE);
    //requested block is free and -if existing- the extra block, too
    if (bl.getState() == CBlock.BLOCK_FREE &&
    (b == null || b.getState () == CBlock.BLOCK_FREE)){
        //AND all blockelements are free
      if (checkFree (iBlNr) && checkFree (bl.getBlockNeeded ())) {
        bl.setState (CBlock.BLOCK_EXECUTED);
        bl.setOwner (sRequestor);
        setFree (iBlNr, false);
        doBlJobs (iBlNr);
        refresh (iBlNr);
        //if extrablock exists
        if (b != null && getBlContainer (bl.getBlockNeeded ()) != null){
          setFree (b.getBlNumber (), false);
          b.setState (CBlock.BLOCK_EXECUTED);
          doBlJobs (b.getBlNumber ());
          b.setOwner (sRequestor);
          refresh (b.getBlNumber ());
          CBlockEventHandler.fireEvent
            (new CBlockEvent (this, b.getBlNumber (), b.getState (), sRequestor));
        }
      }
      //Block  is free, but not all of it's elements
      else {
        bl.setState (CBlock.BLOCK_REQUESTED);
        if (!bl.getRequestors ().contains (sRequestor) && !bl.getOwner ().equals (sRequestor))
          bl.getRequestors ().add (sRequestor);
        bl.setOwner (sRequestor);
      }
    }
    //Block is free, but not it's extrablock
    else if (bl.getState() == CBlock.BLOCK_FREE &&
    (b != null && b.getState () != CBlock.BLOCK_FREE)){
      if (!bl.getRequestors ().contains (sRequestor))
          bl.getRequestors ().add (sRequestor);
      bl.setOwner (sRequestor);
      bl.setState (CBlock.BLOCK_REQUESTED);
    }
    //If the block is not free we add sRequestor to the list of waiters
    else if (bl.getState() == CBlock.BLOCK_REQUESTED){
      if (!bl.getOwner ().equals (sRequestor)){
        if (!bl.getRequestors ().contains (sRequestor))
          bl.getRequestors ().add (sRequestor);
      }
    }
    //block is executed, so we alse add sRequestor to the list of waiters
    else {
      if (!bl.getOwner ().equals (sRequestor)){
        if (!bl.getRequestors ().contains (sRequestor))
          bl.getRequestors ().add (sRequestor);
      }
    }
    CBlockEventHandler.fireEvent
        (new CBlockEvent (this, iBlNr, bl.getState (), sRequestor));
    return bAvailable;
  }

  public final void releaseBlock (int iBlNr, String sRequestor){

    CBlock bl           = getBlock (iBlNr);
    CBlock b            = getBlock (bl.getBlockNeeded ());
    JButton blBox       = getBlContainer (iBlNr).getBlBox ();
    if (b != null && getBlContainer (bl.getBlockNeeded ()) != null){
      JButton blBox2 = getBlContainer (bl.getBlockNeeded()).getBlBox ();
    }
    String sMore        = "++";
    //if block is already free, there's nothing to do
    if (bl.getState () == CBlock.BLOCK_FREE)
      return;
    //if block is requested
    else if (bl.getState () == CBlock.BLOCK_REQUESTED){
      if (bl.getRequestors ().contains (sRequestor)){
        bl.getRequestors ().remove (sRequestor);
      }
      if (bl.getOwner ().equals (sRequestor) || sRequestor.equals ("User")){
        bl.setOwner ("");
        if (bl.getRequestors ().isEmpty ()){
          setFree (iBlNr, true);
          doUnsetBlJobs (iBlNr);
          bl.setState (CBlock.BLOCK_FREE);
          refresh (iBlNr);
        }
      }
    }
    else if (bl.getState () == CBlock.BLOCK_EXECUTED){
      if (bl.getOwner ().equals(sRequestor) || sRequestor.equals ("User")){
        if (bl.getRequestors ().isEmpty()){
          setFree (iBlNr, true);
          doUnsetBlJobs (iBlNr);
          bl.setState (CBlock.BLOCK_FREE);
          bl.setOwner ("");
          refresh (iBlNr);
        }
        else {
          bl.setOwner ((String)bl.getRequestors ().firstElement());
          if (b!= null){
            b.setState (CBlock.BLOCK_EXECUTED);
            if (b.getOwner ().equals ("")) b.setOwner (bl.getOwner ());
            else if (!b.getRequestors ().contains (bl.getOwner ()))
              b.getRequestors ().add (bl.getOwner ());
            refresh (b.getBlNumber ());
          }
          bl.getRequestors ().remove (bl.getRequestors ().firstElement ());
          refresh (iBlNr);
        }
      }
    }
    CBlockEventHandler.fireEvent
                (new CBlockEvent (this, iBlNr, bl.getState (), sRequestor));
    checkBlockWaiting (iBlNr);
  }

  private final void setFs () {
    CStreet street  = null;
    CBlock bl       = null;
    s = null;
    int iLast       = 0;
    CStreetDlg sdlg = new CStreetDlg (this, "Fahrstraße aktivieren", true);
    if (s!= null){
      for (Enumeration en = streetList.elements (); en.hasMoreElements () ;){
        street = (CStreet) en.nextElement ();
        if (street.getName ().equalsIgnoreCase (s)) {
          for (Enumeration enu = street.getBlocks (); enu.hasMoreElements ();){
            bl = (CBlock) enu.nextElement ();
            if (iLast != bl.getBlNumber())
              requestBlock (bl.getBlNumber (), street.getName ());
            iLast = bl.getBlockNeeded ();
          }
        break;
        }
      }
    }
  }

  private final void resetFs () {
    CStreet street  = null;
    CBlock bl       = null;
    s = null;
    CStreetDlg sdlg = new CStreetDlg (this, "Fahrstraße zurücksetzen", true);
    if (s!= null){
      for (Enumeration en = streetList.elements (); en.hasMoreElements () ;){
        street = (CStreet) en.nextElement ();
        if (street.getName ().equalsIgnoreCase (s)) {
          for (Enumeration enu = street.getBlocks (); enu.hasMoreElements ();){
            bl = (CBlock) enu.nextElement ();
            releaseBlock (bl.getBlNumber (), street.getName ());
          }
        break;
        }
      }
    }
  }

  public synchronized boolean sendCommand (int maId){
    boolean bModus = CControlCenter.getTurnoutList ()[maId].getIsGreen ();
    if (bModus == true) bModus = false;
    else if (bModus == false) bModus = true;
    CControlCenter.getTurnoutList ()[maId].setIsGreen (bModus);
    CTurnout.sendTcString (CControlCenter.getTurnoutList ()[maId]);
    CTurnoutHandler.fireEvent (new CTurnoutEvent (this, maId, bModus, false));
    return bModus;
  }

  public synchronized void sendCommand (int maId, boolean bToGreen, boolean bHor){
    boolean bModus = CControlCenter.getTurnoutList ()[maId].getIsGreen ();
    boolean bHorizontal = CControlCenter.getTurnoutList ()[maId].getHorizontal ();
    //if the position is already right, do nothing
    if (bModus == bToGreen && bHor == bHorizontal);
    //if not, let`s do the switching and create an event
    else {
      CControlCenter.getTurnoutList ()[maId].setIsGreen (bToGreen);
      CControlCenter.getTurnoutList ()[maId].setHorizontal (bHor);
      CTurnout.sendTcString (CControlCenter.getTurnoutList ()[maId]);
      CTurnoutHandler.fireEvent (new CTurnoutEvent (this, maId, bToGreen, bHor));
    }
  }

  public final void cleanUp (){
    CFeedbackportHandler.getFeedbackportHandler().
        removeFeedbackportListener (this);
    CTurnoutHandler.removeTurnoutListener (this);
    JTrain.mf.setControlmode (false);
  }

  public void actionPerformed (ActionEvent e){
    Object o = e.getSource ();
    if (o == fsset){
      setFs ();
    }
    else if (o == fsrel){
      resetFs ();
    }
    else if (o == autostart){
      CChainFrameDialog cfd = new CChainFrameDialog (this, "", true);
      cfd.show ();
    }
  }

  class MyMouseAdapter extends MouseAdapter {

    public void mousePressed (MouseEvent e){
      int id = 0;
      if (e.getSource () instanceof CGbSwitchEl
      && ((CGbElement)e.getSource ()).getIsFree ()){

        id =((CGbSwitchEl)e.getSource ()).getId ();
        mode = sendCommand (id);
      }
      else if (e.getSource () instanceof CGbSignal
      && ((CGbElement)e.getSource ()).getIsFree ()){
        id =((CGbSignal)e.getSource()).getId();
        mode = sendCommand (id);
      }
    }

    public void mouseReleased (MouseEvent e){
      if (e.getSource () instanceof JButton){
        if (e.getModifiers () == MouseEvent.BUTTON3_MASK){
          for (Enumeration en = blList.elements (); en.hasMoreElements ();){
            CBlContainer blc = (CBlContainer) en.nextElement ();
            JButton blBox = blc.getBlBox ();
            if (blBox == (JButton) e.getSource()){
              refresh (blc.getBlNumber ());
              break;
            }
          }
        }
        else if (e.getModifiers () == MouseEvent.BUTTON1_MASK){
          CBlock bl = null;
          for (Enumeration en = blList.elements (); en.hasMoreElements ();){
            CBlContainer blc = (CBlContainer) en.nextElement ();
            JButton blBox = blc.getBlBox ();
            if (blBox == (JButton) e.getSource()){
              int iBlNr = blc.getBlNumber();
              bl = getBlock (iBlNr);
              //if this block is not active
              if (bl.getState () != CBlock.BLOCK_EXECUTED){
                requestBlock (iBlNr, new String ("User"));
              }
              else if (bl.getState () == CBlock.BLOCK_EXECUTED){
                releaseBlock (iBlNr, new String ("User"));
              }
              break;
            }
          }
        }
      }
    }
  }

  class MyWindowAdapter extends WindowAdapter {
    public void windowClosing (WindowEvent e){
      cleanUp ();
    }
  }

}

