package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.*;

public class P2PServer {
    private int port;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public P2PServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("P2P Server started on port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted from: " + socket.getInetAddress() + ":" + socket.getPort());
                executorService.execute(new P2PHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start P2P server on port: " + port);
        }
    }

    private class P2PHandler implements Runnable {
        private Socket socket;
        private Gson gson;

        public P2PHandler(Socket socket) {
            this.socket = socket;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(PublicKey.class, new PublicKeySerializer());
            gsonBuilder.registerTypeAdapter(PublicKey.class, new PublicKeyDeserializer());
            gsonBuilder.registerTypeAdapter(PublicKey.class, new PublicKeyInstanceCreator());
            gsonBuilder.registerTypeAdapter(PrivateKey.class, new PrivateKeySerializer());
            gsonBuilder.registerTypeAdapter(PrivateKey.class, new PrivateKeyDeserializer());
            gsonBuilder.registerTypeAdapter(SmartContract.class, new SmartContractDeserializer());
            gsonBuilder.registerTypeAdapter(SmartContract.class, new SmartContractSerializer());
            gsonBuilder.registerTypeAdapter(Block.class, new BlockDeserializer());
            this.gson = gsonBuilder.create();
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    handleRequest(message, writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleRequest(String message, PrintWriter writer) {
            try {
                System.out.println("Received message: " + message);

                if (message.equals("GET_BLOCKCHAIN")) {
                    writer.println(gson.toJson(Blockchain.blockchain));
                    System.out.println("Sent blockchain.");
                } else if (message.startsWith("NEW_BLOCK")) {
                    String blockJson = message.substring(10);
                    Block newBlock = gson.fromJson(blockJson, Block.class);
                    System.out.println("Received new block: " + blockJson);

                    synchronized (Blockchain.blockchain) {
                        if (Blockchain.isValidNewBlock(newBlock)) {
                            Blockchain.blockchain.add(newBlock);
                            System.out.println("Block added to blockchain.");
                        } else {
                            System.out.println("Invalid block received.");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
