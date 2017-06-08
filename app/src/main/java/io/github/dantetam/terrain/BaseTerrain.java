package io.github.dantetam.terrain;

//Base class of all io.github.dantetam.terrain generation algorithms

import java.util.Random;

public abstract class BaseTerrain {

	public double[][] terrain;
	public Random random;

	public void seed(long seed)
	{
		random = null;
		random = new Random(seed); 
	}

	public abstract double[][] generate(double[] arguments);
	
}
