package com.mule.pcp.utilities;

import java.util.ArrayList;

public class ReportResults {
	public String status;
	public String critical;
	public String major;
	public String minor;
	public String info;
	public String security;
	public static ArrayList<Criticality> bugs =new ArrayList<Criticality>();

	public ReportResults(ArrayList<Criticality> bugs) {
		this.status=status;
		this.bugs=bugs;
		this.critical=getCount("critical");
		this.major=getCount("major");
		this.minor=getCount("minor");
		this.info=getCount("info");
		this.security=getSecurityCount();
	}
	public static String getCount(String type) {
		Integer cr=0;
		for(Criticality bug:bugs)
		{
			if(bug.bugType == type)
				cr++;
		}
		System.out.println(":::: "+type+" Count : "+cr);
		return cr.toString();
	}
	public static String getSecurityCount() {
		Integer cr=0;
		for(Criticality bug:bugs)
		{
			if(bug.securtiyRisk == true)
				cr++;
		}
		
		return cr.toString();
	}
	public String getBugList(String type) {
		String html="";
		Integer sno=1;
		for(Criticality bug:bugs) {
			
			if(bug.bugType==type)
			{
				html=html.concat(addTableBody(sno.toString(),type,bug.bugDesc,bug.bugLocation,bug.securtiyRisk));
				sno++;
			}
			else if(type=="security")
			{
				if(bug.securtiyRisk)
				{
					html=html.concat(addTableBody(sno.toString(),type,bug.bugDesc,bug.bugLocation,bug.securtiyRisk));
					sno++;
				}
			}
		}
		return html;
	}
	
	public String addTableBody(String sno,String type, String desc, String loc, boolean security) {
		String table ="<tbody><tr><td>"+sno+"</td><td>"+desc+"</td><td>"+loc+"</td></tr></tbody>";
		return table;
	}
}
