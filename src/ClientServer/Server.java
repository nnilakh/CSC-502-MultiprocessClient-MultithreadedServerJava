package ClientServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;

public class Server {

	private static final int maxClients = 10;
	private static final ClientThread[] workers = new ClientThread[maxClients];

	public static void main(String args[]) {

		ServerSocket server = null;
		Socket connection = null;
		int portNumber = 4000;
		
		if(args.length == 1)
			portNumber = Integer.valueOf(args[0]).intValue();

		try {
			server = new ServerSocket(portNumber);
		} catch (IOException exception) {
			System.out.println("IOException occurred" + exception);
		}

		System.out.println("The server is running.");
		while (true) {
		try {
			connection = server.accept();
			System.out.println("Connection accepted");
			int i = 0;
			for(; i< maxClients ;i++) {
				if(workers[i] == null) {
					(workers[i] = new ClientThread(connection)).start();
					break;
				}			
			
			}
			
			if(i >= maxClients ) {
				System.out.println("Maximum connections reached");
				connection.close();
			}
				
			

		} catch (IOException exception) {
			System.out.println("IOException occurred" + exception);
		}
		
		}

	}

}

class ClientThread extends Thread {

	private String input = null;
	private DataInputStream serverInput = null;
	private DataOutputStream serverOutput = null;
	private Socket clientSocket = null;	
	private String output = null;

	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		
	}

	public void run() {
		try {

			serverInput = new DataInputStream(clientSocket.getInputStream());
			serverOutput = new DataOutputStream(clientSocket.getOutputStream());

			while (serverInput.available() == 0)
				;
			input = serverInput.readUTF();

			output = service(input);

			serverOutput.writeUTF(output);

		} catch (IOException exception) {
			System.out.println("IOException occurred" + exception);
		}

	}

	public int wordCount(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		return tokenizer.countTokens();
	}

	public int charCount(String line) {
		char[] array = line.toCharArray();
		return array.length;
	}
	
	public int countSentences(String line) {
		int sentences = 0;
		for(int i = 0; i<line.length()-1; i++) {
			if ((line.charAt(i) == '.') && (line.charAt(i+1) != '.')) {
				sentences++;
			}
			if ((line.charAt(i) == '!') && (line.charAt(i+1) != '!')) {
				sentences++;
			}
			if ((line.charAt(i) == '?') && (line.charAt(i+1) != '?')) {
				sentences++;
			}			
		}
		return sentences;
	}

	public String service(String input) {
		StringBuffer response = new StringBuffer();
		int words = 0;
		int chars = 0;

		words += wordCount(input);
		chars += charCount(input);
		
		Map<String, Integer> wordMap = wordMap(input);
		Map<Character, Integer> charMap = charMap(input);
		int sentences = countSentences(input);

		response.append("Text in uppercase--> \n");		
		response.append(input.toUpperCase());
		response.append("\n");
		response.append("Number of words--> " + words);
		response.append("\n");
		response.append("Number of characters--> " + chars);
		response.append("\n");
		response.append("Number of sentences--> " + sentences);
		response.append("\n");		
		response.append("Word occurrences--> \n");

		Iterator iterator = wordMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry keyPair = (Map.Entry) iterator.next();
			response.append(keyPair.getKey() + " " + keyPair.getValue() + "\n");
			iterator.remove();
		}
		
		response.append("Character occurrences--> \n");
		iterator = charMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry keyPair = (Map.Entry) iterator.next();
			response.append(keyPair.getKey() + " " + keyPair.getValue() + "\n");
			iterator.remove();
		}

		return response.toString();

	}

	private Map<Character, Integer> charMap(String input) {
		Map<Character, Integer> charMap = new TreeMap<Character, Integer>();
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) != ' ') {
				if (!charMap.containsKey(input.charAt(i))) {
					charMap.put(input.charAt(i), 1);
				} else {
					charMap.put(input.charAt(i),
							charMap.get(input.charAt(i)) + 1);
				}
			}

		}
		return charMap;
	}

	private Map<String, Integer> wordMap(String input) {
		String[] words = input.split(" ");
		Map<String, Integer> wordMap = new TreeMap<>();
		for (String word : words) {
			if (!wordMap.containsKey(word)) {
				wordMap.put(word, 1);
			} else {
				wordMap.put(word, wordMap.get(word) + 1);
			}

		}
		return wordMap;
	}
}
