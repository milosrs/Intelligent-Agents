package beans;

public class FootballPredictions {

	private int[] matchRatings;
	private double[] homeWinPercentage;
	private double[] tiePercentage;
	private double[] awayWinPercentage;
	
	public FootballPredictions(int[] matchRatings, double[] homeWinPercentage, double[] tiePercentage,
			double[] awayWinPercentage) {
		super();
		this.matchRatings = matchRatings;
		this.homeWinPercentage = homeWinPercentage;
		this.tiePercentage = tiePercentage;
		this.awayWinPercentage = awayWinPercentage;
	}

	public int[] getMatchRatings() {
		return matchRatings;
	}

	public void setMatchRatings(int[] matchRatings) {
		this.matchRatings = matchRatings;
	}

	public double[] getHomeWinPercentage() {
		return homeWinPercentage;
	}

	public void setHomeWinPercentage(double[] homeWinPercentage) {
		this.homeWinPercentage = homeWinPercentage;
	}

	public double[] getTiePercentage() {
		return tiePercentage;
	}

	public void setTiePercentage(double[] tiePercentage) {
		this.tiePercentage = tiePercentage;
	}

	public double[] getAwayWinPercentage() {
		return awayWinPercentage;
	}

	public void setAwayWinPercentage(double[] awayWinPercentage) {
		this.awayWinPercentage = awayWinPercentage;
	}
}
