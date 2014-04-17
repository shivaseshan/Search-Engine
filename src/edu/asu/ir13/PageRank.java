package edu.asu.ir13;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class PageRank {
	private static final double  EPSILLON = 0.001, probabilityC = 0.8;
	private static int[] links = null, citations = null ;
	private static double[] pageRank = null;
	
	/*
	 * Construct the pageRank vector through power iteration
	 */
	public static Map<Integer,Double> buildPageRank(String query, String WValue) throws CorruptIndexException, IOException
	{
		double probabilityW = Double.parseDouble(WValue);
		IndexReader r = IndexReader.open(FSDirectory.open(new File("D:\\Eclipse Java EE Developer\\Information Reterival\\IRWebService\\WebContent\\index")));
		LinkAnalysis l = Utilities.getLinkAnalysisObject();
		
		double[] RPrev = new double[r.maxDoc()];
		double[] RNext = null;
		double[] epsillonMatrix = null;
		List<Integer> sinkNodeList = new ArrayList<>();
		double[] documentIDs = new double[r.maxDoc()];
		double[] resetMatrix = new double[r.maxDoc()];
		Map<Integer, Double> results;
		Map<Integer, Double> finalResults = new HashMap<>();
		boolean stopPageRankIteration = false;
		int countTrue = 0;
		
		Arrays.fill(RPrev, 1.0/r.maxDoc());							// Initialize PageRank vector to 1	
		Arrays.fill(resetMatrix, (1-probabilityC)*1/r.maxDoc());	// Initialize Reset Matrix to (1-c)*1/Max doc size
		for (int i = 0; i < documentIDs.length; i++)				// Initialize LinksArray to get a list of sink nodes
		{
			links = l.getLinks(i);
			if (links.length == 0)
				sinkNodeList.add(i);
		}
		
		int countPowerIteration = 1;
		/* Pre-computing the PageRank matrix*/	
		if (pageRank == null)
		{
			while(!stopPageRankIteration)
			{
				// resetting the countTrue to 0
				countTrue = 0;
				
				RNext = new double[documentIDs.length];
				Set<Integer> citationsSet = new HashSet<>();
				
				for (int i = 0; i < documentIDs.length; i++)
				{
					citations = l.getCitations(i);
					for (int citation : citations)
						citationsSet.add(citation);
						
					for (Integer citation : citations)
					{
						links = l.getLinks(citation);
						documentIDs[citation] = probabilityC*1/links.length;
					}
					
					for (Integer sinkNode : sinkNodeList)
						documentIDs[sinkNode] = probabilityC*1/documentIDs.length;		// when no links, then removing sink node by adding Z
					
					documentIDs = Utilities.matrixAdd(documentIDs, resetMatrix);
					RNext[i] = Utilities.matrixMult(documentIDs, RPrev);
					
					Arrays.fill(documentIDs, 0);
				}
				
				// Subtracting the PageRank vector - old and new 
				epsillonMatrix = Utilities.matrixSubtract(RPrev, RNext);
				
				System.out.println("Power Iteration " + countPowerIteration++);
				
				// Checking for stopping condition
				for (int i = 0; i < epsillonMatrix.length; i++)
				{	
					if (!(epsillonMatrix[i] >= EPSILLON))
						countTrue += 1;
					else
						break;
				}
				if (countTrue == epsillonMatrix.length)
					stopPageRankIteration = true;
				
				RPrev = RNext;
			}
		}
		
		if (pageRank == null)
			pageRank = pageRankScale(RNext);
		
		// Get the results from the TF-IDF vector similarity measure
		results = Ranking.getResults(query);
		
		for (Map.Entry<Integer, Double>entry : results.entrySet())
			finalResults.put(entry.getKey(), probabilityW*pageRank[entry.getKey()]+(1-probabilityW)*entry.getValue()); 
		
		// Reverse sorting the Map and getting top 10 authority nodes along with values
		finalResults = Utilities.sortByComparator(finalResults,10);
		
		//Map<Integer,String> resultLinks = Utilities.getLinks(new ArrayList<Integer>(finalResults.keySet()));
		
		return finalResults;
	}
	
	/*
	 * Scale the pageRank matrix in the range of [0-1]
	 */
	private static double[] pageRankScale(double[] pageRank)
	{
		double[] pageRankSorted = new double[pageRank.length];
		System.arraycopy(pageRank, 0, pageRankSorted, 0, pageRank.length);
		Arrays.sort(pageRankSorted);
		double maxValue = pageRankSorted[pageRankSorted.length-1];
		double minValue = pageRankSorted[0];
		
		for (int i = 0; i < pageRank.length; i++)
			pageRank[i] = ((pageRank[i]-minValue)/(maxValue-minValue));
		
		return pageRank;
	}
}
