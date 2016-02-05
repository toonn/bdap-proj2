/**
  @author Jesse Davis
  @author Benjamin Negrevergne

  This class is a stub for VFDT. 

  (c) 2014
  */
import java.io.*;
import java.util.*;
public class Vfdt{

  private int numFeatures;
  private int classPosition;
  private int examplesProcessed;
  private double delta;
  private double tau;
  private int nmin;

  private VfdtNode root;

  /**
    Vfdt constructor

    THIS METHOD IS REQUIRED 

    @param numFeatures is the number of features. 
    @param classPosition position of the label in the feature vector. 
    */
  public Vfdt(int numFeatures, int classPosition, double delta, double tau){
    this.numFeatures = numFeatures;
    this.classPosition = classPosition;
    this.delta = delta;
    this.tau = tau;
    examplesProcessed = 0;

    this.root = new VfdtNode(numFeatures, classPosition);
    nmin = 5;
  }

  /**
    This method will update the decision tree using a new set of training example. 

    THIS METHOD IS REQUIRED 

    @param examples is a set of examples 
    */
  public void update(int[][] examples){
    for (int[] example : examples) {
      examplesProcessed += 1;
      VfdtNode l = root.sortExample(example);
      l.incrementNijk(example);
      if (! l.allTheSame()) {
        l.evaluateSplit(delta, tau, nmin);
      }
    }
  }


  /**
    Uses the current model to calculate the probability that an
    example belongs to class "1";

    THIS METHOD IS REQUIRED

    @param instance is a the test instance to classify
    @return the probability that example belongs to class "1"
    */
  public double makePrediction(int[] example){

    double prediction = root.sortExample(example).majority();

    return prediction;
  }

  /**
    Use makePrediction() to compute the probability of each example
    in the test set to belongs to class "1".

    The predictions are then written to a file named "file +
    examplexProcessed + .probs".

    The file format has the form of Prob(example[ix] belongs to
    class "1") + tab + true label of example.

    DO NOT CHANGE THIS METHOD

    @param file the stem of the output file
    @param data is the test data 
    */
  public void writeAllPredictionsToFile(String file, int[][] data){
    try{
      RandomAccessFile out = new RandomAccessFile(file + examplesProcessed + ".probs", "rw");	
      for(int[] testInstance : data){
        out.writeBytes(makePrediction(testInstance) + "\t" +
            testInstance[classPosition] + "\n");
      }
    }//end try block
    catch(IOException exc){
      System.out.println(exc.toString());
    }
  }

  /** 
    Computes accuracy of the current model on the test set.

    A prediction for an instance labeled "1" is considered to be
    correct iff the model predicts the label "1" with probability
    above the probability threshold.

    DO NOT CHANGE THIS METHOD.

    @param data is the test set. 
    @param thres is the probability threshold. 
    */
  public double computeAccuracy(int [][]data, double thres){
    double correct = 0;
    for(int ix = 0; ix < data.length; ix++){
      double predict = makePrediction(data[ix]);
      if (((data[ix][classPosition] == 0) && (predict <= thres)) ||
          ((data[ix][classPosition] == 1) && (predict > thres))){
        correct++;
          }
    }
    return correct / data.length; 
  }

  /**
    Computes accuracy of the current model on the test set and
    store the result into a file.

    If the file doesn't exist, a new file named "file + .vfdt.acc" is
    created.  Each call to this function adds a new line into the
    file. The lines have the form:
    "examplesProcessed <tab> accuracy\n".

    DO NOT CHANGE THIS METHOD. 

    @param file the stem of the output file
    @param data is the test data 
    @param thres is the threshold for labeling an example as belonging to class "1"
    */
  public void writeAccuracyToFile(String file, int[][] data, double thres){
    double accuracy = computeAccuracy(data, thres); 
    try{
      RandomAccessFile out = new RandomAccessFile(file + ".vfdt.acc", "rw");	
      long fileLength = out.length();
      out.seek(fileLength);
      out.writeBytes(examplesProcessed + "\t" + accuracy + "\n");

    }//end try block
    catch(IOException exc){
      System.out.println(exc.toString());
    }
  }

  /**
    This runs your code to generate the required output for the assignment.

    DO NOT CHANGE THIS METHOD.
    */
  public static void main(String[] args){
    if (args.length < 4){
      System.err.println("Usage: java Vfdt <delta> <tau> <training set> <testset> <output file> <increment>");
      throw new Error("Expected 6 arguments, got "+args.length+"."); 
    }
    Data data = new Data(args[2], args[3], ",");
    int[][] testData = data.getTestData();
    double delta = Double.parseDouble(args[0]);
    double tau = Double.parseDouble(args[1]);
    Vfdt vfdt = new Vfdt(testData[0].length, testData[0].length-1, delta, tau);
    int streamSize = Integer.parseInt(args[5]);

    while (data.hasMoreTrainData()){
      /* Process a new training example and measure the accuracy
       * on the whole test set.*/
      int[][] nextExamples = data.getTrainExamples(streamSize);  
      vfdt.update(nextExamples);
      System.out.println(vfdt.examplesProcessed + 
          " example(s) processed, accuracy on the test set: " + 
          vfdt.computeAccuracy(testData, 0.5)*100 + "%.");
      vfdt.writeAccuracyToFile(args[4], testData, 0.5);
      //      vfdt.writeAllPredictionsToFile(args[4], testData);
      streamSize *= Integer.parseInt(args[5]);
    }

  }

}
