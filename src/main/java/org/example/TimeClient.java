package org.example;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class TimeClient {
	private Socket clientSocket;
	ByteBuffer receiveBuffer = ByteBuffer.allocate(24);
	/**
	 * Start the client and calculate NTP values
	 */
	public TimeClient() {
		try {
			System.out.println("=========================");
			System.out.println("  o \t\t  d");
			System.out.println("=========================");

			// A total of 10 measurements
			for (int i = 0; i < 10; i++) {

//				String host = "192.168.50.171";
				String host = "localhost";
				// Open a socket to server
				clientSocket = new Socket(host, 27999);
				// Send NTP request
				sendNTPRequest();
				// Do measurements
				calculateOandD();
				
				// Check if this is the minimum delay NTP Request so far
				if(networkTimeMin == null || networkTimeTemp.getDelayTime() < networkTimeMin.getDelayTime())
					networkTimeMin = networkTimeTemp;
				
				// wait 300ms before next iteration
				Util.sleepThread(100);
			}
			
			// Calculate based on min value of d
			doFinalDelayCalculation();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	NTPNetwork networkTimeMin;
	NTPNetwork networkTimeTemp;

	private void calculateOandD(){

		receiveBuffer.position(0);
		networkTimeTemp.setT1(receiveBuffer.getLong());
		receiveBuffer.position(8);
		networkTimeTemp.setT2(receiveBuffer.getLong());
		receiveBuffer.position(16);
		networkTimeTemp.setT3(receiveBuffer.getLong());
		/**
		 * Offset formula :
		 * 		o = 1/2 * (T(i-2) - T(i-3) + T(i-1) - T(i))
		 *
		 * Revised formula for our notation :
		 * 		o = 1/2 * (T2 - T1 + T3 - T4)
		 */
		System.out.println(networkTimeTemp.getIntervalTime() + "\t\t" + networkTimeTemp.getDelayTime());
	}

	private void sendNTPRequest() {
		// set T1
		ByteBuffer t1Buffer = ByteBuffer.allocate(8);
		t1Buffer.putLong(System.currentTimeMillis());
		// send request object
		try {

			OutputStream outputStream = clientSocket.getOutputStream();
			outputStream.write(t1Buffer.array());

			// wait for server's response
			InputStream inputStream = clientSocket.getInputStream();
			inputStream.read(receiveBuffer.array());

			// Close streams
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// set t4
		networkTimeTemp = new NTPNetwork();
		networkTimeTemp.setT4(System.currentTimeMillis());
	}

	public static void main(String[] args) {
		new TimeClient();
	}
	
	/**
	 * Selects a NTPRequest based on min value of delay for each request
	 */
	private void doFinalDelayCalculation(){
		System.out.println("------------------------");
		System.out.println("Selected time difference   : " + networkTimeMin.getDelayTime());
		System.out.println("Corresponding clock offset : " + networkTimeMin.getIntervalTime());
		System.out.println("Corresponding accuracy   : " 
					+ networkTimeMin.getAccuracyMin()
					+ " to "
					+ networkTimeMin.getAccuracyMax() );
	}

}
