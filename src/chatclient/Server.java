/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ramon
 */
public class Server implements Runnable {

    private final Socket sock;
    private BufferedReader in;
    private PrintWriter out;

    private final ChatClient chatClient;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public Server(Socket s, ChatClient cc) {
        this.sock = s;
        this.chatClient = cc;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    @Override
    public void run() {
        try {
            this.in=new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.out = new PrintWriter(sock.getOutputStream(), true);
            takeMsg();
            this.sock.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Cierra la conexion
    public void closeConnection() {
        sendMsg("bye");
        try {
            this.sock.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Envía mensaje al servidor
    public void sendMsg(String msg) {
        out.println(msg);
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Recibe los mensajes desde el servidor y se los muestra al usuario
    private void takeMsg() {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    if (line.equals("bye")) {
                        done = true;
                    } else {
                        this.chatClient.showMsg(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            this.chatClient.setServer(null);
            this.chatClient.showMsg("Connection lost, reconnecting...");
        }
    }

}
