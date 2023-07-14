package com.company;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

// Server class
public class Server
{

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();
    public String initVector = "vectorul de init";



    public static void main(String[] args) throws IOException
    {
        String k1_ecb = "";
        String k2_cbc ="";
        String k3 = "";



        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);
        System.out.println("Ok, the KM server is up and running on port " + ss.getLocalPort() + " ...");
        //for Node A
        Socket client_1 = ss.accept();
        System.out.println("Client 1 request received : " + client_1);

        DataOutputStream outClient_1 = new DataOutputStream(client_1.getOutputStream());
        DataInputStream inClient_1 = new DataInputStream(client_1.getInputStream());

        System.out.println("Creating a new handler for this client...");

        // Create a new handler object for handling this request.
        ClientHandler mtch_1 = new ClientHandler(client_1,"client_1", inClient_1, outClient_1);

        // Create a new Thread with this object.
        Thread t_1 = new Thread(mtch_1);

        System.out.println("Adding this client to active client list");

        // add this client to active clients list
        ar.add(mtch_1);

        // start the thread.
        t_1.start();

        // for Node B
        Socket client_2 = ss.accept();
        System.out.println("Client 2 request received : " + client_2);

        DataOutputStream outClient_2 = new DataOutputStream(client_2.getOutputStream());
        DataInputStream inClient_2 = new DataInputStream(client_2.getInputStream());

        System.out.println("Creating a new handler for this client...");

        // Create a new handler object for handling this request.
        ClientHandler mtch_2 = new ClientHandler(client_2,"client_2", inClient_2, outClient_2);

        // Create a new Thread with this object.
        Thread t_2 = new Thread(mtch_2);

        System.out.println("Adding this client to active client list");

        // add this client to active clients list
        ar.add(mtch_2);

        // start the thread.
        t_2.start();


        }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    public static Boolean start = false;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                //System.out.println(this.name + ": " + received);

                if(received.equals("ECB")){
                    System.out.println("Sending the mode " + "ECB");
                    Server.ar.elementAt(0).dos.writeUTF("mode#ECB");
                    Server.ar.elementAt(1).dos.writeUTF("mode#ECB");

                    System.out.println("Sending the encrypted key to the clients");
                    String key_encrypted=AES.encryptECB(AES.k1_ecb,AES.k3);
                    Server.ar.elementAt(0).dos.writeUTF("key#"+key_encrypted);
                    Server.ar.elementAt(1).dos.writeUTF("key#"+key_encrypted);
                }

                if(received.equals("CBC")){
                    System.out.println("Sending the mode " + "CBC");
                    Server.ar.elementAt(0).dos.writeUTF("mode#ECB");
                    Server.ar.elementAt(1).dos.writeUTF("mode#CBC");


                    System.out.println("Sending the encrypted key to the clients");
                    String key_encrypted=AES.encryptCBC(AES.k1_ecb,AES.k3);
                    Server.ar.elementAt(0).dos.writeUTF("key#"+key_encrypted);
                    Server.ar.elementAt(1).dos.writeUTF("key#"+key_encrypted);
                }

                if(received.equals("start")){
                    Server.ar.elementAt(0).dos.writeUTF("start");
                    start= true;

                }

                if(start = true)
                    Server.ar.elementAt(1).dos.writeUTF(received);

                System.out.println("Received is: " + received);

                if(received.equals("STOP")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
