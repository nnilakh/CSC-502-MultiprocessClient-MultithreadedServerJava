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

public class Client {

	public static void main(String[] args) {

		Socket client = null;
		DataInputStream inputLine = null;

		OutputStream outToServer = null;
		DataOutputStream out = null;
		InputStream inFromServer = null;
		DataInputStream in = null;

		printCpuUsage();

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

		System.out.println("time to connect in milliseconds: " + timeToConnect);

		long numConnectionsPerSec = 1000 / timeToConnect;

		System.out.println("number of connections possible per second: "
				+ numConnectionsPerSec);

		System.out.println();

		if (client != null && out != null && in != null) {
			try {

				String fileLoc = null;
				// System.out.println("What file would you like to operate on?");

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

					System.out
							.println("number of responses possible per second: "
									+ numResponsesPerSec);

					System.out.println();

					System.out.println(serverResponse);
					writeFile(serverResponse);

					printCpuUsage();

				}

				String input = inputLine.readLine();
				while (!input.equals("quit"))
					;

				out.close();
				in.close();
				client.close();
			} catch (UnknownHostException e) {
				System.err.println("Unknown host exception: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
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

	public static String readFile(File file) throws IOException {
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

	private static void writeFile(String serverResponse) throws IOException {
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