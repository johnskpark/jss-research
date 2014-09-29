package jss.test;

import jss.IProblemInstance;
import jss.problem.Result;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

public class ResultTest {

	Mockery context = new JUnit4Mockery();

	IProblemInstance problem = context.mock(IProblemInstance.class);

	@Test
	public void basicResult_GetterSetter() {
		Result result = new Result(problem);

		// TODO
	}
}
