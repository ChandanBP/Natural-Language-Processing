/* Author - Chandan Parameswaraiah
 * cbp140230*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Viterbi {

	String obsSequence[];
	int hiddenStates;
	int backPointer[][];
	double viterbi[][];
	double initialProb[]={0.8,0.2};
	double transitionProb[][]={{0.7,0.3},
			                  {0.4,0.6}};
	double obProb[][] = {{0.2,0.5},
						{0.4,0.4},
						{0.4,0.1}};
	
	public void process(String line){
		
		hiddenStates = 2;
		obsSequence = new String[line.length()];
		
		viterbi = new double[hiddenStates+1][line.length()];
		backPointer = new int[hiddenStates+1][line.length()];
		
		
		// First initialises the initial property
		int firstSeq = Integer.parseInt(line.charAt(0)+"");
		firstSeq-=1;
		for (int i = 0; i < hiddenStates; i++) {
			viterbi[i][0] = obProb[firstSeq][i] * initialProb[i]; 
			backPointer[i][0]=-1;
		}
		
		int seq;
		double max;
		double prob;
		for (int j = 1; j < obsSequence.length; j++) {
			
			seq = Integer.parseInt(line.charAt(j)+"");
			seq-=1;
			double val1,val2,val3;
			for (int i = 0; i < hiddenStates; i++) {
				max = Float.MIN_VALUE;
				for (int k = 0; k < hiddenStates; k++) {
					val1 = viterbi[k][j-1];
					val2 = transitionProb[k][i];
					val3 = obProb[seq][i];
					prob = val1*val2*val3;
					//prob = (int)Math.round(prob * 10000)/(double)10000;
					if(max<prob){
						max=prob;
						backPointer[i][j] = k;
					}
				}
				viterbi[i][j] = max;
			}
		}
		
		// Find the one with maximum probability
		max = Float.MIN_VALUE;
		int index = Integer.MIN_VALUE;
		for (int i = 0; i < hiddenStates; i++) {
			if(max<viterbi[i][obsSequence.length-1]){
				max = viterbi[i][obsSequence.length-1];
				viterbi[hiddenStates][obsSequence.length-1]=max;
				backPointer[hiddenStates][obsSequence.length-1]=i;
				index=i;
			}
		}
		
		
		// Trace the path
		int j = obsSequence.length-1;
		int i = hiddenStates;
		StringBuilder sb = new StringBuilder();
		if(index==0){
			sb.append("H");
		}
		else{
			sb.append("C");
		}
		while(j>=1){
			
			seq = backPointer[i][j];
			if(seq==0){
				sb.append("H");
			}
			if(seq==1){
				sb.append("C");
			}
			i=seq;
			j--;
		}
		
		System.out.println("Likelihood weather sequence is "+sb.reverse().toString());
	}
	
	public static void main(String args[]){
		
		try{
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter the input sequence");
			String line = br.readLine();
			Viterbi hw = new Viterbi();
			hw.process(line);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
}
