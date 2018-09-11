package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

@Stateful
public class MapService {
	
	private String textFilesPath;
	private String mapOutputPath;
	private String fileLocation;
	private int numberOfLines;
	private HashMap<String, Integer> counts;
	
	@PostConstruct
	public void init() {
		String absolutePath = ResultPredictionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    String newPath = absolutePath.substring(1);
	    textFilesPath = newPath + "/../largeTextFiles/";
	    mapOutputPath = newPath + "/../mapOutput/";
	    numberOfLines = 0;
	    counts = new HashMap<String, Integer>();
	}
	
	public void createKeyValuePairs(String filename, int mapperPosition, int totalMapperNumber) throws URISyntaxException, IOException {
		File inputFile = new File(textFilesPath + filename);
		File outputFile = new File(mapOutputPath + filename);
		
		if(!outputFile.exists()) {
			outputFile.mkdirs();
			outputFile.createNewFile();
		}
		
		if(inputFile.exists() && outputFile.exists()) {
			try(RandomAccessFile raf = new RandomAccessFile(inputFile, "r")) {
				
				String text = readFile(raf, mapperPosition, totalMapperNumber);
				String[] words = text.trim().split(" ");
				
				for(int i = 0; i < words.length; i++) {
					addToMap(words[i]);
				}
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private String readFile(RandomAccessFile raf, int mapperPosition, int totalMapperNumber) throws IOException {
		Long fileLengthInBytes = raf.length();
		Long nthOfFile = Math.floorDiv(fileLengthInBytes, totalMapperNumber) + 1;
		byte[] buffer = new byte[nthOfFile.intValue() + 10];
		Long seekToPosition = (long)0;
		
		if(mapperPosition > 0) {
			seekToPosition = nthOfFile * mapperPosition;
			raf.seek(seekToPosition);
			
			try {
				while(raf.readChar() != ' ') {
					//Move pointer to right
				}
			} catch(Exception e) {
				//EOF Exception
			}
			
			
			raf.seek(--seekToPosition);
		}
		
		try {
			raf.read(buffer, seekToPosition.intValue(), nthOfFile.intValue());
		} catch(Exception e) {
			raf.read(buffer, seekToPosition.intValue(), (int)(fileLengthInBytes - seekToPosition - 1));
		}
		
		
		String text = new String(buffer);
		
		try {
			boolean shouldLoop = text.charAt(text.length() - 1) != ' ' && Character.isLetter(text.charAt(text.length() - 1));
			
			while(text.charAt(text.length() - 1) != ' ' && shouldLoop) {
				Character c = (char)raf.read();
				shouldLoop = Character.isLetter(c);
				if(shouldLoop) {
					text += c;
				}
			}
		} catch(Exception e) {
			System.out.println("Stopping seek, probably EOF");
		}
		
		return text;
	}

	private void addToMap(String word) throws IOException {
		word = word.trim().replaceAll("[^A-Za-z0-9]", "");
		
		if(word.length() > 0 && word != null && !word.equals("") && !word.equals(" ")) {
			Integer count = counts.get(word);
			
			if(count == null) {
				count = 1;
			} else {
				count += 1;
			}
			
			counts.put(word, count);
			numberOfLines++;
		} else {
			System.out.println("Skipped space");
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
	
	public HashMap<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(HashMap<String, Integer> counts) {
		this.counts = counts;
	}
}
