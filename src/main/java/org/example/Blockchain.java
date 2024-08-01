package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    public static List<Block> blockchain = new ArrayList<>();
    public static int difficulty = 5;

    static {
        // Add a genesis block on initialization
        long genesisTimeStamp = System.currentTimeMillis();
        Block genesisBlock = new Block(new ArrayList<>(), "0", genesisTimeStamp);
        genesisBlock.mineBlock(difficulty);
        blockchain.add(genesisBlock);
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.prevHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            for (Transaction transaction : currentBlock.transactions) {
                if (!transaction.verifySignature()) {
                    System.out.println("Transaction signature invalid");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidNewBlock(Block newBlock) {
        if (newBlock == null) {
            System.out.println("Received block is null");
            return false;
        }

        if (newBlock.hash == null || newBlock.prevHash == null) {
            System.out.println("Block's hash or prevHash is null");
            return false;
        }

        Block latestBlock = blockchain.get(blockchain.size() - 1);
        if (!latestBlock.hash.equals(newBlock.prevHash)) {
            System.out.println("Previous hash does not match.");
            return false;
        }

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        if (!newBlock.hash.equals(newBlock.calculateHash())) {
            System.out.println("New block hash invalid");
            return false;
        }

        if (!newBlock.hash.substring(0, difficulty).equals(hashTarget)) {
            System.out.println("New block hasn't been mined");
            return false;
        }

        return true;
    }

    public static Block getLatestBlock() {
        return blockchain.isEmpty() ? null : blockchain.get(blockchain.size() - 1);
    }

    public static void saveBlockchain(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(blockchain);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadBlockchain(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            blockchain = (List<Block>) ois.readObject();
        }
    }

    public static boolean isValidChain(List<Block> chain) {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.prevHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            for (Transaction transaction : currentBlock.transactions) {
                if (!transaction.verifySignature()) {
                    System.out.println("Transaction signature invalid");
                    return false;
                }
            }
        }
        return true;
    }

    public static void synchronizeBlockchain(List<Block> newBlocks) {
        if (newBlocks.size() > blockchain.size() && isValidChain(newBlocks)) {
            blockchain = newBlocks;
            System.out.println("Blockchain synchronized successfully.");
        } else {
            System.out.println("Received blockchain is invalid or not longer than the current blockchain.");
        }
    }
}
