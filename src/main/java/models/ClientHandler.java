package models;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = bufferedReader.readLine();
            clientHandlers.add(this);
            breadCastMessage("SERVER" + clientName + " has joined the chat");
        } catch (Exception e) {
            classEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                breadCastMessage(clientName + ": " + messageFromClient);
            } catch (Exception e) {
                classEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
        public void breadCastMessage(String messageToClient) {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (clientHandler.socket.isConnected()) {
                        clientHandler.bufferedWriter.write(messageToClient);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (Exception e) {
                    classEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
                }
            }
        }
        public void reserveClientHandler() {
            clientHandlers.remove(this);
            breadCastMessage("SERVER" + clientName + " has left the chat");
        }
        public void classEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
