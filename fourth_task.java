import java.util.Random;
import java.util.Scanner;

public class Main {
    private static volatile String clientGuess = "";
    private static volatile String serverResponse = "";
    private static volatile boolean running = true;

    // Simulated server logic
    public static void runServer() {
        Random random = new Random();
        int targetNumber = random.nextInt(10) + 1;
        System.out.println("Server chose number: " + targetNumber);

        while (running) {
            if (!clientGuess.isEmpty()) {
                try {
                    int guessNum = Integer.parseInt(clientGuess.trim());
                    if (guessNum < targetNumber) {
                        serverResponse = "Too low";
                    } else if (guessNum > targetNumber) {
                        serverResponse = "Too high";
                    } else {
                        serverResponse = "Correct! You win!";
                        running = false;
                    }
                } catch (NumberFormatException e) {
                    serverResponse = "Please send a valid number";
                }
                clientGuess = ""; // Reset for next round
                try {
                    Thread.sleep(100); // Simulate network delay
                } catch (InterruptedException e) {
                    System.err.println("Server thread interrupted: " + e.getMessage());
                }
            }
        }
    }

    // Simulated client logic
    public static void runClient() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.print("Enter your guess (1-10): ");
            String guess = scanner.nextLine();
            clientGuess = guess;
            // Wait for server response
            while (serverResponse.isEmpty() && running) {
                try {
                    Thread.sleep(100); // Wait for server
                } catch (InterruptedException e) {
                    System.err.println("Client thread interrupted: " + e.getMessage());
                }
            }
            if (!serverResponse.isEmpty()) {
                System.out.println(serverResponse);
                serverResponse = "";
                if (serverResponse.equals("Correct! You win!")) {
                    running = false;
                }
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        System.out.println("Starting number guessing game simulation...");
        // Start server thread
        Thread serverThread = new Thread(() -> runServer());
        serverThread.start();

        // Run client in main thread
        runClient();

        // Stop server thread
        running = false;
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.err.println("Error joining server thread: " + e.getMessage());
        }
        System.out.println("Game ended.");
    }
}