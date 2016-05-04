import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Created by anirwanchowdhury on 03/05/2016.
 */
public class Frame {

    protected String name;
    protected PrintWriter out;
    protected BufferedReader in;
    protected JFrame frame;
    protected JTextField textField;
    protected JTextArea textArea;
    protected String prefix;

    private static final Logger logger = Logger.getLogger(Server.class.getName());


    public Frame(String from, String to, BufferedReader in, PrintWriter out) {
        this.name = from;
        this.frame = new JFrame("Chatter :: " + from + " --> " + to);
        this.textField = new JTextField(60);
        this.textArea = new JTextArea(10, 60);

        this.textField.setEditable(false);
        this.textArea.setEditable(false);
        this.frame.getContentPane().add(textField, "North");
        this.frame.getContentPane().add(new JScrollPane(textArea), "Center");
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);

        logger.info("New GUI initialized for " + this.name + "\n");

        setIncoming(in);
        setOutgoing(out);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendText(getText());
                textField.setText("");
            }
        });
    }

    public void setPrefix(String prefix, String name) {
        this.name = name;
        this.prefix = prefix + name + ": ";
        logger.info("Prefix for frame[" + this.name + "]: " + this.prefix + "\n");
    }

    public String getText() {
        return this.textField.getText();
    }

    public void sendText(String message) {
        this.out.println(this.prefix + message);
        this.textArea.append(this.name + ": " + message + "\n");
        logger.info("Frame["+ this.name + "] sent message: " + message + "\n");
    }

    public void receiveText(String message) {
        this.textArea.append(message + "\n");
        logger.info("Frame["+ this.name + "] received message: " + message + "\n");
    }

//    public String getServerAddress() {
//        return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to the Chatter", JOptionPane.QUESTION_MESSAGE);
//    }

    public void setEditable(boolean bool) {
        this.textField.setEditable(bool);
    }

    public void setTitle(String from, String to) {
        this.frame.setTitle("Chatter :: " + from + " --> " + to);
    }

    public String getLogin() {
        String username, password;
        username = JOptionPane.showInputDialog(frame, "Enter username:", "Login", JOptionPane.PLAIN_MESSAGE);
        password = JOptionPane.showInputDialog(frame, "Enter password:", "Login", JOptionPane.PLAIN_MESSAGE);
        return username + ";" + password;
    }

    public void setOutgoing(PrintWriter out) {
        this.out = out;
    }

    public void setIncoming(BufferedReader in) {
        this.in = in;
    }

    public void close() {
        this.frame.setVisible(false);
        this.frame.dispose();
        logger.info("Frame [" + this.name + "] closed\n");
    }
}
