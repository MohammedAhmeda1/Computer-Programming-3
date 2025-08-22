import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            Calculator calc = new CalculatorImpl();
            Registry registry = LocateRegistry.createRegistry(6000);
            Naming.rebind("rmi://localhost:6000/CalculatorService", calc);
            System.out.println("Server --> Calculator Service bound.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}