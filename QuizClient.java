import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuizClient {
    public static void main(String[] args) {
        try {
            // read server IP and Port Number from server_info.dat
            BufferedReader reader = new BufferedReader(new FileReader("server_info.dat"));
            String serverAddress = reader.readLine();  
            int port = Integer.parseInt(reader.readLine());  
            reader.close();

            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to the quiz server...");
            Scanner scanner = new Scanner(System.in);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("QUESTION:")) {
                    System.out.println(line.substring(9)); // Print the question
                    String answer = scanner.nextLine();
                    out.println(answer); // Send the answer
                } else if (line.startsWith("RESULT:")) {
                    System.out.println(line.substring(7)); // Print the result
                } else if (line.startsWith("END:")) {
                    System.out.println(line.substring(4)); // Print the end message
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

