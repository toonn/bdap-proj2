#!/bin/sh

# Big synthetic dataset, no noise
TRAIN=../data/synthetic/bigdata.0.25.0.00.train
TEST=../data/synthetic/bigdata.0.25.0.00.test

TRAIN=../data/zoo-train.dat
TEST=../data/zoo-test.dat

# Generate LR data
make LogisticRegression.class
java LogisticRegression 0.0001 $TRAIN $TEST out 10

# Generate VFDT data
make Vfdt.class
make VfdtNode.class
java Vfdt 0.01 0.0001 $TRAIN $TEST out 10


