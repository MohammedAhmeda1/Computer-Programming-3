import java.rmi.Naming;

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            Calculator calc = (Calculator) Naming.lookup("rmi://localhost:6000/CalculatorService");
            System.out.println("Client --> 5 + 3 = " + calc.add(5, 3));
            System.out.println("Client --> 10 - 4 = " + calc.subtract(10, 4));
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}