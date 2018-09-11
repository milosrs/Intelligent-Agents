package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateful;

import beans.AID;
import mapreduceBean.MapReduceDetails;

@Stateful
public class ReduceService {

	private String mapPath;
	private String reducerPath;
	private MapReduceDetails details;
	private HashMap<String, Integer> counts;
	private List<AID> processedMappers;
	
	public ReduceService() {
		
	}
	
	public ReduceService(String fileName, MapReduceDetails details) {
		String absolutePath = ResultPredictionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    mapPath = newPath + "/../mapOutput/" + fileName;
	    reducerPath = newPath + "/../reducerOutput/" + fileName;
	    this.details = details;
	    counts = new HashMap<String, Integer>();
	}
	
	public void countOccurences() throws IOException {
		File inputFile = new File(mapPath + details.getFileName());
		File outputFile = new File(reducerPath + details.getFileName());
		
		if(!outputFile.exists()) {
			outputFile.mkdirs();
			outputFile.createNewFile();
		}
		
		if(inputFile.exists() && outputFile.exists()) {
			String line;
			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			while((line = br.readLine()) != null) {
				count(line);
			}
			
			bw.close();
			fos.close();
			br.close();
		}
	}

	private void count(String line) {
		line = line.split(",")[0].trim();
		if(counts.get(line) != null) {
			Integer count = counts.get(line);
			count += 1;
			counts.put(line, count);
		}
	}
	
	public boolean areAllMappersProcessed(List<AID> mappers) {
		boolean shouldReset =processedMappers.containsAll(mappers);
		
		if(shouldReset) {
			this.processedMappers = new ArrayList<AID>();
			this.counts = new HashMap<String, Integer>();
		}
		
		return shouldReset;
	}
}
