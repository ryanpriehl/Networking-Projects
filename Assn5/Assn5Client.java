import java.net.*;
import java.io.*;

public class Assn5Client {
    public static void main (String args[]) {
        // Arguments supply message and hostname of destination
        Socket s = null;
        try {
            // Setting up and connecting to server
            int serverPort = 7896;
            s = new Socket(args[1], serverPort);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            // Sending message and receiving reply
            out.writeUTF(args[0]);
            String data = in.readUTF();
            System.out.println("Received: "+ data) ;

        } catch (UnknownHostException e) {
            System.out.println("Socket error:" +e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF error:" +e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error:" +e.getMessage());
        }finally {
            if(s!=null) try {
                s.close();
            } catch (IOException e) {
                System.out.println("Close error:" +e.getMessage());
            }
        }
    }
}
