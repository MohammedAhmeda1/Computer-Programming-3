import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String[] choices = {"rock", "paper", "scissors"};
    private static volatile String clientChoice = "";
    private static volatile String serverResponse = "";
    private static volatile boolean running = true;

    // Simulated server logic
    public static void runServer() {
        Random random = new Random();
        while (running) {
            if (!clientChoice.isEmpty()) {
                String serverChoice = choices[random.nextInt(choices.length)];
                String result;
                if (clientChoice.equals(serverChoice)) {
                    result = "Draw";
                } else if ((clientChoice.equals("rock") && serverChoice.equals("scissors")) ||
                           (clientChoice.equals("paper") && serverChoice.equals("rock")) ||
                           (clientChoice.equals("scissors") && serverChoice.equals("paper"))) {
                    result = "You win";
                } else {
                    result = "You lose";
                }
                serverResponse = "Server chose: " + serverChoice + ". " + result + "!";
                clientChoice = ""; // Reset for next round
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
            System.out.print("Enter rock, paper, scissors, or quit: ");
            String choice = scanner.nextLine().toLowerCase();
            if (choice.equals("quit")) {
                running = false;
                break;
            }
            boolean validChoice = false;
            for (String valid : choices) {
                if (choice.equals(valid)) {
                    validChoice = true;
                    break;
                }
            }
            if (!validChoice) {
                System.out.println("Invalid choice! Please choose rock, paper, or scissors.");
                continue;
            }
            clientChoice = choice;
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
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        System.out.println("Starting rock-paper-scissors game simulation...");
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