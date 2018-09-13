package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateful;

import beans.AID;

@Stateful
public class ReduceService {
	private HashMap<String, Integer> counts;
	private List<AID> processedMappers;
	
	public ReduceService() {
		processedMappers = new ArrayList<AID>();
	    counts = new HashMap<String, Integer>();
	}
	
	public void test(AID mapper) {
		if(!processedMappers.contains(mapper)) {
			processedMappers.add(mapper);
		}
	}
	
	public void countOccurences(HashMap<String, Integer> words, AID mapper) throws IOException {
		if(mapper.getType().getName().equals("MapAgent")) {
			for(String key : words.keySet()) {
				Integer total;
				Integer mapOccurrences = words.get(key);
				Integer occurrences = counts.get(key);
				
				if(mapOccurrences != null && occurrences != null) {
					total = mapOccurrences + occurrences;
				} else {
					total = mapOccurrences;
				}
				
				counts.put(key, total);
			}
			
			if(!processedMappers.contains(mapper)) {
				processedMappers.add(mapper);
			}
		}
	}
	
	public boolean areAllMappersProcessed(List<AID> mappers) {
		boolean containsAll = false;
		int count = 0;
		
		for(int i = 0; i < mappers.size(); i++) {
			for(AID pm : processedMappers) {
				if(mappers.get(i).getType().getName().equals("MapAgent") && pm.getName().equals(mappers.get(i).getName())) {
					count++;
				}
			}
		}
		
		return processedMappers.size() == count;
	}
	
	public void resetAll() {
		counts = new HashMap<String, Integer>();
		processedMappers = new ArrayList<AID>();
	}

	public void writeValues() {
		for(String key : counts.keySet()) {
			Integer count = counts.get(key);
			System.out.println(key + ": " + count);
		}
	}

	public String createHugeString() {
		String ret = "";
		
		for(String key : counts.keySet()) {
			ret += key + " : " + Integer.toString(counts.get(key)) + " ";
		}
		
		return ret;
	}
}
