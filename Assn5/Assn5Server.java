import java.net.*;
import java.io.*;

public class Assn5Server {
    public static void main (String args[]) {
        try {
            // Getting connection to client
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        } catch(IOException e) {
            System.out.println("Listen error:" +e.getMessage());
        }
    }
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out = new DataOutputStream( clientSocket.getOutputStream());
            this.start();
        } catch(IOException e) {
            System.out.println("Connection error:" +e.getMessage());
        }
    }

    public void run(){
        try {
            // Getting message from client
            String data = in.readUTF();
            System.out.println("Received message \"" +data +"\". Sending reverse.");

            // Reversing the string
            char[] dataArray = data.toCharArray();
            for(int i = 0; i < dataArray.length/2; i++){
                char temp = dataArray[i];
                dataArray[i] = dataArray[dataArray.length - 1 - i];
                dataArray[dataArray.length - 1 - i] = temp;
            }
            String newString = new String(dataArray);

            // Replying to client with reversed string
            out.writeUTF(newString);
        } catch(EOFException e) {
            System.out.println("EOF error:" +e.getMessage());
        } catch(IOException e) {
            System.out.println("IO error:" +e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Close error:" +e.getMessage());
            }
        }
    }
}
