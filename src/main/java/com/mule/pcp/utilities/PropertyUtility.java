package com.mule.pcp.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtility {
	String env = "";
	File propFile;
	

	public PropertyUtility(String env) {
		this.env = env;
		this.propFile = new File("src/main/resources/" + env + "/properties/platform.properties");
	}

	public Map<String, String> loadProperties() {
		Map<String, String> result = new HashMap<String, String>();
		try {
		Properties props=new Properties();
		props.load(new FileInputStream(this.propFile));
		Enumeration propKeys = props.keys();
		while (propKeys.hasMoreElements()) {
			String Key = propKeys.nextElement().toString();
			String Value = props.getProperty(Key);
			result.put(Key, Value);
			System.out.println(":::: KEY : " + Key + "\t\t| VALUE : " + Value);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public ArrayList<Map> apiProps(){
		ArrayList<Map> result = new ArrayList<Map>();
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(this.propFile));
			Enumeration propKeys = props.keys();
			while(propKeys.hasMoreElements()) {
				Map<String, String> apiDetails = new HashMap<String, String>();
				String key =  propKeys.nextElement().toString();
				if(key.endsWith("api.id")) {
					System.out.println(":::::::::::::: API KEYS : " +key.substring(-6));
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
