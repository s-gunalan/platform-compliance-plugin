package com.mule.pcp.utilities;

import java.util.ArrayList;

public class Criticality {
	//public static Integer critical;
	public String bugType = "Code Smell";
	public String bugDesc;
	public String bugLocation;
	public boolean securtiyRisk=false;
	public String test = "";
	public Criticality(String bugType,String bugDesc, String bugLocation, boolean securtiyRisk) {
		this.bugType=bugType;
		this.bugDesc=bugDesc;
		this.bugLocation=bugLocation;
		this.securtiyRisk=securtiyRisk;
	}
}
