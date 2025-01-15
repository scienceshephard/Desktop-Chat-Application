package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    private static ArrayList<ClientHandler> clientHandlers= new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private String clientName;
    private BufferedWriter bufferedWriter;
    
    public ClientHandler(Socket socket){
            try{
            this.socket= socket;
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientName = bufferedReader.readLine();
            clientHandlers.add(this);
            broacastMessage("Server: " + clientName+" has entered the chat!");
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }
    public void broacastMessage(String msg){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientName.equals(clientName)){
                    clientHandler.bufferedWriter.write(msg);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }
            }catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    
    @Override
    public void run() {
        String messageFromClient;
        try {
            while (socket.isConnected() ) {
                messageFromClient = bufferedReader.readLine();
            }
        }catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
        try {
            if (bufferedWriter == null) bufferedWriter.close();
            if (bufferedReader == null) bufferedReader.close();
            if (socket == null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void removeClientHandler(){
        clientHandlers.remove(this);
    }


}
