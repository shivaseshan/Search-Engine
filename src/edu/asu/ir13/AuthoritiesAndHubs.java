package edu.asu.ir13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;

public class AuthoritiesAndHubs {

	private static final double  EPSILLON = 0.0001;
	private static int[] links = null, citations = null;
	
	/*
	 * Growing the top k results by getting the links and citations for it and making the base set.
	 */
	private static Set<Integer> getBaseSet(Map<Integer,Double> results)
	{
		LinkAnalysis l = Utilities.getLinkAnalysisObject();
		Set<Integer> rootSet = new LinkedHashSet<>(results.keySet());
		Set<Integer> baseSet = new LinkedHashSet<>();
		
		// Growing the top k results by getting the links and citations for it and making the base set.
		for (int i : rootSet)
		{
			citations = l.getCitations(i);
			for (int citation : citations)
				baseSet.add(citation);
			
			links = l.getLinks(i); 
			for (int link : links)
				baseSet.add(link);
			
			baseSet.add(i);
		}
		System.out.println("root set size : " + results.size());
		
		return baseSet;
	}
	
	/*
	 * Building the adjacency matrix
	 */
	private static int[][] getAdjacencyMatrix(Set<Integer> baseSet) throws CorruptIndexException, IOException
	{
		LinkAnalysis l = Utilities.getLinkAnalysisObject();
	
		int i = 0, row = 0, column = 0;
		int MATRIXSIZE = baseSet.size();
		int[][] adjacencyMatrix = new int [MATRIXSIZE][MATRIXSIZE];
		boolean found = false;
		Map<Integer,Integer> baseSetMap = new LinkedHashMap<>();
		
		for (int node : baseSet)
			baseSetMap.put(node, i++);
		
		
		for(Integer node : baseSetMap.keySet())
		{
			links = l.getLinks(node);
			
			for (Integer link : links)
			{
				if (baseSetMap.containsKey(link))
					adjacencyMatrix[baseSetMap.get(node)][baseSetMap.get(link)] = 1;
			}
		}
		/*for (int node : baseSet)
		{
			links = l.getLinks(node);
			column = 0;
			for (int b : baseSet)
			{
				for (int j = 0; j < links.length; j++)
				{
					if (links[j] == b)
					{
						found = true;
						break;
					}
				}
				if (found)
				{
					adjacencyMatrix[row][column] = 1;
					found = false;
				}
				column++;
			}
			row++;
		}*/
		
		System.out.println("Base Set Size : " + MATRIXSIZE);
		
		return adjacencyMatrix;
	}
	
	
	/* 
	 * Authorities computation 
	 */
	public static Map<Integer,Double>[] authoritiesAndHubs(Map<Integer,Double> results) throws CorruptIndexException, IOException 
	{
		long start = System.nanoTime();
		Set<Integer> baseSet = getBaseSet(results);
		System.out.println(System.nanoTime()-start + " time taken to compute base set");		
		
		int MATRIXSIZE = baseSet.size();
		
		start = System.nanoTime();
		int [][] adjacencyMatrix = getAdjacencyMatrix(baseSet);
		System.out.println(System.nanoTime()-start + " time taken to compute adjacency matrix");		
		
		int [][] multipledMatrixAuthority = null;
		int [][] multipledMatrixHub = null;
		double[] epsillonMatrix = null;
		double[] authorities = null;
		double[] authoritiesPrev = new double [MATRIXSIZE];
		double[] authoritiesNext = null;
		double[] hubs = null;
		double[] hubsPrev = new double [MATRIXSIZE];
		double[] hubsNext = null;
		
		boolean stopAuthorityIteration = false;
		boolean stopHubIteration = false;
		
		int count = 0, countTrue = 0;
		
		Map<Integer,Double>[] returnAuthorityHub = new LinkedHashMap[2];
		
		Arrays.fill(authoritiesPrev, 1.0);	// Initialize the authorities vector to 1
		Arrays.fill(hubsPrev, 1.0);			// Initialize the hubs vector to 1
		
		start = System.nanoTime();
		int[][] adjacencyMatrixTranspose = Utilities.matrixTranspose(adjacencyMatrix, MATRIXSIZE);
		System.out.println(System.nanoTime()-start + " time taken to compute transpose");

		start = System.nanoTime();
		// Multiplying the adjacency and it's transpose matrix
		multipledMatrixAuthority = Utilities.matrixMult(adjacencyMatrixTranspose,adjacencyMatrix);	// A'A
		
		// Multiplying the adjacency and it's transpose matrix
		multipledMatrixHub = Utilities.matrixMult(adjacencyMatrix,adjacencyMatrixTranspose);		// AA'
		System.out.println(System.nanoTime()-start + " time taken to compute matrix multiplication");

		start = System.nanoTime();
		// Power Iteration to compute authority matrix
		while (!stopAuthorityIteration)
		{
			// resetting the countTrue to 0
			countTrue = 0;
			
			authorities = Utilities.matrixMult(multipledMatrixAuthority, authoritiesPrev); 
		
			// Normalizing authorities matrix
			authoritiesNext = normalizeMatrix(authorities);
			
			// Subtracting authority vector - the new and old 
			epsillonMatrix = Utilities.matrixSubtract(authoritiesPrev, authoritiesNext);

			// Checking for stopping condition
			for (int i = 0; i < epsillonMatrix.length; i++)
			{	
				if (!(epsillonMatrix[i] >= EPSILLON))
					countTrue += 1;
				else
					break;
			}
			if (countTrue == epsillonMatrix.length)
				stopAuthorityIteration = true;
			
			authoritiesPrev = authoritiesNext;
		}
		System.out.println(System.nanoTime()-start + " time taken to authorities to converge");

		/* Making map by combining nodes and authorities values */
		Map<Integer, Double> authorityMap = new HashMap<>();
		count = 0;
		for (int i : baseSet)
		{
			authorityMap.put(i, authoritiesNext[count]);
			count++;
		}
		
		// Reverse sorting the Map and getting top 10 authority nodes along with values
		authorityMap = Utilities.sortByComparator(authorityMap,10);
		returnAuthorityHub[0] = authorityMap;
//		Map<Integer,String> resultLinks = Utilities.getLinks(new ArrayList<Integer>(authorityMap.keySet()));
				
		
		start = System.nanoTime();
		// Power Iteration to compute Hub matrix
		while (!stopHubIteration)
		{
			// resetting the countTrue to 0
			countTrue = 0;
			
			hubs = Utilities.matrixMult(multipledMatrixHub, hubsPrev); 
			
			// Normalizing hubs matrix
			hubsNext = normalizeMatrix(hubs);
			
			// Subtracting hubs matrix - the new and old one 
			epsillonMatrix = Utilities.matrixSubtract(hubsPrev, hubsNext);
			
			for (int i = 0; i < epsillonMatrix.length; i++)
			{	
				if (!(epsillonMatrix[i] >= EPSILLON))
					countTrue += 1;
				else
					break;
			}
			if (countTrue == epsillonMatrix.length)
				stopHubIteration = true;
			
			hubsPrev = hubsNext;
		}
		System.out.println(System.nanoTime()-start + " time taken to hubs to converge");

		/* Making map by combining nodes and hub values */
		Map<Integer, Double> hubMap = new HashMap<>();
		count = 0;
		for (int i : baseSet)
		{
			hubMap.put(i, hubsNext[count]);
			count++;
		}
		// Reverse sorting the Map and getting top 10 hub nodes along with values
		hubMap = Utilities.sortByComparator(hubMap,10);
		returnAuthorityHub[1] = hubMap;
//		Map<Integer,String> resultLinks = Utilities.getLinks(new ArrayList<Integer>(hubMap.keySet()));
		
		return returnAuthorityHub;
	}
	
	/*
	 *  Normalizing authorities matrix
	 */
	private static double[] normalizeMatrix(double mat[])
	{
		double[] matToReturn = new double[mat.length];
		int sumSquare = 0;
		double sumSquareRoot = 0;
		for (int i = 0; i < mat.length; i++)
			sumSquare += Math.pow(mat[i], 2);
		
		sumSquareRoot = Math.sqrt(sumSquare);
		
		for (int i = 0; i < mat.length; i++)
			matToReturn[i] = mat[i]/sumSquareRoot;
		
		return matToReturn;
	}
}