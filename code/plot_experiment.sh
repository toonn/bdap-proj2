#!/bin/sh

# Big synthetic dataset, no noise
TRAIN=../data/synthetic/bigdata.0.25.0.00.train
TEST=../data/synthetic/bigdata.0.25.0.00.test

#TRAIN=../data/zoo-train.dat
#TEST=../data/zoo-test.dat

# Generate LR data
make LogisticRegression.class
for i in `seq 1 9`;
do
  rm -f exp"$i".lr.acc
  java LogisticRegression $(echo "($i * 0.1)" | bc) $TRAIN $TEST exp"$i" 10
done

# Generate VFDT data
make Vfdt.class
make VfdtNode.class
for i in `seq 1 9`;
do
  rm -f exp"$i".vfdt.acc
  java Vfdt $(echo "($i * 0.1)" | bc) 0.0001 $TRAIN $TEST exp"$i" 10
done

gnuplot -p exp.gnuplot
