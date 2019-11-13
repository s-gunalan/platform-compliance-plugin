package com.mule.pcp.utilities;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mule.pcp.utilities.Criticality;

public class PlatformAPIs {
	String username = "apiadmin";
	String password = "Support123@";
	public String access_token = "";
	public JSONObject envData;

	public PlatformAPIs() {
		System.out.println(":::::::::: PLatform APIs ::::::::::::");
		this.access_token = login();
		//this.envData = environmentDetails("PPM-Chatbot-PROD");
	}
	public JSONObject apiCall(String reqURL, String reqMethod, String input, Map<String, String> headers) {
		JSONObject result = new JSONObject();
		try {
			String readLine = "";
			URL url = new URL(reqURL);
			System.out.println(":: URL : " + reqURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(reqMethod);
			Set<Map.Entry<String, String>> st = headers.entrySet();
			System.out.println(" :::: Headers : ");
			for (Map.Entry<String, String> me : st) {
				System.out.print(me.getKey() + " : ");
				System.out.println(me.getValue());
				con.setRequestProperty(me.getKey(), me.getValue());
			}
			// con.setRequestProperty("Authorization", "Bearer "+this.access_token);
			// con.setRequestProperty("Accept", "application/json");

			con.setDoOutput(true);
			con.connect();
			if (!input.equals("")) {
				OutputStream os = con.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				os.close();
			}
			System.out.println("::::::::::::: RES CODE " + con.getResponseCode());
			if (con.getResponseCode() < 400) {
				System.out.println("::::::::::::::::::::::::::::: RES " + con.getResponseMessage());
				Scanner sc = new Scanner(con.getInputStream());
				while (sc.hasNext()) {
					readLine += sc.nextLine();
				}
				result = (JSONObject) new JSONParser().parse(readLine);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray apiCallArray(String reqURL, String reqMethod, String input, Map<String, String> headers) {
		JSONArray result = new JSONArray();
		try {
			String readLine = "";
			URL url = new URL(reqURL);
			System.out.println(":: URL : " + reqURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(reqMethod);
			Set<Map.Entry<String, String>> st = headers.entrySet();
			System.out.println(" :::: Headers : ");
			for (Map.Entry<String, String> me : st) {
				System.out.print(me.getKey() + " : ");
				System.out.println(me.getValue());
				con.setRequestProperty(me.getKey(), me.getValue());
			}
			// con.setRequestProperty("Authorization", "Bearer "+this.access_token);
			// con.setRequestProperty("Accept", "application/json");

			con.setDoOutput(true);
			con.connect();
			if (!input.equals("")) {
				OutputStream os = con.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				os.close();
			}
			System.out.println("::::::::::::: RES CODE " + con.getResponseCode());
			if (con.getResponseCode() < 400) {
				System.out.println("::::::::::::::::::::::::::::: RES " + con.getResponseMessage());
				Scanner sc = new Scanner(con.getInputStream());
				while (sc.hasNext()) {
					readLine += sc.nextLine();
				}
				result = (JSONArray) new JSONParser().parse(readLine);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String login() {
		String input = "{\"username\": \"apiadmin\",\"password\": \"Support123@\"}";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json");
		String reqURL = "https://anypoint.mulesoft.com/accounts/login";
		access_token = apiCall(reqURL, "GET", input, headers).get("access_token").toString();
		return access_token;
	}

	public JSONObject environmentDetails(String envName) {
		JSONObject result = new JSONObject();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Authorization", "Bearer " + this.access_token);
		JSONArray env = (JSONArray) (apiCall("https://anypoint.mulesoft.com/accounts/api/profile", "GET",
				"", headers).get("memberOfOrganizations"));

		for (int i = 0; i < env.size(); i++) {
			JSONObject envDetails = (JSONObject) env.get(i);
			if (envDetails.get("name").equals(envName)) 
				result = envDetails;				
		}
		return result;
	}

	public ArrayList<Criticality> validation(String apiId,String orgName,String envName) {
		ArrayList<Criticality> result = new ArrayList<Criticality>();
		JSONObject org = environmentDetails(orgName);
		String env_id="";
		System.out.println(":::::::::::: ORG DETAIILS : "+org.toJSONString());
		//String org_id=org.get("organizationId").toString();
		String org_id=org.get("id").toString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Authorization", "Bearer " + this.access_token);
		
		String envURL = "https://anypoint.mulesoft.com/accounts/api/organizations/"+org_id+"/environments";
		JSONObject orgDetails = apiCall(envURL, "GET", "", headers);
		JSONArray envDetails = (JSONArray) orgDetails.get("data");
		System.out.println("::::::::::::: ENV DETAILS ::::::::::::"+envDetails.toJSONString());
		
		for(int i=0;i<envDetails.size();i++)
		{
			JSONObject env=(JSONObject) envDetails.get(i);
			System.out.println("::::::::::::::: ENVVVVV ::::::::::"+env.toJSONString());
			if(env.get("name").equals(envName)) {
				env_id = env.get("id").toString();
			}
		}
		String apiCheckURL = "https://anypoint.mulesoft.com/apimanager/api/v1/organizations/"+org_id+"/environments/"+env_id+"/apis/"+apiId;
		//System.out.println(apiCall(apiCheckURL, "GET", "", headers).toJSONString());
		JSONObject apiDetails = apiCall(apiCheckURL, "GET", "", headers);
		String instanceLabel;
		try{
			instanceLabel = apiDetails.get("instanceLabel").toString();
		}
		catch(Exception e){
			instanceLabel = "NA";
		}
		String endpointUri;
		try {
			endpointUri = apiDetails.get("endpointUri").toString();}
		catch(Exception e){
			endpointUri = "NA";
		}
		String isPublic = apiDetails.get("isPublic").toString();
		JSONObject endpoint = (JSONObject) apiDetails.get("endpoint");
		String consumerEndpoint;
		try {
		consumerEndpoint = endpoint.get("uri").toString();
		}
		catch(Exception e){
		consumerEndpoint = "NA";
		}
		String deprecated = apiDetails.get("deprecated").toString();
		
		System.out.println("::::::::::::::::::::::::::::::::::::::::: PLATFORM DETAILS ::::::::::::::::::::::::::::::::::::::::::::"+"\nInstance Label : "+instanceLabel+"\nEndpoint URL :"+endpointUri+"\nIs Public : "+isPublic+"\nConsumer Endpoint : "+consumerEndpoint+"\nDeprecated : "+deprecated);
		if(!instanceLabel.equals("PROD"))
			{
			result.add(new Criticality("critical", "Instance Label is not PROD", apiId, true));
			System.out.println("::::::: Instance Label is not PROD ::::");
			}
		if(endpointUri.equals("NA"))
			{
			result.add(new Criticality("critical", "Implementation URI is empty", apiId, true));
			System.out.println("::::::::: ENDPOINT URI IS EMPTY :::");
			}
		if(!isPublic.equals("false"))
			{
			result.add(new Criticality("critical", "Public Portal used", apiId, true));
			System.out.println("::::::::: PUBLIC PORTAL :::::::");
			}
		if(!deprecated.equals("false"))
			{
			result.add(new Criticality("critical", "API is deprecated", apiId, true));
			System.out.println("::::::::: DEPRECATED ::::::::");
			}
		if(consumerEndpoint.equals("NA"))
			{
			result.add(new Criticality("critical", "Consumer Endpoint is Empty", apiId, true));
			System.out.println("::::::::::: Consumer end point is empty ::::::");
			}
		String apiPolicyCheckURL = "https://anypoint.mulesoft.com/apimanager/api/v1/organizations/"+org_id+"/environments/"+env_id+"/apis/"+apiId+"/policies?fullInfo=false";
		JSONArray policyDetails = apiCallArray(apiPolicyCheckURL, "GET", "", headers);
		System.out.println(":::::: POLICY DETAILS ::::::::"+policyDetails.toJSONString());
		Boolean clientIdPolicy = false;
		Boolean throttlinePolicy = false;
		for(int i=0;i<policyDetails.size();i++) {
			JSONObject policy = (JSONObject) policyDetails.get(i);
			System.out.println();
			String policyName = policy.get("policyTemplateId").toString();
			if(policyName.equals("client-id-enforcement"))
			{
				clientIdPolicy=true;
				if(policy.get("disabled").toString().equals("false"))
					System.out.println(":::::::::: CLIENT ID ENFORCEMENT POLICY APPLIED :::::::::");
				else
					{
					result.add(new Criticality("critical", "Client ID Enforcement Policy is disabled", apiId, true));
					System.out.println("::::::::::::::: CLIENT ID ENFORCEMENT POLICY IS DISABLED ::::::::::");
					}
			}
			else if(policyName.equals("throttling"))
			{
				throttlinePolicy=true;
				if(policy.get("disabled").toString().equals("false"))
					System.out.println("::::::::::: THROTTLING POLICY APPLIED :::::::::::::::");
				else
					{
					result.add(new Criticality("critical", "Throttling Policy disabled", apiId, true));
					System.out.println("::::::::::::::: THROTTLING POLICY IS DISABLED ::::::::::");
					}
			}
			else
			{
				result.add(new Criticality("critical", "Other Policy Applied ", apiId, true));
				System.out.println(":::::::::: OTHER POLICY APPLED "+policyName);
			}
		}
		/*policy:
			policyTemplateId
			
		disabled	
		configurationData
		*/
	return result;
	}
}
