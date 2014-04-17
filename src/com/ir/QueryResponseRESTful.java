package com.ir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.asu.ir13.AuthoritiesAndHubs;
import edu.asu.ir13.PageRank;
import edu.asu.ir13.Ranking;
import edu.asu.ir13.Utilities;

@Path("/queryresponse")
public class QueryResponseRESTful {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject queryResponse(@QueryParam("retrievalType") String retrievalType, 
			@QueryParam("query") String query, @QueryParam("probabilityW") String probabilityW) throws JSONException
	{
		JSONObject obj = new JSONObject();
		if (retrievalType.equals("TFIDF"))
		{
			Map<Integer, Double> vectorSimilarity = null;
			Map<Integer, Double> vectorSimilarityClusters = null;
			Map<Integer, String> vectorSimilarityLinks = null;
			try {
				vectorSimilarity = Ranking.getResults(query);
				vectorSimilarity = Utilities.sortByComparator(vectorSimilarity,50);
				vectorSimilarityClusters = Utilities.KMeansCluster(vectorSimilarity,3);
				vectorSimilarityLinks = Utilities.getLinks(new ArrayList<Integer>(vectorSimilarity.keySet()));
				
				JSONArray resultsTFIDF = new JSONArray();
				for (Map.Entry<Integer, String> entry : vectorSimilarityLinks.entrySet())
				{
					JSONObject setTFIDF = new JSONObject();
					setTFIDF.put("url", entry.getValue());
					setTFIDF.put("id", entry.getKey());
					resultsTFIDF.put(setTFIDF);
				}
				obj.put("TFIDF",resultsTFIDF);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (retrievalType.equals("AuthorityHub"))
		{
			Map<Integer, Double> vectorSimilarity;
			Map<Integer,String> authorityLinks = null;
			Map<Integer,String> hubLinks = null;
			try {
				vectorSimilarity = Ranking.getResults(query);
				vectorSimilarity = Utilities.sortByComparator(vectorSimilarity,10);
				Map<Integer,Double>[] returnAuthorityHub = AuthoritiesAndHubs.authoritiesAndHubs(vectorSimilarity);
				authorityLinks = Utilities.getLinks(new ArrayList<Integer>(returnAuthorityHub[0].keySet()));
				hubLinks = Utilities.getLinks(new ArrayList<Integer>(returnAuthorityHub[1].keySet()));
				
				JSONArray resultsAuthority = new JSONArray();
				for (Map.Entry<Integer, String> entry : authorityLinks.entrySet())
				{
					JSONObject setAuthority = new JSONObject();
					setAuthority.put("url", entry.getValue());
					setAuthority.put("id", entry.getKey());
					resultsAuthority.put(setAuthority);
				}
				obj.put("Authority", resultsAuthority);
				
				JSONArray resultsHub = new JSONArray();
				for (Map.Entry<Integer, String> entry : hubLinks.entrySet())
				{
					JSONObject setHub = new JSONObject();
					setHub.put("url", entry.getValue());
					setHub.put("id", entry.getKey());
					resultsHub.put(setHub);
				}
				obj.put("Hub", resultsHub);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (retrievalType.equals("PageRank"))
		{
			Map<Integer, Double> pageRank;
			Map<Integer,String> pageRankLinks = null;
			try {
				pageRank = PageRank.buildPageRank(query, probabilityW);
				pageRankLinks = Utilities.getLinks(new ArrayList<Integer>(pageRank.keySet()));
				
				JSONArray resultsPageRank = new JSONArray();
				for (Map.Entry<Integer, String> entry : pageRankLinks.entrySet())
				{
					JSONObject setPageRank = new JSONObject();
					setPageRank.put("url", entry.getValue());
					setPageRank.put("id", entry.getKey());
					resultsPageRank.put(setPageRank);
				}
				obj.put("PageRank", resultsPageRank);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return obj;
	}
}
