package jss.test;

import jss.IProblemInstance;
import jss.problem.Result;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.Assert;

public class ResultTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private IProblemInstance problem = context.mock(IProblemInstance.class);

	@Test
	public void basicResult_GetterSetter() {
		Result result = new Result(problem);

		Assert.assertTrue(result.getActions().isEmpty());
		Assert.assertEquals(0.0, result.getMakespan(), EPSILON);
		Assert.assertEquals(0.0, result.getTWT(), EPSILON);

		// TODO more shit here.
	}
}
