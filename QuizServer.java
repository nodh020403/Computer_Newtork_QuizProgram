import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static List<String> questions = Arrays.asList(
        "What is the capital of France?",
        "What is 2 + 2?",
        "What is the capital of Japan?"
    );
    private static List<String> answers = Arrays.asList(
        "Paris",
        "4",
        "Tokyo"
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
        
        QuizHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            System.out.println("Connected: " + socket);
            try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                for (int i = 0; i < questions.size(); i++) {
                    out.println("QUESTION:" + questions.get(i));
                    String response = in.nextLine().trim();
                    if (response.equalsIgnoreCase(answers.get(i))) {
                        out.println("RESULT: Correct!");
                    } else {
                        out.println("RESULT: Incorrect! The correct answer was " + answers.get(i));
                    }
                }
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

