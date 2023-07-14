package com.company;

// Java implementation for multithreaded chat client
// Save file as ClientA.java

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientA
{
    final static int ServerPort = 1234;

    public static String msgWelcome = "--- Welcome to The Encrypt/Decrypt Program Client A --- \n Please select the mode of encryption \n ECB \n CBC\n then press ENTER ; ";

    public static Boolean start = false;
    public static String mode="";
    public static String key_encrypted;
    public static String key_decrypted;
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

                    if(msg.equals("ECB")){
                        try {
                            dos.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(msg.equals("CBC")){
                        try {
                            dos.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                    if((!msg.contains("mode")) && (!msg.contains("key")) && (!msg.contains("ECB")) && (!msg.contains("CBC"))) {
                        if (mode.equals("ECB")) {
                                for (int i = 0; i < msg.length(); i += 16) {
                                    String msgg = msg.substring(i, Math.min(i + 16, msg.length()));
                                    String sendEncryptedMsg;

                                    if (msgg.length() < 16) {
                                            String repeated = new String(new char[16-msgg.length()]).replace("\0","#");
                                            System.out.println("The message to be encrypted: " + msgg+repeated);
                                            sendEncryptedMsg = AES.encryptECB(msgg+repeated, key_decrypted);
                                            System.out.println("The message encrypted is: " + sendEncryptedMsg);
                                            try {
                                                dos.writeUTF(sendEncryptedMsg);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                    } else {
                                        System.out.println("The message to be encrypted: " + msgg);
                                        sendEncryptedMsg = AES.encryptECB(msgg, key_decrypted);
                                        System.out.println("The message encrypted is: " + sendEncryptedMsg);
                                        try {
                                            dos.writeUTF(sendEncryptedMsg);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                            }
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
                                    String key_decrypted = AES.decryptCBC(key_encrypted, AES.k3);
                                    System.out.println("The decrypted key is: " + key_decrypted);
                                }

                            }
                            if(msg.contains("start")){
                                        start=true;
                                }

                        //System.out.println(msg);
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

