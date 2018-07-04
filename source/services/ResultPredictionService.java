package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;

import beans.FootballResult;

@Stateful
public class ResultPredictionService {

	private List<FootballResult> results;
	
	@PostConstruct
	public void init() {
		results = new ArrayList<FootballResult>();
	}
	
	public List<FootballResult> readResults() {
		
		ArrayList<FootballResult> retList = new ArrayList<FootballResult>();
		
		String absolutePath = ResultPredictionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    String fullResourcePath = newPath + "/../csvData/football_results.csv";
		File file = new File(fullResourcePath);
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			FileReader nodesData = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(nodesData);
			String line;
			while((line = bufferedReader.readLine()) != null)
				lines.add(line);
			bufferedReader.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
		if(lines.size() == 0)
			System.out.println("ERROR WHLE READING THE .csv DATA!");
		else {
			for(int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] splits = line.split(",");
				String homeTeam = splits[0];
				String awayTeam = splits[1];
				String homeScore = splits[2];
				String awayScore = splits[3];
				
				boolean isCorrectScore = true;
				int hScore = 0;
				try {
					hScore = Integer.parseInt(homeScore);	
				} catch (Exception e) {
					isCorrectScore = false;
				}
				
				int aScore = 0;
				try {
					aScore = Integer.parseInt(awayScore);	
				} catch (Exception e) {
					isCorrectScore = false;
				}
				
				if(isCorrectScore) {
					FootballResult result = new FootballResult(homeTeam, awayTeam, hScore, aScore);
					retList.add(result);
				}
			}
		}
		
		this.results = retList;
		
		return retList;
	}
	
	public FootballResult predictResult(String homeTeam, String awayTeam) {
		
		FootballResult retObj = null;
		
		List<FootballResult> filteredResults = this.results.stream()
				.filter(o -> o.getHomeCountry().equalsIgnoreCase(homeTeam)
					&& o.getAwayCountry().equalsIgnoreCase(awayTeam))
						.collect(Collectors.toList());
		
		if(!filteredResults.isEmpty()) {
			retObj = new FootballResult(homeTeam, awayTeam, 0, 0);
			
		}
		
		return retObj;
	}
	
	public FootballResult doRegression(String homeTeam, String awayTeam, List<FootballResult> myResults) {
		
		FootballResult retObj = new FootballResult(homeTeam, awayTeam, 0, 0);
		
		for (int i = 0; i < myResults.size(); i++) {
			FootballResult res = myResults.get(i);
		}
		
		return retObj;
	}
}
