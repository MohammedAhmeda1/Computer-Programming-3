import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {
    private final String text;
    private final List<String> shortNames;
    private volatile String modifiedText;
    private final List<String> phoneNumbers;

    public TextProcessor(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null");
        }
        this.text = text;
        this.shortNames = new ArrayList<>();
        this.modifiedText = text;
        this.phoneNumbers = new ArrayList<>();
    }

    class ShortNameFinder extends Thread {
        @Override
        public void run() {
            try {
                Pattern pattern = Pattern.compile("\\b[A-Z][a-z]?\\b");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    synchronized (shortNames) {
                        shortNames.add(matcher.group());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in ShortNameFinder: " + e.getMessage());
            }
        }
    }

    class EmailReplacer extends Thread {
        @Override
        public void run() {
            try {
                Pattern pattern = Pattern.compile("\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b");
                Matcher matcher = pattern.matcher(modifiedText);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, "[EMAIL_HIDDEN]");
                }
                matcher.appendTail(sb);
                synchronized (TextProcessor.this) {
                    modifiedText = sb.toString();
                }
            } catch (Exception e) {
                System.err.println("Error in EmailReplacer: " + e.getMessage());
            }
        }
    }

    class PhoneNumberExtractor extends Thread {
        @Override
        public void run() {
            try {
                Pattern pattern = Pattern.compile("\\b\\d{10,11}\\b");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    synchronized (phoneNumbers) {
                        phoneNumbers.add(matcher.group());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in PhoneNumberExtractor: " + e.getMessage());
            }
        }
    }

    public void process() {
        try {
            ShortNameFinder nameFinder = new ShortNameFinder();
            EmailReplacer emailReplacer = new EmailReplacer();
            PhoneNumberExtractor phoneExtractor = new PhoneNumberExtractor();

            nameFinder.start();
            emailReplacer.start();
            phoneExtractor.start();

            nameFinder.join();
            emailReplacer.join();
            phoneExtractor.join();

            System.out.println("Short names found: " + shortNames);
            System.out.println("Text with emails hidden: " + modifiedText);
            System.out.println("Phone numbers found: " + phoneNumbers);
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String text = "Hi, I'm A.\n\n" +
                     "Contact me at a@short.com or support@example.org.\n\n" +
                     "You can also reach us via our assistant: info@company.net.\n\n" +
                     "Call us at 01012121212 or 01156789012 or 01234567890 or 01512345678.\n\n" +
                     "My friends: Al, Bo, Ann, Joe, Z, K, Moe.\n\n" +
                     "Random words: supercalifragilisticexpialidocious, ok, i, no.";
                     
        TextProcessor processor = new TextProcessor(text);
        processor.process();
    }
}