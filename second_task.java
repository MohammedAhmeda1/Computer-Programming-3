import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PrinterQueue {
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    class Printer extends Thread {
        private String printerName;

        public Printer(String name) {
            this.printerName = name;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = queue.take();
                    if (message.equals("STOP")) {
                        break;
                    }
                    String formattedMessage = message.substring(0, 1).toUpperCase() + message.substring(1);
                    System.out.println(printerName + ": " + formattedMessage + " (length: " + message.length() + ")");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Sender extends Thread {
        private String[] messages;

        public Sender(String[] messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            try {
                for (String message : messages) {
                    queue.put(message);
                    Thread.sleep(100);
                }
                queue.put("STOP");
                queue.put("STOP");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void startPrinting() throws InterruptedException {
        Printer printer1 = new Printer("Printer 1");
        Printer printer2 = new Printer("Printer 2");
        String[] messages = {
            "hello everyone!",
            "welcome to java",
            "printer queue demo",
            "multithreading example"
        };
        Sender sender = new Sender(messages);

        printer1.start();
        printer2.start();
        sender.start();

        printer1.join();
        printer2.join();
        sender.join();
    }

    public static void main(String[] args) throws InterruptedException {
        PrinterQueue printerQueue = new PrinterQueue();
        printerQueue.startPrinting();
    }
}