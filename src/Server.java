import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.*;

/**
 * Created by anirwanchowdhury on 03/05/2016.
 */

public class Server {

    private static final String LOGIN = "LOGIN";
    private static final String LOGGEDIN = "LOGGEDIN ";
    private static final String ADMIN = "Admin";
    private static final String MESSAGE = "MESSAGE ";

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 9090;
    private static HashSet<String> names = new HashSet<String>();
    private static HashMap<String, Frame> frames = new HashMap<String, Frame>();

    public static void main(String[] args) throws Exception {
        logger.info("Server is running\n");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String[] string;
        private String name, password;
        private Socket socket;
        PrintWriter out;
        BufferedReader in;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {

                    String input;
                    out.println(LOGIN);
                    input = in.readLine();


                    if (input == null) {
                        return;
                    }

                    string = input.split(";");
                    name = string[0];
                    password = string[1];

                    synchronized (names) {
                        if (!names.contains(name)) {
                            if(checkLogin(name, password)) {
                                logger.info("Login successful for " + name + " from: " + socket.getInetAddress() + "\n");
                                names.add(name);
                                break;
                            }
                        }
                    }
                }

                out.println(LOGGEDIN + name);
                initializeGui(name, in, out);

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    } else if (input.startsWith(MESSAGE)) {
                        frames.get(name).receiveText(input.substring(8));
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                    if(frames.get(name) != null) {
                        frames.get(name).close();
                        frames.remove(name);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean checkLogin(String username, String password) {
        HashMap<String, String> users = new HashMap<String, String>();

        users.put("anir1", "asd");
        users.put("anir2", "asd");
        users.put("anir3", "asd");

        logger.info("Server received credentials: " + "\n" +
                "username: " + username + "\n" +
                "password: " + password + "\n");

        if(users.containsKey(username) && users.get(username).equals(password)) {
            return true;
        }

        logger.info("Login failed for username: " + username + "\n");
        return false;
    }

    private static void initializeGui(String name, BufferedReader in, PrintWriter out) {
        Frame frame = new Frame(ADMIN, name, in, out);
        frame.setPrefix(MESSAGE, ADMIN);
        frame.setEditable(true);
        frames.put(name, frame);
        frame.sendText("Greetings, " + name + ". How may I help you today?");
    }
}