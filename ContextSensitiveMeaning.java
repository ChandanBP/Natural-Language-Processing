import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ContextSensitiveMeaning {

	String words[];
	HashMap<String, ArrayList<HashSet<String>>>map=new HashMap<>();
	HashMap<String, ArrayList<String>>mapOfStrings=new HashMap<>();
	HashMap<String, Integer>phraseSelected = new HashMap<>();
	
	
	public void originalLesk(String phrase){
	
	
		words = phrase.split(" ");
		String key;
		for(int i=0;i<words.length;i++){
			
			key = words[i];
			ArrayList<HashSet<String>>listOfSets1 = map.get(key);
			HashSet<String>set1,set2;
			
			for(int j=0;j<listOfSets1.size();j++){
				
				set1 = listOfSets1.get(j);
				int maxJ=0;
				int count=0;
				for(int k=0;k<words.length;k++){
					
					if(k==i)continue;
					
					ArrayList<HashSet<String>>listOfSets2=new ArrayList<HashSet<String>>();
					
					if(k<i){
						int selectedJ = phraseSelected.get(words[k]);
						HashSet<String>s = map.get(words[k]).get(selectedJ);
						listOfSets2.add(s);
					}
					else{
						listOfSets2 = map.get(words[k]);
					}
					
					for(int l=0;l<listOfSets2.size();l++){
						set2 = listOfSets2.get(l);
						HashSet<String>tempSet = new HashSet<String>(set2);
						tempSet.retainAll(set1);
						if(tempSet.size()>count){
							count=set2.size();
							maxJ=j;
						}
					}
				}
				phraseSelected.put(key, maxJ);
			}
		}
	}
	
	public void readWordNet(){
		
		String line;
		String input[];
		try{
		
			FileReader fr =new FileReader("wordnet.txt");
		    BufferedReader br = new BufferedReader(fr);
		
			while((line=br.readLine())!=null){
				
				input = line.split("=");
				String key = input[0];
				String meanings[] = input[1].split(",");
				
				ArrayList<HashSet<String>>listOfSets = new ArrayList<HashSet<String>>();
				ArrayList<String>list = new ArrayList<String>();
				for(int i=0;i<meanings.length;i++){
					
					String meaning = meanings[i];
					list.add(meaning);
					String wordsInMeaning[] = meaning.split(" ");
					HashSet<String>set = new HashSet<String>();
					for(int j=0;j<wordsInMeaning.length;j++){
						set.add(wordsInMeaning[j]);
					}
					listOfSets.add(set);
				}
				map.put(key, listOfSets);
				mapOfStrings.put(key, list);
			}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	public void printSelected(String phrase){
		
		System.out.println("The meanings of sentences chosen are");
		for(int i=0;i<words.length;i++){
			System.out.println(words[i]+"= "+mapOfStrings.get(words[i]).get(phraseSelected.get(words[i])));
		}
	}
	
	public static void main(String[] args) {
		
		String phrase = "Time flies like an arrow";
		ContextSensitiveMeaning obj = new ContextSensitiveMeaning();
		obj.readWordNet();
		obj.originalLesk(phrase);
		obj.printSelected(phrase);
	}
}
