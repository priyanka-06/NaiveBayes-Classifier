import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class NaiveBayes {
	
	public static void main(String[] args) {
		ArrayList<ArrayList<String>> allTrainingFiles = new ArrayList<>();
		ArrayList<String> trainingLabel = new ArrayList<>();
		readAllFiles(args[0], allTrainingFiles, trainingLabel);
		
		ArrayList<ArrayList<String>> allTestingFiles = new ArrayList<>();
		ArrayList<String> testingLabel = new ArrayList<>();
		readAllFiles(args[1], allTestingFiles, testingLabel);
		
		//Probability of each class
		double[] probLabel = findProbLabel(allTrainingFiles);
		
		ArrayList<Integer> wordsInEachLabel = new ArrayList<>();
		
		Set<String> set = new HashSet<>();
		for(ArrayList<String> temp : allTrainingFiles){
			int count=0;
			for(String s: temp){
				StringTokenizer input = new StringTokenizer(s);
				while(input.hasMoreElements()){
					count++;
					set.add(input.nextToken());
				}
			}
			wordsInEachLabel.add(count);
		}
		
		int totalDistinctWords = set.size();
		
		ArrayList<Map<String,Integer>> mapList = new ArrayList<>();
		for(int i=0;i<allTrainingFiles.size();i++){
			Map<String, Integer> map = new HashMap<>();
			for(String s: allTrainingFiles.get(i)){
				StringTokenizer input = new StringTokenizer(s);
				while(input.hasMoreElements()){
					String s1=input.nextToken();
					if(map.containsKey(s1)){
						map.put(s1, map.get(s1)+1);
					}else{
						map.put(s1, 1);
					}
				}
			}
			mapList.add(map);
		}
		
		
		int correctPredictions=0, incorrectPredictions=0;
		for(int j=0;j<allTestingFiles.size();j++){
			for(String s : allTestingFiles.get(j)){
				double a[]=new double[probLabel.length];
				for(int i=0;i<probLabel.length;i++){
					a[i]=Math.log(probLabel[i]);
					
					StringTokenizer input = new StringTokenizer(s);
					while(input.hasMoreElements()){
						String s1=input.nextToken();
						int count;
						if(mapList.get(i).containsKey(s1)){
							count=mapList.get(i).get(s1)+1;
						}else{
							count=1;
						}
						a[i] = a[i]+Math.log(count*1.0/(totalDistinctWords+wordsInEachLabel.get(i)));
					}
				}
				
				double max=a[0];
				int index=0;
				
				for(int i=1;i<a.length;i++){
					if(max<a[i]){
						max=a[i];
						index=i;
					}
				}
				
				if(trainingLabel.get(index).equals(testingLabel.get(j))){
					correctPredictions++;
				}else{
					incorrectPredictions++;
				}
			}
		}
		
		//System.out.println("Correct Predictions : "+correctPredictions);
		//System.out.println("Incorrect Predictions : "+incorrectPredictions);
		System.out.println("Testing Accuracy : "+correctPredictions*100.0/(correctPredictions+incorrectPredictions));
	}
	
	private static double[] findProbLabel(ArrayList<ArrayList<String>> allTrainingFiles) {
		double[] result = new double[allTrainingFiles.size()];
		int sum=0;
		for(ArrayList<String> temp:allTrainingFiles){
			sum+=temp.size();
		}
		
		for(int i=0;i<result.length;i++){
			result[i]=allTrainingFiles.get(i).size()*1.0/sum;
		}
		
		return result;
	}

	public static void readAllFiles(String folderPath, ArrayList<ArrayList<String>> allFiles, ArrayList<String> trainingLabel){
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (!file.isFile()) {
		    	File folder1 = new File(folderPath+"\\"+file.getName());
				File[] listOfFiles1 = folder1.listFiles();
		    	
				ArrayList<String> list = new ArrayList<>();
				for(File file1 : listOfFiles1){
					if (file1.isFile()) {
				    	list.add(readExcelFromPath(folderPath+"\\"+file.getName()+"\\"+file1.getName()));
					}
				}
				allFiles.add(list);
				trainingLabel.add(file.getName());
		    }
		}
	}
	
	public static String readExcelFromPath(String path){
		FileReader fr;
		BufferedReader br;
		StringBuilder output=new StringBuilder();
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);  
			String s;  
			boolean flag=true;
			while((s = br.readLine()) != null) {
				if(flag){
					if(s.contains("Lines:")){
						flag=false;
					}
				}else{
					if(!s.matches("[\\s]*")){
						output.append(s);
					}
				}
			}
			fr.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} 

		return ignoreStopWords(output.toString());
	}
	
	@SuppressWarnings("resource")
	public static String ignoreStopWords(String s1){
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader("stopWords.txt");
			br = new BufferedReader(fr);  
			String s;
			while((s = br.readLine()) != null) {
				if(s1.contains(s)){
					s1=s1.replaceAll(s, "");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}
		
		return s1;
	}
	
}
