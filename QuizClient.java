import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuizClient {
    public static void main(String[] args) throws Exception {
        var socket = new Socket("localhost", 8888);
        System.out.println("Connected to the quiz server...");
        var scanner = new Scanner(System.in);
        var in = new Scanner(socket.getInputStream());
        var out = new PrintWriter(socket.getOutputStream(), true);
        
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
    }
}
