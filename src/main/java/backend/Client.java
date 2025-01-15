package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import backend.service.config.Authorization;

public class Client {
    private User user = new User();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public Client(Socket socket, String Username, String password){
         try{
            this.socket = socket;
            user.setUsername(Username);
            user.setPassword(password);
            bufferedReader  = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         }catch(IOException ioe){
            closeEverything(bufferedWriter, bufferedReader, socket);
         }
    }
    private void sendMsg(){
        try{
            bufferedWriter.write(user.getUsername());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner readInput = new Scanner(System.in);
            while (socket.isConnected()){
                String message= readInput.nextLine();
                
                bufferedWriter.write(user.getUsername() + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(bufferedWriter, bufferedReader, socket);
        }
    }

    private void ListenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(bufferedWriter, bufferedReader,socket);
                    }
                }
            }
        }
        ).start();
    }

    public void closeEverything( BufferedWriter bufferedWriter, BufferedReader bufferedReader, Socket socket) {
        try {
            if (bufferedWriter == null) bufferedWriter.close();
            if (bufferedReader == null) bufferedReader.close();
            if (socket == null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        System.out.println("Welcome to shephard studio!! Please Enter \"Sign in\" if you have an account or \"Sign up\" if you want to create an account. ");
        Scanner scn = new Scanner(System.in);
        String signIn_signUp = scn.nextLine();
        
        while (true) {
            if( signIn_signUp.toLowerCase() == "sign in" ){
                /*Authorization starts first, then it ask if user is as an account or user is a new member
                **
                */
                Authorization authorization = new Authorization();
                authorization.Start();
                break;
            }else if(signIn_signUp.toLowerCase() == "sign up"){
                //Creates a username and password for user feature coming soon
                break;
            }else{
                System.out.println("User input is not recognized!!!");
            }
        }

        System.out.println("Enter your username: ");
        String username = scn.nextLine();
        String password = scn.nextLine();
        //Before passing the password into the consrtuctor the password will be encrypted into the db and into the Client class
        Client client= new Client(new Socket("localhost", ServerSide.PORT_NUMBER), username, password);
        client.ListenForMessage();
        client.sendMsg();
    }
}
