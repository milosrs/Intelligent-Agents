package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

@Stateful
public class MapService {
	
	private String textFilesPath;
	private String mapOutputPath;
	private String fileLocation;
	private int numberOfLines;
	
	@PostConstruct
	public void init() {
		String absolutePath = ResultPredictionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    textFilesPath = newPath + "/../largeTextFiles/";
	    mapOutputPath = newPath + "/../mapOutput/";
	    numberOfLines = 0;
	}
	
	public String createKeyValuePairs(String filename) throws URISyntaxException, IOException {
		File inputFile = new File(textFilesPath + filename);
		File outputFile = new File(mapOutputPath + filename);
		
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
				createKeyValuePairsForLine(bw, line);
			}
			
			bw.close();
			fos.close();
			br.close();
			
			fileLocation = mapOutputPath + "/" + filename;
		}
		
		return fileLocation;
	}

	private void createKeyValuePairsForLine(BufferedWriter bw, String line) throws IOException {
		String[] words = line.trim().split(" ");
		String writeToFile;
		
		for(String word : words) {
			word = word.replaceAll("[^A-Za-z0-9]", "");
			word = word.trim();
			
			if(word.length() > 0) {
				writeToFile=word + ", 1";
				bw.write(writeToFile);
				bw.newLine();
				numberOfLines++;
			}
		}
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(int numberOfLines) {
		this.numberOfLines = numberOfLines;
	}
}
