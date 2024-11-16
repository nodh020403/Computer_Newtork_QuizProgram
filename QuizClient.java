import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuizClient {
    public static void main(String[] args) {
        try {
            // Initialize server address and port from configuration file
            BufferedReader reader = null;
            String serverAddress = "localhost"; // Default server address
            int port = 8888; // Default port
            try {
                // Read server details from 'server_info.dat'
                reader = new BufferedReader(new FileReader("server_info.dat"));
                serverAddress = reader.readLine();
                port = Integer.parseInt(reader.readLine());
            } catch (FileNotFoundException e) {
                // If the file is not found, use default values
                System.out.println("server_info.dat not found. Using default server: localhost:8888");
            } catch (IOException e) {
                System.out.println("Error reading server_info.dat: " + e.getMessage());
            } finally {
                if (reader != null) reader.close();
            }

            // Establish a connection to the server
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to the quiz server...");
            
            // Set up input and output streams
            Scanner scanner = new Scanner(System.in); // For user input
            Scanner in = new Scanner(socket.getInputStream()); // For reading server messages
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // For sending answers

            // Main loop to receive questions and send answers
            while (in.hasNextLine()) {
                String line = in.nextLine(); // Read a line from the server
                
                if (line.startsWith("QUESTION:")) {
                    System.out.println(line.substring(9).trim()); // Display the question
                    System.out.print("Your answer: ");
                    String answer = scanner.nextLine(); // Read user's answer
                    out.println("ANSWER: " + answer); // Send answer to the server
                } else if (line.startsWith("RESULT:")) {
                    System.out.println(line.substring(7).trim()); // Display result (Correct/Incorrect)
                } else if (line.startsWith("SCORE:")) {
                    System.out.println(line.substring(6).trim()); // Display the total score
                } else if (line.startsWith("ERROR:")) {
                    System.out.println("Error from server: " + line.substring(6).trim()); // Display error messages
                } else if (line.startsWith("END:")) {
                    System.out.println(line.substring(4).trim()); // Display end message and exit
                    break;
                }
            }
            // Close the socket after the quiz ends
            socket.close();
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
