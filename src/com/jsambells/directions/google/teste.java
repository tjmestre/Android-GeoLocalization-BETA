package com.jsambells.directions.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class teste {

	private static List<DirectionsAPIStep> instructions;
	

	
	public void setInstructions(List<DirectionsAPIStep> lista)
	{
		 instructions = lista;
	
	}
	

	public synchronized static List<DirectionsAPIStep> getInstructions() {
		if (instructions == null)
			instructions = new ArrayList<DirectionsAPIStep>();
			
		return instructions;
	}
	
}
