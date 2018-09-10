package mapreduceBean;

public class MapReduceDetails {
	private String fileName;
	private int startFrom;
	private int endAt;
	
	public MapReduceDetails() {
		super();
	}
	
	public MapReduceDetails(String fileName, int startFrom, int endAt) {
		super();
		this.fileName = fileName;
		this.startFrom = startFrom;
		this.endAt = endAt;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(int startFrom) {
		this.startFrom = startFrom;
	}
	public int getEndAt() {
		return endAt;
	}
	public void setEndAt(int endAt) {
		this.endAt = endAt;
	}
}
