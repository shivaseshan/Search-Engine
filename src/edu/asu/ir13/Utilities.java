package edu.asu.ir13;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class Utilities {
	
	/*
	 * Returning the object of LinkAnalysis
	 */
	public static LinkAnalysis getLinkAnalysisObject()
	{
		LinkAnalysis.numDocs = 25054;
		LinkAnalysis linkAnalysis = null;
		
		if (linkAnalysis == null)
			linkAnalysis = new LinkAnalysis();
		
		return linkAnalysis;
	}
	
	/* 
	 * Computing transpose of 2-Dimensional matrix 
	 */
	public static int[][] matrixTranspose(int[][] mat, int matrixSize)
	{
		int[][] matrixTranspose = new int [matrixSize][matrixSize];
		for (int row = 0 ; row < mat.length; row++)
			for (int column = 0; column < mat.length; column++)
				matrixTranspose[row][column] = mat[column][row];
		
		return matrixTranspose;
	}
	
	/*
	 *  Multiplying two 1-Dimensional matrices
	 */
	public static double matrixMult(double mat[], double mat2[])
	{
		double sum = 0;
		
		for (int i = 0; i < mat.length; i++)
		{	
			sum += mat[i]*mat2[i];
		}
		
		return sum;
	}
	
	/*
	 *  Multiplying an 1-Dimensional matrix with 2-Dimensional matrix
	 */
	public static double[] matrixMult(int mat[][], double mat2[])
	{
		double[] mat3 = new double[mat.length];
		double sum = 0;
		
		for (int row = 0; row < mat.length; row++)
			for (int column=0; column < mat.length; column++)
			{
				sum = 0;
				for (int k=0; k < mat.length; k++)
				{
					sum += mat[row][k]*mat2[k];
				}
				mat3[row] = sum;
			}
		
		return mat3;
	}
	
	/*
	 *  Multiplying two 2-Dimensional matrices
	 */
	public static int[][] matrixMult(int mat[][], int mat2[][])
	{
		int [][] mat3 = new int[mat.length][mat2[0].length];
		int sum = 0;
		
		for (int row=0; row < mat.length; row++)
		{
			for (int col=0; col < mat2[0].length; col++)
			{
				sum = 0;
				for (int k=0; k < mat[row].length; k++)
				{
					sum += mat[row][k]*mat2[k][col];
				}
				mat3[row][col] = sum;
			}
		}
		
		return mat3;
	}
	
	/* 
	 * Subtract the two 1-Dimensional matrices
	 */
	public static double[] matrixSubtract(double mat1[], double mat2[])
	{
		double mat3[] = new double [mat1.length];
		
		for (int i = 0; i < mat1.length; i++)
		{
			mat3[i] = Math.abs(mat1[i] - mat2[i]); 
		}
		
		return mat3;
	}
	
	/*
	 * Add the two 1-Dimensional matrices
	 */
	public static double[] matrixAdd(double mat1[], double mat2[])
	{
		double mat3[] = new double [mat1.length];
		
		for (int i = 0; i < mat1.length; i++)
		{
			mat3[i] = mat1[i] + mat2[i]; 
		}
		
		return mat3;
	}
	
	/*
	 * 
	 */
	public static Map<Integer,String> getLinks(List<Integer> documentsList) throws CorruptIndexException, IOException
	{
		String url = null;
		String path = "file:///D:/Eclipse%20Java%20Devloper/Information%20Retrieval/result3";
		Map<Integer,String> map = new LinkedHashMap<>();
		IndexReader r = IndexReader.open(FSDirectory.open(new File("D:\\Eclipse Java EE Developer\\Information Reterival\\IRWebApp\\index")));
		
		for (Integer document : documentsList)
		{
			Document d = r.document(document);
			url = d.getFieldable("path").stringValue();
			url = url.replace("%", "%25");
			url = path + url;
			map.put(document, url);
		}
		
		return map;
	}
	
	/*
	 * Reverse sorting the list and returning the top k results  
	 */
	public static Map<Integer, Double> sortByComparator (Map<Integer,Double> unsortMap, int k)
	{
		List<Map.Entry<Integer, Double>> list = new LinkedList<>(unsortMap.entrySet());
		
		// sort list based on comparator
		Collections.sort(list, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				return -((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Getting top 10 results
		if (list.size() <10)
			list = list.subList(0, list.size());
		else		
			list = list.subList(0, k);
		
		// put sorted list into map again
		Map<Integer,Double> sortedMap = new LinkedHashMap<>();
		Iterator<Map.Entry<Integer, Double>> it = list.iterator();
		while( it.hasNext()) {
			Map.Entry<Integer,Double> entry = (Map.Entry<Integer,Double>) it.next();
			sortedMap.put((Integer)entry.getKey(), (Double)entry.getValue());
		}
		return sortedMap;
	}
	
	/*
	 * Perform clustering using K means algorithm
	 */
	public static Map<Integer, Double> KMeansCluster(Map<Integer, Double> vectorSimilarity, int numberOfClusters) throws CorruptIndexException, IOException 
	{
		IndexReader r = IndexReader.open(FSDirectory.open(new File("D:\\Eclipse Java EE Developer\\Information Reterival\\IRWebApp\\index")));
//		vectorSimilarity
		return vectorSimilarity;
	}
	
}
