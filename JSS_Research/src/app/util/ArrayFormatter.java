package app.util;

public class ArrayFormatter {

	private String openingBrace;
	private String closingBrace;

	public ArrayFormatter(String opening, String closing) {
		openingBrace = opening;
		closingBrace = closing;
	}

	public String formatData(double... values) {
		StringBuilder builder = new StringBuilder();
		builder.append(openingBrace);
		if (values.length > 0) {
			builder.append(values[0]);
		}
		for (int i = 1; i < values.length; i++) {
			builder.append("," + values[i]);
		}
		builder.append(closingBrace);

		return builder.toString();
	}
}
