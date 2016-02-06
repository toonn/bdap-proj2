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
  java LogisticRegression $(expr $i * 0.1) $TRAIN $TEST out 10
done

# Generate VFDT data
make Vfdt.class
make VfdtNode.class
for i in `seq 1 9`;
do
  rm -f exp"$i".vfdt.acc
  java Vfdt $(expr $i * 0.1) 0.0001 $TRAIN $TEST out 10
done

gnuplot exp.gnuplot
