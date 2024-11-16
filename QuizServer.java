import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static List<String> questions = Arrays.asList(
        "What is the capital of France?",
        "What is 2 + 2?",
        "What is the capital of Japan?",
        "What is the largest planet in our solar system?",
        "What is the chemical symbol for water?"
    );
    private static List<String> answers = Arrays.asList(
        "Paris",
        "4",
        "Tokyo",
        "Jupiter",
        "H2O"
    );

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8888);
        System.out.println("The quiz server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while (true) {
            Socket sock = listener.accept();
            pool.execute(new QuizHandler(sock));
        }
    }

    private static class QuizHandler implements Runnable {
        private Socket socket;

        QuizHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                int score = 0;
                for (int i = 0; i < questions.size(); i++) {
                    out.println("QUESTION:" + questions.get(i));
                    String response = in.nextLine().trim();
                    if (response.equalsIgnoreCase(answers.get(i))) {
                        score += 20;
                        out.println("RESULT: Correct!");
                    } else {
                        out.println("RESULT: Incorrect! The correct answer was " + answers.get(i));
                    }
                }
                out.println("SCORE: Your total score is " + score + " out of 100.");
                out.println("END: Quiz over. Thank you for participating!");

            } catch (Exception e) {
                System.out.println("Error: " + socket);
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
}

