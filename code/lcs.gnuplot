set autoscale
set xtic auto
set ytic auto
set xlabel "Number of training examples"
set ylabel "Accuracy"
set terminal epslatex

set output "lc_lr.tex"
plot "out.lr.acc" using 1:2 title "Logistic Regression" with linespoints

set output "lc_vfdt.tex"
plot "out.vfdt.acc" using 1:2 title "VFDT" with linespoints
