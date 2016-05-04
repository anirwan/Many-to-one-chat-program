import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String LOGIN = "LOGIN";
    private static final String LOGGEDIN = "LOGGEDIN";

    Frame frame;
    BufferedReader in;
    PrintWriter out;

    public Client() {
        frame = new Frame(null, "Admin", null, null);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }

    private void run() throws IOException {

        String serverAddress = frame.getServerAddress();
        Socket socket = new Socket(serverAddress, 9090);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        frame.setIncoming(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        frame.setOutgoing(new PrintWriter(socket.getOutputStream(), true));

        while (true) {
            String line = frame.in.readLine();
            if (line.equals(LOGIN)) {
                frame.out.println(frame.getLogin());
            } else if (line.startsWith(LOGGEDIN)) {
                frame.setEditable(true);
                String name = line.substring(9);
                frame.setTitle(name, "Admin");
                frame.setPrefix("MESSAGE ", name);
            } else if (line.startsWith("MESSAGE")) {
                frame.receiveText(line.substring(8) + "\n");
            }
        }
    }
}