package beans;

import java.io.Serializable;

public class FootballResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String homeCountry;
	
	private String awayCountry;
	
	private int homeResult;
	
	private int awayResult;

	public FootballResult(String homeCountry, String awayCountry, int homeResult, int awayResult) {
		super();
		this.homeCountry = homeCountry;
		this.awayCountry = awayCountry;
		this.homeResult = homeResult;
		this.awayResult = awayResult;
	}

	public String getHomeCountry() {
		return homeCountry;
	}

	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}

	public String getAwayCountry() {
		return awayCountry;
	}

	public void setAwayCountry(String awayCountry) {
		this.awayCountry = awayCountry;
	}

	public int getHomeResult() {
		return homeResult;
	}

	public void setHomeResult(int homeResult) {
		this.homeResult = homeResult;
	}

	public int getAwayResult() {
		return awayResult;
	}

	public void setAwayResult(int awayResult) {
		this.awayResult = awayResult;
	}		
}
