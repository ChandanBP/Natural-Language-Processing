import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class BigramModelling {

	int vocabulary=0;
	int N1=0,N2=0;
	float bigramCount1[][];
	float bigramCount2[][];
	float biProbWthSmt1[][];
	float biProbWthSmt2[][];
	float biProbaddOne1[][];
	float biProbaddOne2[][];
	float biProbGTuring1[][];
	float biProbGTuring2[][];
	
    Node sentenceTrie1 = new Node();
    Node sentenceTrie2 = new Node();
    Node root = new Node();
    HashMap<String, Integer>map = new HashMap<>();
    
    PriorityQueue<GoodTuring>s1Q = new PriorityQueue<>();
    PriorityQueue<GoodTuring>s2Q = new PriorityQueue<>();
    
    TreeMap<Float, Integer>s1Map = new TreeMap<>();
    TreeMap<Float, Integer>s2Map = new TreeMap<>(); 
    
   class Node implements Comparable<Node>{
		
		char alpha;
		String word;
		int frequency;
		int index;
		Node next[];
		@Override
		public int compareTo(Node o) {
			return o.frequency-this.frequency;
		}
	}
   
   class GoodTuring implements Comparable<GoodTuring>{
	   
	   int nc;
	   int count;
	
	   @Override
	public int compareTo(GoodTuring o) {
		return this.nc-o.nc;
	}
	   
   }
   
public void readCorpus(String s){
		
		try{

			BufferedReader br = new BufferedReader(new FileReader("Corpus.txt"));
			String line = br.readLine();
			String words[];
			
			int prevWord1,prevWord2;
			int nextWord1,nextWord2;
			
			while(line!=null){
				
				words = line.split(" ");
				String word;
				for (int i = 0; i < words.length; i++) {
					
					word = words[i];
					//System.out.println(word);
					if(word.length()>1&&word.charAt(word.length()-1)=='.'){
		    			word = word.substring(0, word.length()-1);
		        	}
					Node node = root;
					int index;
					
					if(i>0){
						prevWord1 = checkIfWordIsPresent(words[i-1],sentenceTrie1);
						nextWord1 = checkIfWordIsPresent(word,sentenceTrie1);
						if(prevWord1!=-1 && nextWord1!=-1){
							bigramCount1[prevWord1][nextWord1]++;
						}
						
						prevWord2 = checkIfWordIsPresent(words[i-1],sentenceTrie2);
						nextWord2 = checkIfWordIsPresent(word,sentenceTrie2);
						if(prevWord2!=-1 && nextWord2!=-1){
							bigramCount2[prevWord2][nextWord2]++;
						}
					}
					
					for (int j = 0; j < word.length(); j++) {
						
						index = word.charAt(j);
						if(node.next==null){
							node.next = new Node[256];
						}
						
						if(node.next[index] == null){
							node.next[index] = new Node();
						}
						node = node.next[index];
					}
					
					node.frequency++;
					if(node.frequency==1){
						vocabulary++;
					}
					node.word = word;
				}
				line = br.readLine();
			}
			
		}
	    catch(FileNotFoundException fileNotFoundException){
	    	fileNotFoundException.printStackTrace();
	    }
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
    public int checkIfWordIsPresent(String word,Node sentence){
    	
    	Node node = sentence;
        int index=-1;
    	
        
        for (int i = 0; i < word.length(); i++) {
			
        	index = word.charAt(i);
        	if(node.next==null){
        		return -1;
        	}
        	else if(node.next[index]!=null){
    			node = node.next[index];
    			index = node.index;
    		}
    		else{
    			return -1;
    		}
		}
    	if(node.next==null){
    		return index;
    	}
    	else{
    		return -1;
    	}
    	
    }

    public void generateBiGrams(String s,Node sentence,boolean isFirst){
    	
    	String words[] = s.split(" ");
    	String word;
    	int length = words.length;
    	
    	if(isFirst){
    		bigramCount1 = new float[length][length];
    	}
    	else{
    		bigramCount2 = new float[length][length];
    	}
  
    	Node node;
    	for (int i = 0; i < words.length; i++) {
    		
    		word = words[i];
    		
    		if(word.charAt(word.length()-1)=='.'){
    			word = word.substring(0, word.length()-1);
        	}
    		
    		int index;
    		node  = sentence;
    		for (int j = 0; j < word.length(); j++) {
    			
    			index = word.charAt(j);
    			if(node.next == null){
					node.next = new Node[256];
				}
    			
    			if(node.next[index] == null){
					node.next[index] = new Node();
				}
    			node = node.next[index];
    		}
    		node.index = i;
    	}
    }

    public void printBigramCount(){
    	
    	System.out.println("Bigram count for Sentence1");
    	int count;
    	
    	for (int i = 0; i < bigramCount1.length; i++) {
			for (int j = 0; j < bigramCount1.length; j++) {
				System.out.print(bigramCount1[i][j]+" ");
				if(bigramCount1[i][j]>0){
					N1++;
				}
				if(s1Map.get(bigramCount1[i][j])==null){
					s1Map.put(bigramCount1[i][j], 1);
				}
				else{
					count = s1Map.get(bigramCount1[i][j]);
					count=count+1;
					s1Map.put(bigramCount1[i][j], count);
				}
				
			}
			System.out.println();
		}
    	
    	System.out.println();
    	System.out.println("Bigram count for Sentence2");
    	for (int i = 0; i < bigramCount2.length; i++) {
			for (int j = 0; j < bigramCount2.length; j++) {
				System.out.print(bigramCount2[i][j]+" ");
				if(bigramCount2[i][j]>0){
					N2++;
				}
				if(s2Map.get(bigramCount2[i][j])==null){
					s2Map.put(bigramCount2[i][j], 1);
				}
				else{
					count = s2Map.get(bigramCount2[i][j]);
					count=count+1;
					s2Map.put(bigramCount2[i][j], count);
				}
			}
			System.out.println();
		}
    }
    
    public int getFrequency(String word){
    	
    	Node node = root;
        int freq=-1;
    	int index;
        
        for (int i = 0; i < word.length(); i++) {
			
        	index = word.charAt(i);
        	if(node.next==null){
        		return -1;
        	}
        	else if(node.next[index]!=null){
    			node = node.next[index];
    			index = node.index;
    		}
    		else{
    			return -1;
    		}
		}
        return node.frequency;
    }
    
    public void withoutSmoothing(String s,boolean isFirst){
    	
    	int frequency;
    	
    	String words[] = s.split(" ");
    	String word;
    	float val;
    	
    	if(isFirst){
    		biProbWthSmt1 = new float[bigramCount1.length][bigramCount1.length];
    		System.out.println("Without Smoothing Bigram probability for first sentence");
    		for (int i = 0; i < bigramCount1.length; i++) {
        		
        		word = words[i];
        		if(word.charAt(word.length()-1)=='.'){
        			word = word.substring(0, word.length()-1);
            	}
    			frequency = getFrequency(word);
    			map.put(word, frequency);
        		for (int j = 0; j < bigramCount1.length; j++) {
        			if(frequency!=-1){
        				val = bigramCount1[i][j]/frequency;
        				biProbWthSmt1[i][j] = val;
        			}
        			System.out.print(biProbWthSmt1[i][j]+" ");
    			}
        		System.out.println();
    		}
    	}
    	else{
    		biProbWthSmt2 = new float[bigramCount2.length][bigramCount2.length];
    		System.out.println("Without Smoothing Bigram probability for Second sentence");
    		for (int i = 0; i < bigramCount2.length; i++) {
        		
        		word = words[i];
        		if(word.charAt(word.length()-1)=='.'){
        			word = word.substring(0, word.length()-1);
            	}
    			frequency = getFrequency(word);
    			map.put(word, frequency);
        		for (int j = 0; j < bigramCount2.length; j++) {
        			if(frequency!=-1){
        				val = bigramCount2[i][j]/frequency;
        				biProbWthSmt2[i][j] = val;
        			}
        			System.out.print(biProbWthSmt2[i][j]+" ");
    			}
        		System.out.println();
    		}
    	}
    	
    }
    
    public void addOneSmoothing(String s,boolean isFirst){
    	
    	int frequency;
    	
    	String words[] = s.split(" ");
    	String word;
    	float val;
    	if(isFirst){
    		biProbaddOne1 = new float[bigramCount1.length][bigramCount1.length];
    		System.out.println("AddOne Smoothing Bigram probability for first sentence");
    		for (int i = 0; i < bigramCount1.length; i++) {
        		
        		word = words[i];
        		if(word.charAt(word.length()-1)=='.'){
        			word = word.substring(0, word.length()-1);
            	}
    			
        		frequency = map.get(word);
        		frequency+=vocabulary;
        		
    			for (int j = 0; j < bigramCount1.length; j++) {
        			
    				biProbaddOne1[i][j] = bigramCount1[i][j]+1;
        			val = (float)biProbaddOne1[i][j]/frequency;
        			val = (float)Math.round(val * 1000000)/(float)1000000;
        			biProbaddOne1[i][j] = val;
        			System.out.print(biProbaddOne1[i][j]+" ");
    			}
    			System.out.println();
    		}
    	}
    	else{
    		biProbaddOne2 = new float[bigramCount2.length][bigramCount2.length];
    		System.out.println("AddOne Smoothing Bigram probability for Second sentence");
    		for (int i = 0; i < bigramCount2.length; i++) {
        		
        		word = words[i];
        		if(word.charAt(word.length()-1)=='.'){
        			word = word.substring(0, word.length()-1);
            	}

        		frequency = map.get(word);
        		frequency+=vocabulary;
        		
        		for (int j = 0; j < bigramCount2.length; j++) {
        			biProbaddOne2[i][j]=bigramCount2[i][j]+1;
        			val = (float)biProbaddOne2[i][j]/frequency;
        			val = (float)Math.round(val * 1000000)/(float)1000000;
        			biProbaddOne2[i][j] = val;
        			System.out.print(biProbaddOne2[i][j]+" ");
    			}
        		System.out.println();
    		}
    	}
    }    
    
    public float probability(float bigramCount[][]){
    	
    	int j;
    	float prob=1;
    	
    	for (int i = 1; i < bigramCount.length; i++) {
			j=i-1;
			prob = prob*bigramCount[i][j];
		}
    	
    	return prob;
    }
    
    public void goodTuringDiscounting(String s,boolean isFirst){
    	
    	int nc;
    	float ncplusone;
    	float c;
    	float cstar;
    	float key;
		
    	
    	if(isFirst){
    		System.out.println("Bigram probability for good turing discount for sentence 1");
    		biProbGTuring1 = new float[bigramCount1.length][bigramCount1.length];
    		Iterator ite;
    		
    		for (int i = 0; i < bigramCount1.length; i++) {
				for (int j = 0; j < bigramCount1.length; j++) {
					c = bigramCount1[i][j];
					ncplusone=(int)c+1;
					ite = s1Map.entrySet().iterator();
					while(ite.hasNext()){
		    			Map.Entry<Float, Integer>pair = (Map.Entry<Float, Integer>)ite.next();
		    			key = pair.getKey();
		    			ncplusone = pair.getValue();
		    			if(key>c){
		    				break;
		    			}
		    		}
					
					nc = s1Map.get(c);
					ncplusone = ncplusone/nc;
					cstar = (c+1)*(ncplusone);
					if(c>0){
						cstar = cstar*(c/N1);
					}
					biProbGTuring1[i][j]=cstar;
					System.out.print(biProbGTuring1[i][j]+" ");
				}
				System.out.println();
			}
    	}
    	else{
    		
    		biProbGTuring2 = new float[bigramCount2.length][bigramCount2.length];
    		Iterator ite;
    		System.out.println("Bigram probability for good turing disscount for sentence 2");
    		
    		for (int i = 0; i < bigramCount2.length; i++) {
				for (int j = 0; j < bigramCount2.length; j++) {
					c = bigramCount2[i][j];
					ncplusone=(int)c+1;
					ite = s2Map.entrySet().iterator();
					while(ite.hasNext()){
		    			Map.Entry<Float, Integer>pair = (Map.Entry<Float, Integer>)ite.next();
		    			key = pair.getKey();
		    			ncplusone = pair.getValue();
		    			if(key>c){
		    				break;
		    			}
		    		}
					nc = s2Map.get(c);
					ncplusone = ncplusone/nc;
					cstar = (c+1)*(ncplusone);
					if(c>0){
						cstar = cstar*(c/N2);
					}
					biProbGTuring2[i][j]=cstar;
					System.out.print(biProbGTuring2[i][j]+" ");
				}
				System.out.println();
			}
    	}
    	
    }
    
	public static void main(String args[]){
		
		BigramModelling obj = new BigramModelling();
		String S1 = "The president has relinquished his control of the company's board.";
		String S2 = "The chief executive officer said the last year revenue was good.";
		
		obj.generateBiGrams(S1,obj.sentenceTrie1,true);
		obj.generateBiGrams(S2,obj.sentenceTrie2,false);
		
		obj.readCorpus(S1);
		
		obj.printBigramCount();
		System.out.println();
		
		obj.withoutSmoothing(S1,true);
		System.out.println();
		obj.withoutSmoothing(S2,false);
		System.out.println();		
		
		System.out.println("Sentence 1 probability without smoothing"+obj.probability(obj.biProbWthSmt1));
		System.out.println("Sentence 2 probability without smoothing"+obj.probability(obj.biProbWthSmt2));
		System.out.println();
		
		obj.addOneSmoothing(S1,true);
		System.out.println();
		obj.addOneSmoothing(S2,false);
		System.out.println();
		
		System.out.println("Sentence 1 probability after addOne smoothing"+obj.probability(obj.biProbaddOne1));
		System.out.println("Sentence 2 probability after addOne smoothing"+obj.probability(obj.biProbaddOne2));
		System.out.println();
		
		obj.goodTuringDiscounting(S1, true);
		System.out.println();
		obj.goodTuringDiscounting(S2, false);
		System.out.println();
		
		System.out.println("Sentence 1 probability after addOne smoothing"+obj.probability(obj.biProbGTuring1));
		System.out.println("Sentence 2 probability after addOne smoothing"+obj.probability(obj.biProbGTuring2));
	}
}
