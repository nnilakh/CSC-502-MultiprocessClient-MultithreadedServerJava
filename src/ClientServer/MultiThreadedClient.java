package ClientServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiThreadedClient {
	private static final int maxClients = 2;
	private static final ThreadedClient[] clients = new ThreadedClient[maxClients];

	public static void main(String[] args) {

		printCpuUsage();

		for (int i = 0; i < maxClients; i++) {
			if (clients[i] == null) {
				(clients[i] = new ThreadedClient()).start();
			}

		}

	}

	public static void printCpuUsage() {
		OperatingSystemMXBean opSystemBean = ManagementFactory
				.getOperatingSystemMXBean();

		int numCPU = opSystemBean.getAvailableProcessors();
		double systemLoadAvg = opSystemBean.getSystemLoadAverage();

		double cpuUsage = systemLoadAvg / numCPU;

		System.out.println("CPU Usage: " + cpuUsage);
	}
}

class ThreadedClient extends Thread {

	Socket client = null;
	DataInputStream inputLine = null;

	OutputStream outToServer = null;
	DataOutputStream out = null;
	InputStream inFromServer = null;
	DataInputStream in = null;

	public void run() {
		long connectStartTime = System.currentTimeMillis();

		try {
			client = new Socket("localhost", 4000);
			outToServer = client.getOutputStream();
			out = new DataOutputStream(outToServer);

			inFromServer = client.getInputStream();
			in = new DataInputStream(inFromServer);

			inputLine = new DataInputStream(new BufferedInputStream(System.in));

		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to host");
		}

		long connectEndTime = System.currentTimeMillis();

		long timeToConnect = connectEndTime - connectStartTime;

		//System.out.println("time to connect in milliseconds: " + timeToConnect);

		long numConnectionsPerSec = 1000 / timeToConnect;

		//System.out.println("number of connections possible per second: "+ numConnectionsPerSec);

		System.out.println();

		String fileLoc = null;
		// System.out.println("What file would you like to operate on?");

		try {

			fileLoc = "/Users/vn0n6vr/Documents/OSProject/OSFinalClientServer/src/ClientServer/test.txt";// inputLine.readLine();
			Path path = Paths.get(fileLoc);

			if (!Files.exists(path)) {
				System.out.println("The file does not exist");
			} else {

				out.writeUTF(readFile(path.toFile()));

				long responseStartTime = System.currentTimeMillis();

				while (in.available() == 0)
					;
				String serverResponse = in.readUTF();

				long responseEndTime = System.currentTimeMillis();

				long timeToRespond = responseEndTime - responseStartTime;

				System.out.println("time to respond in milliseconds: "
						+ timeToConnect);

				long numResponsesPerSec = 1000 / timeToRespond;

				System.out.println("number of responses possible per second: "
						+ numResponsesPerSec);

				System.out.println();

				System.out.println(serverResponse);
				writeFile(serverResponse);

				printCpuUsage();

			}

			String cmdInput = null;
			while ((cmdInput = inputLine.readLine()) != null) {

				if (cmdInput.equalsIgnoreCase("quit"))
					break;
			}
			out.close();
			in.close();
			client.close();

		} catch (IOException e) {
			System.err.println("IOException:  " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void printCpuUsage() {
		OperatingSystemMXBean opSystemBean = ManagementFactory
				.getOperatingSystemMXBean();

		int numCPU = opSystemBean.getAvailableProcessors();
		double systemLoadAvg = opSystemBean.getSystemLoadAverage();

		double cpuUsage = systemLoadAvg / numCPU;

		System.out.println("CPU Usage: " + cpuUsage);
	}

	public String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();
		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
			sb.append("\n");
		}
		br.close();
		return sb.toString();
	}

	public void writeFile(String serverResponse) throws IOException {
		File file = new File("result.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(serverResponse);
		bufferedWriter.close();

	}
}
