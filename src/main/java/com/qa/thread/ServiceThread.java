package com.qa.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceThread implements Runnable {
	static ConcurrentHashMap<String, String> filename = new ConcurrentHashMap<String, String>();
	static String requestFile;
	static String responseFile;
	static String path = "C:\\Utility\\Service\\tempsrc";
	static String fsource = "C:\\Utility\\Pairing\\Source";
	static String ftarget = "C:\\Utility\\Pairing\\Target";
	static String archieve = "C:\\Utility\\Archieve";
	static ConcurrentHashMap<String, String> hm=new ConcurrentHashMap<String, String>();
	// static CloseableHttpResponse response;
	static boolean status = false;
	

	

	public void run() {
	
		try {
			System.out.println("service thread started");
			Thread.sleep(100);
			System.out.println("INSIDE TRY-- service map clear");
			if(filename.size()!=0)
			filename.clear();
			if(hm.size()!=0)
				hm.clear();				
				
			 hm = fileIteration();
			mapIteration(hm, path);		
			System.out.println("service thread ended");
		} 
		catch (Exception e) {
			if(filename.size()!=0)
			filename.clear();
			if(hm.size()!=0)
				hm.clear();	
		e.printStackTrace();
		}
	
	}

	public ConcurrentHashMap<String, String> fileIteration() {
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing.length!=0) {
			for (File child : directoryListing) {
				if (child.getName().contains("Request")) {
					requestFile = child.getName();
				}
				responseFile = requestFile.replaceAll("Request", "Response");
				File f = new File(path + "\\" + responseFile);
				if (f.exists() && !f.isDirectory()) {
					filename.put(requestFile, responseFile);
				}
			}
		}
		return filename;
	}

	public void mapIteration(ConcurrentHashMap<String, String> hm, String path) throws ParseException, IOException {
		if (hm.size()!=0) {
			for (Entry<String, String> m : hm.entrySet()) {
				requestFile = m.getKey();
				responseFile = m.getValue();

				try {
					readRequestXML(requestFile, path);
					//status = postXMLresponse(requestFile, path);
					status = readResponseXML(responseFile, requestFile, path);
									
				} 
				catch (InterruptedException e) {
					if(hm.size()!=0)
						hm.clear();	
					
					if(filename.size()!=0)
					filename.clear();
					System.out.println("INSIDE CATCH MAP ITERATION--service thread map cleared");
					e.printStackTrace();
				}
				
			}
			if(hm.size()!=0)
				hm.clear();	
			if(filename.size()!=0)
			filename.clear();
		}
	}

	public void readRequestXML(String requestFile, String path) throws IOException {
		String fileName=path+"\\"+ requestFile;
		System.out.println(fileName);
		if(new File(fileName).exists())
		{
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer request = new StringBuffer();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
		//	line=line.replaceAll("Washington", "Chennai");
			request.append(line);
			
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(ftarget + "\\" + requestFile));
		bw.write(request.toString());
		bw.close();
		bufferedReader.close();
		File afile = new File(fileName);
		afile.delete();
	}
	}
	
	
	
	public static boolean postXMLresponse(String requestFile, String path) throws ParseException, IOException, InterruptedException {
		boolean status = false;
		 URL urlForGetRequest = new URL("http://www.deeptraining.com/webservices/weather.asmx?WSDL");
		    String readLine = null;
		    StringBuffer response = null;
		    BufferedReader in = null;
		    URLConnection connection = urlForGetRequest.openConnection();
		    HttpURLConnection httpConn = (HttpURLConnection)connection;
		    httpConn.setRequestMethod("GET");
		    httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		    int responseCode = httpConn.getResponseCode();
		    System.out.println(responseCode);
		    if (responseCode == HttpURLConnection.HTTP_OK) {
		        in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		         response = new StringBuffer();
		        while ((readLine = in .readLine()) != null) {
		            response.append(readLine);
		        } in .close();
		    }
		        else {
		        	System.out.println("error..");
		        }
		    BufferedWriter bw = new BufferedWriter(new FileWriter(ftarget + "\\" + requestFile));
		    bw.write(response.toString());
		    in.close();
			bw.close();
			File afile = new File(path + "\\" + requestFile);
			afile.delete();
		return status;
	}

	public static boolean readResponseXML(String responseFile, String requestFile, String path) throws IOException, InterruptedException {
		String fileName = path + "\\" + responseFile;
		if (new File(fileName).exists()) 
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			//	line=line.replaceAll("Washington", "Chennai");
				response.append(line);
				status = true;
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(fsource + "\\" + requestFile));
			bw.write(response.toString());
			bufferedReader.close();
			fileReader.close();
			bw.close();
			File afile = new File(fileName);
			afile.delete();
		
			
		}
		return status;
	}

	
	
	
}
