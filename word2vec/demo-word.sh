make

time ./word2vec -train trwiki -output vectors.txt -cbow 1 -size 200 -window 8 -negative 25 -hs 0 -sample 1e-4 -threads 0 -binary 0 -iter 15
./distance vectors.txt
