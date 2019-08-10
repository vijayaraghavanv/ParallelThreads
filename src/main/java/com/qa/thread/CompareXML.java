package com.qa.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

public class CompareXML extends TriggerPointWS {
	boolean sourcefile=false;
	boolean targetfile=false;
	
	static String reason="";
	static String sourcename="";
	static String targetname="";
	static String testcasename="";
	static String result="PASS";
	static List<String> description=new ArrayList<String>();
	static HashMap<String,List<String>> hm1=new HashMap<String,List<String>>();
	static HashMap<String,String> hm;
	static Difference difference=null;
	//static WatchService ws=new WatchService();
	
	public static boolean compare(String source,String target,String sresult,String srcfilename) throws IOException, Exception {
		boolean status=false;

			File fsource = new File(source+"\\"+srcfilename);
			File ftarget = new File(target+"\\"+srcfilename);
			File fresult = new File(sresult);
			
			if (fsource.exists() && ftarget.exists()) {
				System.out.println("source and destination file exists");
			
			
			// reading two xml file to compare in Java program
			FileInputStream fisl = new FileInputStream(fsource);
			FileInputStream fis2 = new FileInputStream(ftarget);
			
			// using BufferedReader for improved performance
			BufferedReader source1 = new BufferedReader(new InputStreamReader(fisl));
			BufferedReader target1 = new BufferedReader (new InputStreamReader (fis2));
			
			//configuring XMLValue to ignore white spaces
			XMLUnit.setIgnoreWhitespace(true);
			
			//creating Diff instance to compare two XML files 
			Diff xmlDiff = new Diff(source1, target1); 

			//for getting detailed differences between two xml files 
			DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);

			@SuppressWarnings("rawtypes")
			List differences = detailXmlDiff.getAllDifferences();
			String summary_html = "";
			String location ="";
			
			summary_html  = "<html><head><style>";
			summary_html=summary_html+"table.gridtable{font-family: verdana,arial,sans-serif;font-size:11px;";
			summary_html=summary_html+"color:#333333;border-width:1px;border-color:#666666;border-collapse:collapse;}";
				//padding:8px;border-style:solid;border-color:#666666;";
			summary_html=summary_html+"table.gridtable th{border-width:1px;border-style:solid;border-color:#666666;";
			summary_html=summary_html+"background-color:#dedede;}";
			summary_html=summary_html+"table.gridtable td {border-width: 1px;padding:8px;border-style:solid;";
			summary_html=summary_html+"border-color:#666666;background-color: #ffffff;}</style></head>";
			summary_html=summary_html+"<body><div style='width=100%'><h2 style='width:40%;margin:0 auto'>XML Comparison Summary Result</h2>";
			summary_html=summary_html+"</div><table class='gridtable'><tr>";
		
			summary_html=summary_html+"<th>Sno</th><th>sourcename</th><th>targetname</th><th>Result</th><th>Differences</th><tr>";
			int i=1;
			if(differences.size()>0) {
			for (Object object : differences) {
			difference = (Difference) object;
			System.out.println("*******************");
			System.out.println (difference);
			System.out.println("*******************");
			System.out.println (difference.getDescription());
			description.add (difference.toString());
			//if (difference.getDescription().contains("text value")){
			result = "FAIL";
			reason = "Difference:"+difference.toString();
		//	}
			summary_html=summary_html+"<tr>";
			summary_html=summary_html+"<td>" + i + "</td>";
			summary_html=summary_html+"<td>" + srcfilename + "</td>";
			summary_html=summary_html+"<td>" + srcfilename + "</td>";
			summary_html=summary_html+"<td>" + result + "</td>";
			summary_html=summary_html+"<td>" + reason + "</td>";
			summary_html=summary_html+"</tr>";
			i=i+1;
			status=true;
				}
			}else {
				result="PASS";
				summary_html=summary_html+"<tr>";
				summary_html=summary_html+"<td>" + i + "</td>";
				summary_html=summary_html+"<td>" + srcfilename + "</td>";
				summary_html=summary_html+"<td>" + srcfilename + "</td>";
				summary_html=summary_html+"<td>" + result + "</td>";
				summary_html=summary_html+"<td>" + "No difference in XML" + "</td>";
				summary_html=summary_html+"</tr>";
				status=true;
			}
			summary_html=summary_html+"</table></body></html>";
			   try {
			      String currentdate=new SimpleDateFormat("dd-MM-YYYY-HH-MM-SS").format(new Date());
			      location=fresult+"\\Results_Summary_"+srcfilename+currentdate+".htm";
			      @SuppressWarnings("resource")
				PrintStream out=new PrintStream(new FileOutputStream(location));
			      byte[] bb=summary_html.getBytes();
			      out.write(bb);
			   } catch(IOException x) {
				   System.out.println("Exception thrown:"+x);
			   }
			}	else {
				   System.out.println("source and destination file not exists");
					
			}
	
		return status;
	}
}
