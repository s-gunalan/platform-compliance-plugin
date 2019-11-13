package com.mule.pcp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.mule.pcp.utilities.ReportResults;
import com.mule.pcp.utilities.Criticality;
import com.mule.pcp.utilities.PlatformAPIs;
import com.mule.pcp.utilities.PropertyUtility;

@Mojo(name = "review")
public class PlatformCompliance extends AbstractMojo{

	public void execute() throws MojoExecutionException, MojoFailureException {
		PropertyUtility prop = new PropertyUtility("dev");
		prop.apiProps();
		Map<String,String> props = prop.loadProperties();
		System.out.println("API ID " + props.get("api.id"));
		System.out.println("API ENV " + props.get("api.environment"));
		
		PlatformAPIs pf = new PlatformAPIs();
		ArrayList<Criticality> Bugs = pf.validation(props.get("api.id"),props.get("api.organization"), props.get("api.environment"));
		generateReports(Bugs);
		//pf.instanceLabelCheck();
		
	}
	public void generateReports(ArrayList<Criticality> Bugs) {
		try {
		System.out.println(":::::::::: Loading Template ::::::");
		InputStream res = this.getClass().getResourceAsStream("/Template.html");
		String htmlString = IOUtils.toString(res, "UTF-8");
		ReportResults repResults=new ReportResults(Bugs);
		if((Integer.parseInt(repResults.critical)<1)&&(Integer.parseInt(repResults.major)<=3)&&(Integer.parseInt(repResults.minor)<=5)&&(Integer.parseInt(repResults.security)<1))
		{
			htmlString=htmlString.replace("${pcp.status}", "Qualified");
		}
		else
		{
			htmlString=htmlString.replace("${pcp.status}", "DisQualified");
		}
		htmlString=htmlString.replace("${critical.count}", repResults.critical);
		htmlString=htmlString.replace("${critical.bug.list.table}", repResults.getBugList("critical"));
		//System.out.println("::: HTML \n"+htmlString);
		File file = new File("target/platform/Results.html");
		FileUtils.writeStringToFile(file, htmlString);
		System.out.println(":::::::::: Written to "+file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}

