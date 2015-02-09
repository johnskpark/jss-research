package app.evolution.test;

public class StatCollector {

	private String name;
	private double sum = 0.0;
	private double sumSq = 0.0;
	private int count = 0;
	private double max;
	private double min;
	protected double lastVal;

	public StatCollector() {
		this.name = null;
	}

	public StatCollector(String name) {
		this.name = name;
	}

	public void add(double value) {
		lastVal = value;

		count++;
		if (value < min) {
			min = value;
		}
		if (value > max) {
			max = value;
		}

		sum += value;
		sumSq += value * value;
	}

	public double mean() {
		return sum / count;
	}

	public double sum()	{
		return sum;
	}

	public double sumSq() {
		return sumSq;
	}

	public int count() {
		return count;
	}

	public double min() {
		return min;
	}

	public double max() {
		return max;
	}

	public void clear() {
		sum = 0.0;
		sumSq = 0.0;
		count = 0;
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
	}

	public String getName() {
		return name;
	}

}
