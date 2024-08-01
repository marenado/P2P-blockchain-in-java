package org.example;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static Wallet walletA;
    public static Wallet walletB;
    public static Scanner scanner = new Scanner(System.in);
    public static P2PServer p2pServer;
    public static ArrayList<P2PClient> p2pClients = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Main <port> <peer_port1> <peer_port2> ...");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String[] peerPorts = new String[args.length - 1];
        System.arraycopy(args, 1, peerPorts, 0, args.length - 1);

        // Setup Bouncy Castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        // Create wallets
        walletA = new Wallet();
        walletB = new Wallet();

        // Initialize balances
        Ledger.updateBalance(walletA.getPublicKey(), 100f);
        Ledger.updateBalance(walletB.getPublicKey(), 50f);

        // Start P2P server
        p2pServer = new P2PServer(port);
        new Thread(() -> p2pServer.start()).start();

        // Connect to peers
        for (String peerPort : peerPorts) {
            P2PClient client = new P2PClient("localhost", Integer.parseInt(peerPort));
            p2pClients.add(client);
            client.requestBlockchain(); // Request blockchain from peers
        }

        // Interactive CLI
        while (true) {
            System.out.println("1. Create Regular Transaction");
            System.out.println("2. Create Smart Contract Transaction");
            System.out.println("3. Mine Block");
            System.out.println("4. Validate Blockchain");
            System.out.println("5. Save Blockchain");
            System.out.println("6. Load Blockchain");
            System.out.println("7. Print Blockchain");
            System.out.println("8. Print Balances");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    createTransaction(false);
                    break;
                case 2:
                    createTransaction(true);
                    break;
                case 3:
                    mineBlock();
                    break;
                case 4:
                    System.out.println("\nBlockchain is Valid: " + Blockchain.isChainValid());
                    break;
                case 5:
                    saveBlockchain();
                    break;
                case 6:
                    loadBlockchain();
                    break;
                case 7:
                    printBlockchain();
                    break;
                case 8:
                    printBalances();
                    break;
                case 9:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private static void createTransaction(boolean isSmartContract) {
        System.out.print("Enter sender (A/B): ");
        String sender = scanner.nextLine().toUpperCase();
        System.out.print("Enter recipient (A/B): ");
        String recipient = scanner.nextLine().toUpperCase();
        System.out.print("Enter value: ");
        float value = scanner.nextFloat();
        scanner.nextLine();  // Consume newline

        Wallet senderWallet = sender.equals("A") ? walletA : walletB;
        Wallet recipientWallet = recipient.equals("A") ? walletA : walletB;

        Transaction transaction;
        if (isSmartContract) {
            transaction = new Transaction(new SmartContract.TransferContract(), senderWallet.getPublicKey(), recipientWallet.getPublicKey(), value);
        } else {
            transaction = new Transaction(senderWallet.getPublicKey(), recipientWallet.getPublicKey(), value);
        }
        transaction.generateSignature(senderWallet.getPrivateKey());

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        long currentTimeStamp = System.currentTimeMillis();
        Block newBlock = new Block(transactions, Blockchain.blockchain.isEmpty() ? "0" : Blockchain.getLatestBlock().hash, currentTimeStamp);
        Blockchain.blockchain.add(newBlock);

        // Broadcast the new block to peers
        for (P2PClient client : p2pClients) {
            client.sendBlock(newBlock);
        }
    }

    private static void mineBlock() {
        if (!Blockchain.blockchain.isEmpty()) {
            Block lastBlock = Blockchain.blockchain.get(Blockchain.blockchain.size() - 1);
            lastBlock.mineBlock(Blockchain.difficulty);
        }
    }

    private static void saveBlockchain() {
        try {
            Blockchain.saveBlockchain("blockchain.dat");
            System.out.println("Blockchain saved successfully.");
        } catch (Exception e) {
            System.out.println("Failed to save blockchain: " + e.getMessage());
        }
    }

    private static void loadBlockchain() {
        try {
            Blockchain.loadBlockchain("blockchain.dat");
            System.out.println("Blockchain loaded successfully.");
        } catch (Exception e) {
            System.out.println("Failed to load blockchain: " + e.getMessage());
        }
    }

    private static void printBlockchain() {
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(Blockchain.blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    private static void printBalances() {
        System.out.println("Balance of Wallet A: " + Ledger.getBalance(walletA.getPublicKey()));
        System.out.println("Balance of Wallet B: " + Ledger.getBalance(walletB.getPublicKey()));
    }
}
