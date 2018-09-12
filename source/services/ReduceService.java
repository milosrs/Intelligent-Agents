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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.Stateful;

import beans.AID;
import beans.MapAgent;
import mapreduceBean.MapReduceDetails;

@Stateful
public class ReduceService {
	private HashMap<String, Integer> counts;
	private List<AID> processedMappers;
	
	public ReduceService() {
	    counts = new HashMap<String, Integer>();
	}
	
	public void countOccurences(HashMap<String, Integer> words, AID mapper) throws IOException {
		if(mapper.getType().getClass().equals(MapAgent.class)) {
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
			
			processedMappers.add(mapper);
		}
	}
	
	public boolean areAllMappersProcessed(List<AID> mappers) {
		List<AID> mappersList = new ArrayList<AID>();
		
		for(int i = 0; i < mappers.size(); i++) {
			if(mappers.get(i).getType().getName().equals("MapAgent")) {
				mappersList.add(mappers.get(i));
			}
		}
		
		return processedMappers.containsAll(mappers);
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
}
