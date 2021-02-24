package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TimeServer {
	private ServerSocket serverSocket;
	/**
	 * Start Time server and listen for connections
	 */
	public TimeServer() {
		try {
			serverSocket = new ServerSocket(27999);
			System.out.println("Server started on port: " + 27999);
			System.out.println("waiting for connection");

			// Always keep trying for new client connections
			while (true) {
				try {
					Socket incomingSocket = serverSocket.accept();
					// Handle the incoming NTP request on a new thread
					NTPRequestHandler ntpReqHandler = new NTPRequestHandler(incomingSocket);
					new Thread(ntpReqHandler).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TimeServer();
	}
	

	/**
	 * NTPRequest Handler for the server side
	 */
	private class NTPRequestHandler implements Runnable {
		private Socket mCientSocket;

		public NTPRequestHandler(Socket clientSocket) {
			mCientSocket = clientSocket;

		}

		@Override
		public void run() {
			InputStream is;
			ByteBuffer t1Buffer = ByteBuffer.allocate(8);
			try {
				is = mCientSocket.getInputStream();
				is.read(t1Buffer.array());
			} catch (IOException e) {
				e.printStackTrace();
			}

			// set T2 value
			long t2 = System.currentTimeMillis();
			
			// set T3 value
			ByteBuffer ansWerBuffer = ByteBuffer.allocate(24);
			// T1
			ansWerBuffer.put(t1Buffer);
			// T2
			ansWerBuffer.putLong(t2);
			// T3
			ansWerBuffer.putLong(System.currentTimeMillis());
			// Respond to client
			sendNTPAnswer(ansWerBuffer.array());
		}

		private void sendNTPAnswer(byte[] bytes) {
			// write to client socket
			try {
				OutputStream outputStream= mCientSocket.getOutputStream();
				outputStream.flush(); // -TODO- Flush before write?
				outputStream.write(bytes);
				outputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// Close socket
			try {
				mCientSocket.close();
			} catch (Exception e) {
				System.out.println("failed to close socket");
				e.printStackTrace();
			}
		}
	}
}