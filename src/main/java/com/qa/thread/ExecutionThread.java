package com.qa.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;

public class ExecutionThread implements Runnable{

		 String fsource;
		 String ftarget;
		
		 static String srcfilename;
		 static boolean status=false;
		 static ConcurrentHashMap<String, String> compareMap=new ConcurrentHashMap<String, String>();
		 static String comparedfsource ="C:\\Utility\\Comparison\\Source";
		 static String comparedftarget ="C:\\Utility\\Comparison\\Target";
		 static String path = "C:\\Utility\\Service\\tempsrc";
		 static String archieve = "C:\\Utility\\Archieve";
		 static String fValidated = "C:\\Utility\\ComparedXMLs";
		 static String fresult = "C:\\Utility\\Comparison\\Result";
	
		
	
	public void run() {
		
		System.out.println("Execution thread started");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		
			e1.printStackTrace();
		}
		
		compareMap = fileIteration();
		
		mapIteration(compareMap);
		
		
		System.out.println("Execution thread ended");	
	}

		public ConcurrentHashMap<String, String> fileIteration(){
			String fileName=null;
			File dir = new File(comparedftarget);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.getName().contains("Request")) {
						fileName=child.getName();
					}
				if(child.exists() && !child.isDirectory()) {
					compareMap.put(fileName, fileName);
				}
			  }
			}	
			return compareMap;
		}
		
		public synchronized static void mapIteration(ConcurrentHashMap<String, String> compareMap) {
			System.out.println("comparison started..");
			for (Map.Entry<String, String> entry : compareMap.entrySet()) {
				srcfilename = entry.getKey();
				try {
					status = CompareXML.compare(comparedfsource, comparedftarget, fresult, srcfilename);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (status == true) {
					File afile = new File(comparedfsource + "\\" + srcfilename);
					File bfile = new File(comparedftarget + "\\" + srcfilename);
					File cfile = new File(fValidated + "\\source" + "\\" + srcfilename);
					File dfile = new File(fValidated + "\\target" + "\\" + srcfilename);
					System.out.println("fresult::" + fresult);
					try {
						moveandoverwritesource(afile, cfile);
						moveandoverwritetarget(bfile, dfile);
					} catch (IOException e) {
						System.out.println("File already moved");
					}
				} else {
					System.out.println("Target file or source file not exists");
				}
				
			}
			System.out.println("comparison ended..");
			compareMap.clear();
		}
		
		public synchronized static void moveandoverwritesource(File source, File dest) throws IOException {
			try {
				FileUtils.copyFile(source, dest);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (!source.delete()) {
				throw new IOException("Failed to delete" + source.getName());
			}
		}

		public synchronized static void moveandoverwritetarget(File source, File dest) throws IOException {
			try {
				FileUtils.copyFile(source, dest);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (!source.delete()) {
				throw new IOException("Failed to delete" + source.getName());
			}
		}

	public static boolean movement(String path, String archieve, String requestFile) {
		boolean status = false;
		Path result = null;
		if (!compareMap.isEmpty()) {

			try {
				Thread.sleep(2000);
				Runtime.getRuntime().exec("TASKKILL /F /IM "+requestFile);
				//System.exit(0);
				File afile=new File(path+"\\"+requestFile);	
			//	boolean sta=afile.canWrite();
				boolean locked=isFileUnlocked(afile);
				//result=Files.move(afile.toPath(),Paths.get(archieve).resolve(afile.getName()), StandardCopyOption.REPLACE_EXISTING);
				result = Files.move(Paths.get(path + "\\" + requestFile), Paths.get(archieve + "\\" + requestFile),
						StandardCopyOption.REPLACE_EXISTING);
				boolean deletable=checkDeletable(path+"\\"+requestFile);
			} catch (IOException e) {
				System.out.println("Exception while moving file::" + e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result != null) {
				System.out.println("File moved successfully");
				status = true;
			} else {
				System.out.println("File moved failed");
			}

		}
		return status;
	}
	
	
	public static boolean isFileUnlocked(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            if (in!=null) in.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
	
	public static boolean checkDeletable(String FilePath) {
		boolean status=false;
		try {
			Files.deleteIfExists(Paths.get(FilePath));
			status =true;
		} catch (NoSuchFileException e) {
			System.out.println("No such file/directory exists");
		} catch (DirectoryNotEmptyException e) {
			System.out.println("Directory is not empty.");
		} catch (IOException e) {
			System.out.println("Invalid permissions.");
		}

		System.out.println("Deletion successful.");
		return status;
	}
		
}
