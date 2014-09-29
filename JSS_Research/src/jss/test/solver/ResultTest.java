package jss.test.solver;

import jss.IProblemInstance;
import jss.evolution.sample.BasicResult;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

public class ResultTest {

	Mockery context = new JUnit4Mockery();

	IProblemInstance problem = context.mock(IProblemInstance.class);

	@Test
	public void basicResult_GetterSetter() {
		BasicResult result = new BasicResult(problem);

		// TODO
	}
}
