package de.jtrain.gb;
/*
This class defines nodes needed for autorouting in the GbBlockEditor

last modified: 2002 03 06
*/


public class CNode {
  private CGbElement root;
  private CGbElement tree_a;
  private CGbElement tree_b;

  CNode (CGbElement root){
    super ();
    this.root = root;
  }

  public void setRoot (CGbElement gbel){this.root = gbel;}
  public void setTree_a (CGbElement gbel){this.tree_a = gbel;}
  public void setTree_b (CGbElement gbel){this.tree_b = gbel;}
  public CGbElement getRoot (){return root;}
  public CGbElement getTree_a (){return tree_a;}
  public CGbElement getTree_b (){return tree_b;}
}

