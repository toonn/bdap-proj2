set logscale x
set nologscale y
set xtic auto
set ytic auto
set xlabel "Number of training examples"
set ylabel "Accuracy"
set key right bottom
set terminal epslatex

set output "exp-lr.tex"
plot for [i=1:9] 'exp'.i.'.lr.acc' using 1:2 title '\eta = 0.'.i with linespoints

set output "exp-vfdt.tex"
plot for [i=1:9] 'exp'.i.'.vfdt.acc' using 1:2 title '\delta = 0.'.i with linespoints
