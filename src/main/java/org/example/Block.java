package org.example;

import java.util.List;

public class Block {
    public String hash;
    public String prevHash;
    public List<Transaction> transactions;
    public long timeStamp;
    public int nonce;

    public Block(List<Transaction> transactions, String prevHash, long timeStamp) {
        this.transactions = transactions;
        this.prevHash = prevHash;
        this.timeStamp = timeStamp;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + transactions.toString();
        return StringUtil.applySha256(dataToHash);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);

        for (Transaction transaction : transactions) {
            System.out.println("Processing transaction: " + transaction);
            if (transaction.smartContract != null) {
                boolean success = transaction.smartContract.execute(transaction);
                if (!success) {
                    System.out.println("Transaction failed: " + transaction);
                }
            } else {
                Ledger.updateBalance(transaction.sender, -transaction.value);
                Ledger.updateBalance(transaction.recipient, transaction.value);
                System.out.println("Regular transaction processed: " + transaction);
            }
        }
    }
}
