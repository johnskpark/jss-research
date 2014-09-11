package jss;


public abstract class ProblemInstance {

	// TODO the getters for the problem instance's job and machine information

	// So what are the functionalities that a problem instance needs to have?
	// It needs to know:
	// - Its jobs and their properties
	// - Its machines and their properties
	// - When to show the individuals in GP the jobs (for static and dynamic)
	// - How many jobs that has been processed
	// - When to terminate.

	// So basically this is sort of the control centre for individual problems, with the GP
	// instances complementing the problem instance.

	// On the other hand, JSS Problem should know how well each individual did overall.

	// TODO this should be on the outside, not the inside.
}
