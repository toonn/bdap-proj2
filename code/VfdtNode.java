/**
   @author Jesse Davis
   @author Benjamin Negrevergne

   This class is a stub for VFDT. 

   (c) 2014
 */
import java.io.*;
import java.util.*;
public class VfdtNode{

    private VfdtNode left; /* left child (null if node is a leaf) */
    private VfdtNode right; /* right child (null if node is a leaf) */

    private int splitFeatureId; /* splitting feature */

    protected int[][][] nijk; /* instance counts (see paper) */
    /* FILL IN HERE */

    /**
       Create and initialize a leaf node. 
    */
    public VfdtNode(int numFeatures){
	left = null;
	right = null; 
	
	/* FILL IN HERE */
	
    }

    /** 
	Turn a leaf node into a internal node.
	
	@param testFeature is the feature to test on this node. 
	@param left is the left child (testFeature = 0). 
	@param right is the right child (testFeature = 1).
    */
    public void addChildren(int testFeature, VfdtNode left, VfdtNode right){
	
	/* FILL IN HERE */
	
    }

    /** 
	Returns the leaf node corresponding to the test example. 

	@param example is the test example to sort. 
    */
    public VfdtNode sortExample(int[] example){

	VfdtNode leaf = null; // change this

	/* FILL IN HERE */

	return leaf;
    }

    /**
       Split evaluation method (function G in the paper)
       
       Compute a splitting score for the feature featureId.
       For now, we'll use information gain, but this may be changed. 
       
       @param featureId is the feature to be considered. 
    */
    public double splitEval(int featureId){
	return informationGain(featureId, nijk);
    }

    /**
       Compute the information gain of a feature for this leaf node. 

       THIS METHOD IS REQUIRED.

       @param featureId is the feature to be considered. 
       @param nijk are the instance counts.
    */ 
    public static double informationGain(int featureId, int[][][] nijk){
	double ig = 0; 

	/* FILL IN HERE */
	
	return ig; 
    }


    public void printTree(){
	printTree("");
    }

    private void printTree(String indent){
	if (left==null || right==null){
	    System.out.println(indent+"Leaf");
	}
	else {
	    System.out.println(indent+splitFeatureId+"=0:");
	    left.printTree(indent+"| ");
	    System.out.println(indent+splitFeatureId+"=1:");
	    right.printTree(indent+"| ");
	}
    }

}
