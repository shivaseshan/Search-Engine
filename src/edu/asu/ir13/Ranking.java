package edu.asu.ir13;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

public class Ranking {
	
	static double[] documentTFIDF;
	static Map<String, Double> idfWeights;
	
	public static Map<Integer, Double> Query (IndexReader r, String query) throws IOException
	{
		Map<Integer,Double> resultWeights = new HashMap<Integer,Double>();
		
		int queryWeightValue = 0;
		double weightValue = 0;
		
		String[] terms = query.split("\\s+");
			
		// Taking the magnitude of the query vector
		Map<String, Integer> queryMap = new HashMap<String, Integer>();
		double queryMagnitude = 0; 
		for(String word : terms)
		{
			if (queryMap.containsKey(word))
			{
				queryWeightValue = 1 + queryMap.get(word);
				queryMap.put(word, queryWeightValue);
			}
			else
				queryMap.put(word, 1);
		}
		for (Integer i : queryMap.values())
		{
			queryMagnitude += Math.pow(i, 2);
		}
		// Taking square root of squared query vector magnitude
		queryMagnitude = Math.sqrt(queryMagnitude);
		
		// Incrementally finding the similarity
		for(String word : terms)
		{
			Term term = new Term("contents", word);
			TermDocs tdocs = r.termDocs(term);

			while(tdocs.next())
			{
				if (resultWeights.containsKey(tdocs.doc()))
				{
					weightValue = (double) tdocs.freq()*idfWeights.get(word) + resultWeights.get(tdocs.doc());
					resultWeights.put(tdocs.doc(), weightValue);
				}
				else
						resultWeights.put(tdocs.doc(), (double) tdocs.freq()*idfWeights.get(word));
			}
		}
		
		Iterator<Map.Entry<Integer,Double>> it = resultWeights.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Integer,Double> pairs = (Map.Entry<Integer,Double>)it.next();
			pairs.setValue((double)pairs.getValue()/(documentTFIDF[(int) pairs.getKey()] * queryMagnitude));
		}
		
		// Sorting the results in decreasing order
		long startTime = System.nanoTime();
		Map<Integer, Double> sortedMap = sortByComparator(resultWeights);
		System.out.println(System.nanoTime() - startTime + " total nanoseconds to sort the results");
		
		return sortedMap;
	}
	
	private static Map<Integer, Double> sortByComparator (Map<Integer,Double> unsortMap)
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
		/*if (list.size() <10)
			list = list.subList(0, list.size());
		else		
			list = list.subList(0, 12);*/
		
		// put sorted list into map again
		Map<Integer,Double> sortedMap = new LinkedHashMap<>();
		Iterator<Map.Entry<Integer, Double>> it = list.iterator();
		while( it.hasNext()) {
			Map.Entry<Integer,Double> entry = (Map.Entry<Integer,Double>) it.next();
			sortedMap.put((Integer)entry.getKey(), (Double)entry.getValue());
		}
		return sortedMap;
	}
	
	public static void  precomputeWeights (IndexReader r) throws IOException
	{
		documentTFIDF = new double[r.maxDoc()];
		idfWeights = new HashMap<String, Double>();
				
		TermEnum t = r.terms();
		long startTime = System.nanoTime();	// Starting the timer
		while(t.next())
		{
			Term te = new Term("contents", t.term().text());
			TermDocs td = r.termDocs(te);
			
			try
			{
				idfWeights.put(t.term().text(), Math.log(r.maxDoc()/(double)r.docFreq(te)));
				/*if (r.docFreq(te) > 15000)
					System.out.println(t.term().text());*/
			}
			catch (ArithmeticException e)
			{
				idfWeights.put(t.term().text(), Math.log(r.maxDoc()/r.maxDoc()));
			}
						
			while(td.next())
			{
				documentTFIDF[td.doc()] += Math.pow(td.freq()*idfWeights.get(t.term().text()), 2);
			}
		}
		
		// Taking square root of squared documentTF & documentTFIDF vector magnitude for all documentTFs in corpus
		for (int n = 0; n < r.maxDoc(); n++)
			documentTFIDF[n] = Math.sqrt(documentTFIDF[n]);
		
		System.out.println(System.nanoTime() - startTime + " total nanoseconds to compute document norms");
	}
	
	public static Map<Integer, Double> getResults(String query) throws CorruptIndexException, IOException 
	{
		// the IndexReader object is the main handle that will give you 
		// all the documentTFs, terms and inverted index
		IndexReader r = IndexReader.open(FSDirectory.open(new File("D:\\Eclipse Java EE Developer\\Information Reterival\\IRWebApp\\index")));
		Map<Integer, Double> results;
		if (documentTFIDF == null)
			precomputeWeights(r);
		
		long startTime = System.nanoTime();	
		results = Query(r,query);
		System.out.println(System.nanoTime() - startTime + " total nanoseconds to get the results");
		
		return results;
	}
}
