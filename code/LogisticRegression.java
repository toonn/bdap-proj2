/**
  @author Jesse Davis
  @author Benjamin Negrevergne
  @author Jessa Bekker

  This class is a stub for incrementally building a Logistic Regression model. 

  (c) 2014
  */
import java.io.*;
import java.util.*;

public class LogisticRegression{

  private int numFeatures;
  private int classPosition;
  private int examplesProcessed;
  private double learningRate;
  private double[] theta;


  /**
    LogisticRegression constructor.

    THIS METHOD IS REQUIRED 

    @param numFeatures is the number of features. 
    @param classPosition position of the label in the feature vector.
    @param learningRate is the learning rate
    */
  public LogisticRegression(int numFeatures, int classPosition,
                            double learningRate){
    this.numFeatures = numFeatures;
    this.classPosition = classPosition;
    this.learningRate = learningRate;
    examplesProcessed = 0;

    /*
       FILL IN HERE
       You will need other data structures, initialize them here
       */
    this.theta = new double[numFeatures+1];
    Arrays.fill(this.theta, 0);
  }

  private double h(int[] x) {
    // Inner product of theta and x
    double theta_x = theta[0];
    int offset = 1;
    for (int i = 0; i < x.length; i++) {
      if (i == classPosition) {
        // offset has to change because the class has to be skipped in the
        // feature vector but the parameter vector has no corresponding
        // element
        offset = 0;
        continue;
      } else {
        theta_x += x[i] * theta[i+offset];
      }
    }

    return 1 / (1 + Math.exp(-theta_x));
  }

  /**

    This method will update the parameters of your model using new training examples. 

    Uses the training data to update the current parameters of the model. 

    THIS METHOD IS REQUIRED

    @param examples is a set of training examples
    */
  public void updateParameters(int[][] examples){
    for(int[] example : examples) {
      double factor = learningRate * (h(example) - example[classPosition]);

      theta[0] += theta[0] - factor;
      int offset = 1;
      for (int i = 0; i < example.length; i++) {
        if (i == classPosition) {
          // offset has to change because the class has to be skipped in the
          // feature vector but the parameter vector has no corresponding
          // element
          offset = 0;
          continue;
        } else {
          theta[i+offset] += theta[i+offset] - factor * example[i];
        }
      }
    }
  }


  /**
    Uses the current model to calculate the probability that an
    example belongs to class "1";

    THIS METHOD IS REQUIRED

    @param example is a test example 
    @return the probability that example belongs to class "1"
    */
  public double makePrediction(int[] example){

    /*
       FILL IN HERE
       Compute the probability that example belongs to class "1"
       */ 
    return 0.0d;
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

    DO NOT CHANGE THIS METHOD

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

    If the file doesn't exist, a new file named "file + .nb.acc" is
    created.  Each call to this function adds a new line into the
    file. The lines have the form:
    "examplesProcessed <tab> accuracy\n".

    DO NOT CHANGE THIS METHOD

    @param file the stem of the output file
    @param data is the test data 
    @param thres is the threshold for labeling an example as belonging to class "1"
    */
  public void writeAccuracyToFile(String file, int[][] data, double thres){
    double accuracy = computeAccuracy(data, thres); 
    try{
      RandomAccessFile out = new RandomAccessFile(file + ".nb.acc", "rw");  
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

    DO NOT CHANGE THIS METHOD 

*/
  public static void main(String[] args){
    if (args.length < 5){
      System.err.println("Usage: java LogisticRegression <learningRate> <training set> <testset> <output file> <increment>");
      throw new Error("Expected 4 arguments, got "+args.length+"."); 
    }
    Data data = new Data(args[1], args[2], ",");
    int[][] testData = data.getTestData();
    LogisticRegression nb = new LogisticRegression(testData[0].length, testData[0].length-1, Double.parseDouble(args[0]));
    int streamSize = Integer.parseInt(args[4]);
    System.out.println("Start training");
    while (data.hasMoreTrainData()){
      /* Process a new training example and measure the accuracy
       * on the whole test set.*/
      int[][] nextExamples = data.getTrainExamples(streamSize);  
      nb.updateParameters(nextExamples);
      System.out.println(nb.examplesProcessed + 
          " example(s) processed, accuracy on the test set: " + 
          nb.computeAccuracy(testData, 0.5)*100 + "%.");
      nb.writeAccuracyToFile(args[3], testData, 0.5);
      nb.writeAllPredictionsToFile(args[3], testData);
      streamSize *= Integer.parseInt(args[4]);
    }

  }

}
