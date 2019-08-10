package com.qa.thread;

public class TriggerPointWS {
	static String fsource = "C:\\Utility\\Comparison\\Source";
	static String ftarget = "C:\\Utility\\Comparison\\Target";
	static String fresult = "C:\\Utility\\Comparison\\Result";

	public static void main(String[] args) {

for (;;) {
			System.out.println("processing started..");
			ServiceThread obj = new ServiceThread();
			Thread t=new Thread(obj);
			t.start();
			try {Thread.sleep(100);}catch(Exception e) {}
			PairingThread obj1 = new PairingThread();
			Thread t1=new Thread(obj1);
			t1.start();
			try {Thread.sleep(100);}catch(Exception e) {}	
			ExecutionThread obj2 = new ExecutionThread();
			Thread t2=new Thread(obj2);
			t2.start();
			System.out.println("processing finished..");
		}
	}
}
