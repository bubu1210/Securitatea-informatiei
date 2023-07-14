package com.company;

// Java implementation for multithreaded chat client
// Save file as ClientB.java

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientB
{
    final static int ServerPort = 1234;

    public static String msgWelcome = "--- Welcome to The Encrypt/Decrypt Program Client A --- \n Waiting for the chosen mode!.. ";

    public static Boolean start = false;
    public static String mode="";
    public static String key_encrypted;
    public static String key_decrypted;
    public  static String decryptedMessage="";
    public String initVector = "vectorul de init";

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        System.out.println(msgWelcome);

        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());



        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF(msg);

                        if(start == true)
                            dos.writeUTF("start");


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();

                        if(msg.contains("mode")) {
                            StringTokenizer st = new StringTokenizer(msg, "#");
                            String stt = st.nextToken();
                            mode = st.nextToken();
                            System.out.println("The mode chosen is: " + mode);
                        }
                        else
                            if(msg.contains("key")){
                                StringTokenizer st = new StringTokenizer(msg, "#");
                                String stt = st.nextToken();
                                key_encrypted = st.nextToken();
                                System.out.println("The encrypted key is: " + key_encrypted);
                                if(mode.equals("ECB")) {
                                    key_decrypted=AES.decryptECB(key_encrypted,AES.k3);
                                    System.out.println("The decrypted key is: " + key_decrypted);
                                }
                                if(mode.equals("CBC")) {
                                    key_decrypted = AES.decryptCBC(key_encrypted, AES.k3);
                                    System.out.println("The decrypted key is: " + key_decrypted);

                                }
                                System.out.println("Client 1 can start to send the blocks");
                                start =true;
                            }

                            if((start == true) && (!msg.contains("mode")) && (!msg.contains("key")) && (!msg.contains("ECB")) && (!msg.contains("CBC"))){
                                        if(mode.equals("ECB")) {
                                            System.out.println("The message encrypted is: " + msg);
                                                String decryptedPartMsg=AES.decryptECB(msg,key_decrypted);

                                                if(decryptedPartMsg.contains("#")==true){
                                                    StringTokenizer st = new StringTokenizer(decryptedPartMsg, "#");
                                                    String stt = st.nextToken();
                                                    //String sttt = st.nextToken();
                                                    decryptedMessage=decryptedMessage+stt;
                                                    System.out.println("Decrypted message is: " + decryptedMessage);
                                                }
                                                else{
                                                    decryptedMessage=decryptedMessage+decryptedPartMsg;
                                                    System.out.println("Decrypted message is: " + decryptedMessage);
                                                }


                                            }
                                    }

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}

