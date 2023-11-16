import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClientUI extends JFrame {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final JTextArea chatArea;
    private final JTextField messageField;
    private String nickname;

    public ChatClientUI() throws IOException {
        socket = new Socket("localhost", 4321);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        setNickname();

        setTitle("Chat Client - " + nickname);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setVisible(true);

        startListening();
        messageField.addActionListener(e -> sendMessageOnEnter());
    }

    private void sendMessageOnEnter() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            writer.println(nickname + ": " + message);
            displayMessage("You: " + message);
            messageField.setText("");
        }
    }

    private void setNickname() {
        nickname = JOptionPane.showInputDialog(this, "Enter your nickname:");
        if (nickname == null || nickname.trim().isEmpty()) {
            setNickname();
        }
    }

    private void startListening() {
        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    displayMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            writer.println(nickname + ": " + message);
            displayMessage("You: " + message);
            messageField.setText("");
        }
    }

    private void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatClientUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
