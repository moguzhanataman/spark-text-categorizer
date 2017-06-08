package Algorithms;

public class CosineSimilarity implements Similarity{
	
	/**
	 * calculate the consine similarity of two vector
	 * @param x
	 * @param y
	 * @return
	 */
	public double getSimilarity(double []x,double []y){
		double res = 0;
		double sx = 0;
		double sy = 0;
		if( x.length != y.length ){
			System.out.println ("The length of input vector is not consistent.");
			return 0;
		}
		
		for( int i = 0; i < x.length; ++ i ){
			res += x[i]*y[i];
			sx += x[i]*x[i];
			sy += y[i]*y[i];
		}
		
		return res/(Math.sqrt(sx)*Math.sqrt(sy));
	}
	
}
