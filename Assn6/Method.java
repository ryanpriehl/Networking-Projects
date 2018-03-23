import java.rmi.*;
import java.rmi.server.*;

public class Method extends UnicastRemoteObject implements MethodInterface {

    public Method() throws RemoteException { }

    public int fibonacci(int num) {
        if (num == 0 || num == 1) {
            return 1;
        } else if (num == 2) {
            return 2;
        } else {
            return fibonacci(num - 1) + fibonacci(num - 2);
        }
    }

    public int factorial(int num) {
        if(num == 0 || num == 1){
            return 1;
        } else {
            return num * factorial(num - 1);
        }
    }

}
