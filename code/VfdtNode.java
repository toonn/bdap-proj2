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

  private boolean split;
  private int numFeatures;
  private int classPosition;
  private List<Integer> X;
  // When majorityClass >= 0 the majority class is positive else negative
  private int majorityClass;
  private int accumulatedExamples;
  private int accSinceGComputation;

  /**
    Create and initialize a leaf node. 
    
    This should only be used for the root node to split a node on a certain
    feature see split.
    */
  public VfdtNode(int numFeatures){
    // assume classPosition is the last element
    classPosition = numFeatures;
    X = new ArrayList<Integer>();
    for (int i = 0; i < numFeatures; i++) {
      if (i != classPosition) {
        X.add(i);
      }
    }

    this.init(numFeatures, classPosition, X);
  }

  /**
    Create a root node with a known classPosition
    */
  public VfdtNode(int numFeatures, int classPosition) {
    X = new ArrayList<Integer>();
    for (int i = 0; i < numFeatures; i++) {
      if (i != classPosition) {
        X.add(i);
      }
    }

    this.init(numFeatures, classPosition, X);
  }

  private VfdtNode(int numFeatures, int classPosition, List<Integer> X) {
    this.init(numFeatures, classPosition, X);
  }
  
  private void init(int numFeatures, int classPosition, List<Integer> X) {
    left = null;
    right = null; 

    this.numFeatures = numFeatures;
    nijk = new int[numFeatures][2][2];
    split = false;
    this.classPosition = classPosition;
    this.X = X;
    majorityClass = 0;
    accumulatedExamples = 0;
    accSinceGComputation = 0;
  }

  private void split(int testFeature) {
    splitFeatureId = testFeature;
    // remove element; not at index
    X.remove(Integer.valueOf(splitFeatureId));
    
    left = new VfdtNode(numFeatures, classPosition,
                        new ArrayList<Integer>(X));
    right = new VfdtNode(numFeatures, classPosition,
                         new ArrayList<Integer>(X));
  }

  private double epsilon(double delta) {
    return Math.sqrt(Math.log(1/delta)/(2*accumulatedExamples));
  }

  public void evaluateSplit(double delta, double tau, int nmin) {
    if (accSinceGComputation >= nmin) {
      accSinceGComputation = 0;
      int a_i = 0;
      double a = Double.NEGATIVE_INFINITY;
      int b_i = 0;
      double b = Double.NEGATIVE_INFINITY;
      for (int i : X) {
        double G = splitEval(i);
        if (G > a) {
          b_i = a_i;
          b = a;
          a_i = i;
          a = G;
        } else if (G > b) {
          b_i = i;
          b = G;
        }
      }
      if (a - b > epsilon(delta)) {
        split(a_i);
      } else if (a - b < tau) {
        // if DeltaG < tau there's a tie, break it
        split(a_i);
      }
    }
  }

  /** 
    Turn a leaf node into a internal node.

    This method does not do all the bookkeeping required and violates
    encapsulation.

    @param testFeature is the feature to test on this node. 
    @param left is the left child (testFeature = 0). 
    @param right is the right child (testFeature = 1).
    */
  public void addChildren(int testFeature, VfdtNode left, VfdtNode right){
    split = true;
    splitFeatureId = testFeature;
    this.left = left;
    this.right = right;
  }

  /** 
    Returns the leaf node corresponding to the test example. 

    @param example is the test example to sort. 
    */
  public VfdtNode sortExample(int[] example){
    VfdtNode leaf = this;
    if (split) {
      leaf = (0 == example[splitFeatureId])
             ? left.sortExample(example)
             : right.sortExample(example);
    }

    return leaf;
  }

  public void incrementNijk(int[] example) {
    int k = example[classPosition];
    accumulatedExamples += 1;
    accSinceGComputation += 1;
    if (k == 0) {
      majorityClass -= 1;
    } else {
      majorityClass += 1;
    }
    for (int i : X) {
      // i only for features to be checked, j is index of value xij
      // -> here 0 and 1 so values are their own index and are found at
      // example[i], k is the index of the class yk and is found at
      // classPosition
      int j = example[i];
      nijk[i][j][k] += 1;
    }
  }

  public boolean allTheSame() {
    return (Math.abs(majorityClass) == accumulatedExamples);
  }

  public int predict() {
    return (majorityClass + accumulatedExamples) / (2*accumulatedExamples);
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

  private static double entropy(double P, double N) {
    return -P * Math.log(P)/Math.log(2) - N * Math.log(N)/Math.log(2);
  }

  /**
    Compute the information gain of a feature for this leaf node. 

    THIS METHOD IS REQUIRED.

    @param featureId is the feature to be considered. 
    @param nijk are the instance counts.
    */ 
  public static double informationGain(int featureId, int[][][] nijk){
    int count = 0;
    for (int j = 0; j < 2; j++) {
      for (int k = 0; k < 2; k++) {
        count += nijk[featureId][j][k];
      }
    }
    double P = (nijk[featureId][0][1] + nijk[featureId][1][1]) / count;
    double N = (nijk[featureId][0][0] + nijk[featureId][1][0]) / count;

    double ig = entropy(P,N); 

    for (int j = 0; j < 2; j++) {
      int count_j = 0;
      for (int k = 0; k < 2; k++) {
        count_j += nijk[featureId][j][k];
      }
      if (count_j > 0) {
        double P_j = nijk[featureId][j][1] / count_j;
        double N_j = nijk[featureId][j][0] / count_j;

        ig -= (count_j/count) * entropy(P_j, N_j);
      }
    }

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
