import org.jgroups.JChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

// From tutorial: http://jgroups.org/tutorial/
public class SimpleChat extends ReceiverAdapter {

    JChannel channel;
    static String username;

    public static void main(String[] args) throws Exception {
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
        System.out.println("\nView: " +view +"\n");
    }

    public void receive(Message msg) {
        if(msg.getSrc().equals(channel.getAddress())) return;
        System.out.println("\t\t\t" +msg.getSrc() +" " +msg.getObject());
    }
}
