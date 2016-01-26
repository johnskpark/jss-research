package app.evaluation.util;

import java.io.IOException;

public class TrainPerformance {

	public TrainPerformance() {
		// TODO
	}
	
	public void readLogFile(String logName) throws IOException {
		// TODO 
	}
	
	public static void main(String[] args) {
		TrainPerformance trainPerf = new TrainPerformance();
		
		try {
			trainPerf.readLogFile(args[0]);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
}
