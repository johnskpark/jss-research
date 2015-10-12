package ec.multilevel_new;

import ec.DefaultsForm;
import ec.util.Parameter;

public final class MLSDefaults implements DefaultsForm {

	public static final String P_MLS = "mls";

	public static final Parameter base() {
		return new Parameter(P_MLS);
	}

}
