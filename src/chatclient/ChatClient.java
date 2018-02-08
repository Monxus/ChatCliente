/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ramon
 */
public class ChatClient extends JFrame implements ActionListener, KeyListener {

    private int PORT;
    private String HOST;
    private String username;

    private OutServer os;
    private Server server;

    private JFrame frameIni;
    private JTextField port;
    private JTextField host;
    private JTextField userText;
    private JButton startButton;

    private JTextArea chatArea;
    private JTextField chatText;
    private JButton sendButton;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public ChatClient() {
        this.frameInicial();
    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    public static void main(String[] args) {
        ChatClient cc = new ChatClient();
    }

    //Añade un servidor cuando se conecta a uno
    public void addServer(Socket serverSocket) {
        this.server = new Server(serverSocket, this);
        Thread t = new Thread(this.server);
        t.start();
    }

    //Muestra el mensaje en el area de chat
    public void showMsg(String msg) {
        this.chatArea.append("\n" + msg);
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Añade los componentes a la ventana principal
    private void addUIComponentes(Container panel) {
        GridBagConstraints c;
        c = new GridBagConstraints();

        JLabel label = new JLabel(HOST + ":" + PORT);
        label.setFont(new Font("Arial Black", 0, 14));

        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label, c);

        JLabel label2 = new JLabel(this.username);
        label2.setFont(new Font("Arial Black", 0, 14));

        c.gridx = 0;
        c.gridy = 1;

        panel.add(label2, c);

        this.chatArea = new JTextArea(40, 50);
        JScrollPane scrollPane = new JScrollPane(this.chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatArea.setEditable(false);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, c);

        this.chatText = new JTextField();
        this.chatText.addKeyListener(this);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 1.0d;
        panel.add(this.chatText, c);

        this.sendButton = new JButton("Send");
        this.sendButton.addActionListener(this);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.weightx = 0.2d;
        panel.add(this.sendButton, c);

    }

    //Al salir del chat cierra todas las conexiones
    private void closeConnection() {
        if (this.server != null) {
            this.server.closeConnection();
            this.os.closeConnection();
        } else {
            this.os.closeConnection();
        }
    }
    
    //Crea la interfaz principal
    private void crearVentana() {
        this.setTitle("2do DAM - Chat as "+this.username+". Conected to" + HOST + ":" + PORT);
        this.setLayout(new GridBagLayout());

        this.addUIComponentes(getContentPane());
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeConnection();
                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setVisible(true);
    }

    //Crea la interaz principal donde se piden los datos
    private void frameInicial() {
        frameIni = new JFrame();
        frameIni.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel label1 = new JLabel("Server PORT");
        label1.setFont(new Font("Arial Black", 0, 14));
        frameIni.add(label1);

        this.port = new JTextField();
        frameIni.add(this.port);

        JLabel label2 = new JLabel("Server HOST");
        label2.setFont(new Font("Arial Black", 0, 14));
        frameIni.add(label2);

        this.host = new JTextField();
        frameIni.add(this.host);

        JLabel label3 = new JLabel("Username");
        label3.setFont(new Font("Arial Black", 0, 14));
        frameIni.add(label3);

        this.userText = new JTextField();
        frameIni.add(this.userText);

        frameIni.add(new JPanel());

        this.startButton = new JButton("Start");
        this.startButton.addActionListener(this);
        frameIni.add(this.startButton);

        frameIni.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameIni.pack();
        frameIni.setResizable(false);
        frameIni.setVisible(true);
    }
    
    //Envía un mensaje al servidor
    private void sendMsg(String msg) {
        this.showMsg(msg);
        this.server.sendMsg(msg);
    }

    //Coge el texto del textfield para enviar el mensaje
    private void takeMsgTextField(){
        if (!"".equals(this.chatText.getText()) && this.server != null) {
            String msg = this.username + ": " + this.chatText.getText();
            this.chatText.setText("");
            this.sendMsg(msg);        
        } else {
            JOptionPane.showMessageDialog(this, "Wait to connect to a server to send messages");
        }
    }
    
    
    /* LISTENERS ------------------------------------------------------------ */
    @Override
    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals("Start")) {
            if (!(this.port.getText().equals("") || this.host.getText().equals("") || this.userText.getText().equals(""))) {
                PORT = Integer.parseInt(this.port.getText());
                HOST = this.host.getText();
                username = this.userText.getText();

                frameIni.setVisible(false);
                this.crearVentana();

                os = new OutServer(PORT, HOST, this);
                os.start();

            } else {
                JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos");
            }
        }
        if (str.equals("Send")) {
            this.takeMsgTextField();

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == this.chatText) {
            if (e.VK_ENTER == e.getKeyCode()) {
                this.takeMsgTextField();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
