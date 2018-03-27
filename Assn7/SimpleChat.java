import org.jgroups.JChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

// Adapted from tutorial: http://jgroups.org/tutorial/
public class SimpleChat extends ReceiverAdapter {

    JChannel channel;
    static String username;

    public static void main(String[] args) throws Exception {

        // Uses first argument as username if it's present, otherwise uses system username
        if (args.length != 0)
            username = args[0];
        else
            username = System.getProperty("user.name", "n/a");

        new SimpleChat().start();
    }

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.flush();
                String line = in.readLine();
                String quitCheck = line.toLowerCase();
                // Check for exit commands
                if(quitCheck.startsWith("quit") || quitCheck.startsWith("exit"))
                    break;
                line = "[" +username +"]: " + line;
                Message msg = new Message(null, null, line);
                channel.send(msg);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void viewAccepted(View view) {
        // Prints whenever someone enters or leaves the group chat
        System.out.println("\nView: " +view +"\n");
    }

    public void receive(Message msg) {
        // Gets and prints messages from others
        if(msg.getSrc().equals(channel.getAddress())) return;
        System.out.println("\t\t\t" +msg.getSrc() +" " +msg.getObject());
    }
}
