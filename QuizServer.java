import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    // List of quiz questions
    private static List<String> questions = Arrays.asList(
        "What is the capital of Korea?",
        "What is the capital of Japan?",
        "What is the capital of France?",
        "What is the capital of Italy?",
        "What is the capital of China?"
    );

    // Corresponding list of answers
    private static List<String> answers = Arrays.asList(
        "Seoul",
        "Tokyo",
        "Paris",
        "Rome",
        "Beijing"
    );

    public static void main(String[] args) throws Exception {
        // Create a server socket that listens on port 8888
        ServerSocket listener = new ServerSocket(8888);
        System.out.println("The quiz server is running...");
        
        // Create a thread pool to handle multiple clients
        ExecutorService pool = Executors.newFixedThreadPool(20);
        
        // Continuously accept client connections
        while (true) {
            Socket sock = listener.accept();
            // Assign each client to a new thread
            pool.execute(new QuizHandler(sock));
        }
    }

    private static class QuizHandler implements Runnable {
        private Socket socket;

        // Constructor to initialize the socket
        QuizHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                // Set up input and output streams
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                int score = 0; // Initialize the score for the client
                
                // Iterate over each question
                for (int i = 0; i < questions.size(); i++) {
                    out.println("QUESTION: [" + questions.get(i) + "]"); // Send question to the client

                    if (in.hasNextLine()) {
                        String response = in.nextLine().trim(); // Read client's response
                        
                        // Check if the response starts with "ANSWER:"
                        if (response.startsWith("ANSWER:")) {
                            String answer = response.substring(7).trim(); // Extract the actual answer
                            // Check if the answer is correct
                            if (answer.equalsIgnoreCase(answers.get(i))) {
                                score += 20; // Add points for correct answer
                                out.println("RESULT: Correct!");
                            } else {
                                out.println("RESULT: Incorrect! The correct answer was " + answers.get(i));
                            }
                        } else {
                            // Handle invalid response format
                            out.println("ERROR: Invalid answer format.");
                        }
                    } else {
                        // Handle case where no answer is received
                        out.println("ERROR: No answer received.");
                        break;
                    }
                }
                // Send the final score to the client
                out.println("SCORE: Your total score is " + score + " out of 100.");
                out.println("END: Quiz over. Thank you for participating!");
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                // Close the socket after communication is done
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close socket: " + e.getMessage());
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}
