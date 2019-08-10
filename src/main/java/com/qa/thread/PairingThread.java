package com.qa.thread;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class PairingThread implements Runnable {
	final long timeinterval = 6000;
	boolean flag = false;

	private static HashMap<String, String> srcfilename = new HashMap<String, String>();

	static String pairedfsource = "C:\\Utility\\Pairing\\Source";
	static String pairedftarget = "C:\\Utility\\Pairing\\Target";
	static String comparedfsource = "C:\\Utility\\Comparison\\Source";
	static String comparedftarget = "C:\\Utility\\Comparison\\Target";

	public void run() {

		System.out.println("Pairing thread started");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		try {

			srcfilename = watchservice_time();
			System.out.println("from watch 1::" + srcfilename);
			watchservice2(srcfilename);
			System.out.println("from watch 2::");
			System.out.println("Pairing thread ended");

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Thread.sleep(timeinterval);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	static HashMap<String, String> watchservice_time() {
		Path path = Paths.get(pairedfsource);
		System.out.println(path);
		File dir = new File(path.toString());
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				System.out.println("pairedfsource:" + child.getName());
				srcfilename.put(child.getName(), child.getName());
			}
		} else {
			System.out.println("source folder is empty");
		}
		return srcfilename;
	}

	static void watchservice2(HashMap<String, String> srcfile) throws Exception {
		final Path path = FileSystems.getDefault().getPath(pairedftarget);
		System.out.println(path);
		boolean Flag = true;
		while (Flag) {
			File dir = new File(path.toString());
			File[] directoryListing = dir.listFiles();
			if (directoryListing.length != 0) {
				for (File child : directoryListing) {
					if (srcfile.size()!=0) {
						String temp = srcfile.get(child.getName());
						if (temp.equalsIgnoreCase(child.getName())) {
							System.out.println("File exists");
							Thread.sleep(100);
							Flag = false;
							System.out.println("File added for comparison" + child.getName());
							try {
								File afile = new File(pairedfsource + "\\" + child.getName());
								File bfile = new File(pairedftarget + "\\" + child.getName());
								File cfile = new File(comparedfsource + "\\" + child.getName());
								File dfile = new File(comparedftarget + "\\" + child.getName());
								if (afile.exists() && bfile.exists()) {
									try {
										moveandoverwritesource(afile, cfile);
										moveandoverwritetarget(bfile, dfile);
										watchservice_time();
									} catch (IOException e) {
										System.out.println("File already moved");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}
				}
			} else {
				System.out.println("no file exists in target folder");
				srcfile.clear();
				Arrays.fill( directoryListing, null );
				srcfilename.clear();
				System.out.println("Pairing map cleared");
				
			}
			
			if(Flag==false) {
				System.out.println("no file exists in target folder");
				srcfile.clear();
				Arrays.fill( directoryListing, null );
				srcfilename.clear();
				System.out.println("Pairing map cleared");
				break;
			}
		} 
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

	// }
	public HashMap<String, String> setsrc(HashMap<String, String> mapname) {
		return this.srcfilename = mapname;
	}

	public HashMap<String, String> getsrc() {
		return srcfilename;
	}

}
