import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class SpellChecker {

	HashMap<Character, LinkedList<String>>charMap=new HashMap<>();
	HashMap<String, Integer>corpusMap = new HashMap<String,Integer>();
	String query;
	File corpus;
	HashSet<String>vocabulary=new HashSet<String>();
	
	Trie trie;
	BigramTree bigramTree[][];
	class Trie{
		int count;
		Trie charRef[];
	}
	
	class BigramTree{
		LinkedList<String>list;
		public BigramTree(String word){
			list=new LinkedList<String>();
			list.addFirst(word);
		}
	}
	
	public void readQuery(){
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			query=br.readLine();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	public void insertInCharMap(String word){
		
		char c = word.charAt(0);
		if(charMap.containsKey(c)){
			charMap.get(c).addFirst(word);
		}
		else{
			LinkedList<String>list = new LinkedList<String>();
			list.addFirst(word);
			charMap.put(c, list);
		}
	}
	
	
	public void insertInBigramTree(int i,int j,String word){
		
		if(bigramTree[i][j]==null){
			bigramTree[i][j] = new BigramTree(word);
		}
		else{
			bigramTree[i][j].list.addFirst(word);
		}
	}
	
	public void loadCorpus(){
		
		String line;
		corpus = new File("Corpus.txt");
		trie = new Trie();
		bigramTree = new BigramTree[256][256];
		try{
			
			FileReader reader = new FileReader(corpus);
			BufferedReader br = new BufferedReader(reader);
			String words[];
			while((line=br.readLine())!=null){
				words = line.split(" ");
				String word;
				for(int i=0;i<words.length;i++){
					word=words[i];
					char c;
					if(!vocabulary.contains(word)){
						vocabulary.add(word);
						if(word.length()>0){
							insertInCharMap(word);
							for(int j=0;j<word.length();j++){
								c=word.charAt(j);
								if(j<word.length()-1){
									insertInBigramTree(c, word.charAt(j+1), word);
								}
							}
						}
					}
					int count;
					if(corpusMap.containsKey(word)){
						count = corpusMap.get(word);
						count+=1;
						corpusMap.put(word, count);
					}
					else{
						corpusMap.put(word, 1);
					}
				}
			}
		}
		catch(FileNotFoundException fileNotFoundException){
			fileNotFoundException.printStackTrace();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	int min(int x, int y, int z) {
	    return Math.min(Math.min(x, y), z);
	}
	
	public int editDistance(String s1,String s2){
		
		int m = s1.length();
		int n = s2.length();
		m++;
		n++;
		int dp[][]=new int[m][n];
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				if(i==0){
					dp[i][j]=j;
				}
				else if(j==0){
					dp[i][j]=i;
				}
				else if( s1.charAt(i-1)==s2.charAt(j-1) ){
					dp[i][j]=dp[i-1][j-1];
				}
				else{
					dp[i][j] = 1 + min(dp[i][j-1],  // Insert
                            dp[i-1][j],  // Remove
                            dp[i-1][j-1]);// Replace
				}
			}
		}
		return dp[m-1][n-1];
	}
	
	public String process(String word){

		// Generate rotations of word and for each rotaion comput the edit distance of the word from the corpus
		String correctWord=word,targetWord;
		int min =Integer.MAX_VALUE;
		int maxProb=0;
		//for(int i=0;i<word.length();i++){
              
			  //rotatedWord=word.substring(i, word.length())+word.substring(0, i);
              LinkedList<String>list =charMap.get(word.charAt(0));
              if(list==null){
            	  return word;
              }
              
              for(int j=0;j<list.size();j++){
            	  
            	  targetWord=list.get(j);
            	  if(targetWord.length()<=1){
            		  continue;
            	  }
            	  int val=editDistance(word, targetWord);
            	  int prob=corpusMap.get(targetWord);
            	  if(val<min){
            		  correctWord=targetWord;
            		  maxProb=prob;
            		  min=val;
            	  }
            	  else if(val==min && prob>maxProb){
            		  maxProb=prob;
            		  correctWord=targetWord;
            		  min=val;
            	  }
              }
		//}
		return correctWord;
	}
	
	public int getProbability(String targetWord){
		
		
		Trie curr=trie;
		int i=0;
		int c;
		while(curr!=null && i<targetWord.length()){
			c=targetWord.charAt(i);
			if(curr.charRef==null){
				break;
			}
			if(curr.charRef[targetWord.charAt(i)]==null){
				break;
			}
			curr=curr.charRef[targetWord.charAt(i)];
			i++;
		}
		if(curr!=null && i==targetWord.length()){
			
			return curr.count;
		}
		return 0;
	}
	
	public void checkSpelling(){
		
		String words[] = query.split(" ");
		String word;
        
		// For each word
		System.out.println("Correct spelling using edit distance without heuristics");
		for(int i=0;i<words.length;i++){
			word=words[i];
			System.out.println(word+" "+process(word));
		}
	}
	
	public float getJaccardCoefficient(String targetWord,String queryWord){
		
		int count=0;
		for(int i=0;i<queryWord.length()-1;i++){
			if(targetWord.contains(queryWord.substring(i, i+1).toLowerCase())){
				count++;
			}
		}
		int A=targetWord.length()-1;
		int B=queryWord.length()-1;
		int C=count;
		float jc = (float)count/(A+B-C);
		
		return jc;
	}
	
	public String processNGramWord(String word){
		
		int ci,cj;
		int min=Integer.MAX_VALUE,val;
		String correctWord=word;
		LinkedList<String>list;
		HashSet<String>mainSet = new HashSet<String>();
		
		
		for(int i=0;i<word.length()-1;i++){
			
			ci=word.charAt(i);
			cj=word.charAt(i+1);
			if(bigramTree[ci][cj]==null){
				continue;
			}
			list=bigramTree[ci][cj].list;
			HashSet<String>newSet = new HashSet<String>();
			if(list!=null){
				
				String matchedWord;
				for(int j=0;j<list.size();j++){
					
					matchedWord=list.get(j);
					if(!newSet.contains(matchedWord)){
						newSet.add(matchedWord);
						if(mainSet.contains(matchedWord)){
							val=editDistance(matchedWord, word);
							if(val<=min){
								min=val;
								correctWord=matchedWord;
							}
						}
				}
			}
			mainSet.addAll(newSet);
		}
		}
		return correctWord;
	}
	
	public void kGramIndex(){
		
		String words[] = query.split(" ");
		String word;
        
		// For each word
		System.out.println("Correct spelling of words with n gram as heuristics");
		for(int i=0;i<words.length;i++){
			word=words[i];
			System.out.println(word+" "+processNGramWord(word));
		}
	}
	
	public static void main(String[] args) {
		
		SpellChecker spellChecker = new SpellChecker();
		
		System.out.println("Reading corpus...");
		spellChecker.loadCorpus();
		System.out.println("Finshed Reading.");
		
		System.out.println("Enter input");
		spellChecker.readQuery();
		System.out.println("Spell check in progress...");
		spellChecker.checkSpelling();

		// Approach 2
		spellChecker.kGramIndex();

		System.out.println("Completed");
	}
}