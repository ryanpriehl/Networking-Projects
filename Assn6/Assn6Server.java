import java.rmi.*;
import java.rmi.server.*;

public class Assn6Server {
    public static void main(String[] args) {
        try {
            Method method = new Method();
            //Method stub = (Method) UnicastRemoteObject.exportObject(method, 0);
            System.setProperty("java.rmi.server.hostname", args[0]);
            Naming.rebind("rmi://" +args[0] +"/cecs327", method);
            System.out.println("The server is ready. Connect using:\n\trmi://" +args[0] +"/cecs327");
        } catch(Exception e) {
            System.out.println("Server failed: " +e);
        }
    }
}
