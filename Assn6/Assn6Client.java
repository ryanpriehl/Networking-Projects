import java.rmi.*;

public class Assn6Client {
    public static void main(String[] args) {
        MethodInterface method;
        try {

            if(args.length != 3){
                System.out.println("Incorrect number of arguments. The arguments should be:");
                System.out.println("{RMI location} factorial {number} OR {RMI location} fibonnaci {number}\n");
                return;
            }

            method = (MethodInterface)Naming.lookup(args[0]);

            int input = 0;
            try {
                input = Integer.valueOf(args[2]);
                if (input < 0) throw new NumberFormatException();
            } catch(NumberFormatException e){
                System.out.println("Input must be a non-negative integer. Try again.\n");
                return;
            }

            if(args[1].equals("factorial")) {
                System.out.println("The factorial of " +args[2] +" is " +method.factorial(input) +".\n");
            }
            else if(args[1].equals("fibonacci")) {
                System.out.println("The fibonacci of " +args[2] +" is " +method.fibonacci(input) +".\n");
            }
            else {
                System.out.println("Command \"" +args[1] +"\" not recognized.\n");
            }

        } catch(Exception e) {
            System.out.println("Client error: " +e +".\n");
        }
    }
}
