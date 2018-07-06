package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.FootballPredictions;

public class RegressionService {

	private double k;
	private double n;
	
	public FootballPredictions getPredictions() {
		
		FootballPredictions footballPredictions = null;
		
		String absolutePath = ResultPredictionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    String fullResourcePath = newPath + "/../csvData/football_predictions.csv";
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
			int[] matchRatings = new int[lines.size()];
			double[] homeWinPercentages = new double[lines.size()];
			double[] tiePercentages = new double[lines.size()];
			double[] awayWinPercentages = new double[lines.size()];
			
			for(int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] splits = line.split(",");
				System.out.println(splits);
				String matchRating = splits[0];
				String homeWinPercentage = splits[4].replace("%", "");
				String tiePercentage = splits[5].replace("%", "");
				String awayWinPercentage = splits[6].replace("%", "");
				
				boolean isCorrect = true;
				int matchRatingNum = 0;
				try {
					matchRatingNum = Integer.parseInt(matchRating);	
				} catch (Exception e) {
					isCorrect = false;
				}
				
				double homeWinPercentageNum = 0.0;
				try {
					homeWinPercentageNum = Double.parseDouble(homeWinPercentage);	
				} catch (Exception e) {
					isCorrect = false;
				}
				
				double tiePercentageNum = 0.0;
				try {
					tiePercentageNum = Double.parseDouble(tiePercentage);	
				} catch (Exception e) {
					isCorrect = false;
				}
				
				double awayWinPercentageNum = 0.0;
				try {
					awayWinPercentageNum = Double.parseDouble(awayWinPercentage);	
				} catch (Exception e) {
					isCorrect = false;
				}
				
				if(isCorrect) {				
					matchRatings[i-1] = matchRatingNum;
					homeWinPercentages[i-1] = homeWinPercentageNum;
					tiePercentages[i-1] = tiePercentageNum;
					awayWinPercentages[i-1] = awayWinPercentageNum;
				}
			}
			
			footballPredictions = new FootballPredictions(matchRatings,homeWinPercentages,tiePercentages,awayWinPercentages);
		}
		
		return footballPredictions;
	}
	
	
	 public void fit(int[] x, double[] y) {
         int l = x.length;
         double s1 = 0;
         double s2 = 0;
         double s3 = 0;
         double s4 = 0;

         for (int i = 0; i < l; i++)
         {
             s1 += x[i] * y[i];
             s2 += x[i];
             s3 += y[i];
             s4 += x[i] * x[i];
         }
         k = (l * s1 - s2 * s3) / (l * s4 - s2 * s2);
         n = (s3 - k * s2) / l;


	    }

     public double predict(int x)
     {   
         return k*x+n;
     }
	
	public double getK() {
		return k;
	}

	public double getN() {
		return n;
	}
}
