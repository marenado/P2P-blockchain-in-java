package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class P2PClient {
    private String host;
    private int port;
    private Gson gson;
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY_MS = 2000;

    public P2PClient(String host, int port) {
        this.host = host;
        this.port = port;
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

    public void sendBlock(Block block) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, port);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                String blockJson = gson.toJson(block);
                writer.println("NEW_BLOCK " + blockJson);
                System.out.println("Block sent to peer: " + block.hash);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to send block to " + host + ":" + port);
            }
        }).start();
    }

    public void requestBlockchain() {
        new Thread(() -> {
            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    System.out.println("Requesting blockchain from " + host + ":" + port);
                    try (Socket socket = new Socket(host, port);
                         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        writer.println("GET_BLOCKCHAIN");
                        String response;
                        StringBuilder blockchainJson = new StringBuilder();
                        while ((response = reader.readLine()) != null) {
                            blockchainJson.append(response);
                        }
                        List<Block> receivedBlockchain = gson.fromJson(blockchainJson.toString(), new TypeToken<List<Block>>() {}.getType());
                        if (receivedBlockchain != null && Blockchain.isValidChain(receivedBlockchain)) {
                            Blockchain.blockchain = receivedBlockchain;
                            System.out.println("Blockchain updated from peer.");
                        }
                        return;  // Success, exit the method
                    }
                } catch (IOException e) {
                    System.out.println("Connection attempt " + (i + 1) + " failed: " + e.getMessage());
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            System.out.println("Failed to connect to peer after " + MAX_RETRIES + " attempts.");
        }).start();
    }
}
