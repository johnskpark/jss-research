package SmallStatistics;

import java.util.ArrayList;
import java.util.List;

public class AllStatistics extends SmallStatistics {

	private List<Double> values = new ArrayList<Double>();

	public AllStatistics() {
		super();
	}

	@Override
	public void add(double value) {
		super.add(value);
		values.add(value);
	}

	public List<Double> getValues() {
		return values;
	}

}
