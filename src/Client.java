import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {

    private static final String LOGIN = "LOGIN";
    private static final String LOGGEDIN = "LOGGEDIN";
    private static final String MESSAGE = "MESSAGE ";
    private static final String ADMIN = "Admin";

    Frame frame;
    BufferedReader in;
    PrintWriter out;

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public Client() {
        frame = new Frame(null, ADMIN, null, null);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }

    private void run() throws IOException {

        String serverAddress = "localhost";
        Socket socket = new Socket(serverAddress, 9090);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        frame.setIncoming(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        frame.setOutgoing(new PrintWriter(socket.getOutputStream(), true));

        while (true) {
            String line = frame.in.readLine();
            if (line.equals(LOGIN)) {
                frame.out.println(frame.getLogin());
                logger.info("Getting login credentials\n");
            } else if (line.startsWith(LOGGEDIN)) {
                frame.setEditable(true);
                String name = line.substring(9);
                frame.setTitle(name, ADMIN);
                frame.setPrefix(MESSAGE, name);
                logger.info("Successfully logged in\n");
            } else if (line.startsWith(MESSAGE)) {
                frame.receiveText(line.substring(8));
            }
        }
    }
}