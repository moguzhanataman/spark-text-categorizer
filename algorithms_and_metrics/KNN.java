package Algorithms;

import java.util.ArrayList;
import java.util.List;

public class KNN {
	private Similarity similarity;
	public KNN(Similarity similarity){
		this.similarity = similarity;
	}
	public int run(int K,List<double[][]> class_vecs,double[] doc_vec){
		List<Integer> type = new ArrayList<Integer>();
		List<Double> dist_list = new ArrayList<Double>();

		int tp = 0;
		for( double [][] vecs: class_vecs ){
			for( double[] vec: vecs){
				/** if use KL the reference vector should be the second parameter*/
				dist_list.add( similarity.getSimilarity(doc_vec, vec) );
				type.add(tp);
			}
			tp ++;
		}
		
		double [] dists = new double[dist_list.size()];
		int i = 0;
		for( double d: dist_list)
			dists[i++] = d;
		TopKSelector selector = new TopKSelector(K);
		int []top = selector.getTopK(dists);
		int C[] = new int[tp];
		for( int x : top){
			C[ type.get(x) ] ++;
		}
		int max = 0;
		for( i = 1; i < C.length; ++ i )
		{
			if( C[max] < C[i]) max = i;
		}
		return max;
	}
}
