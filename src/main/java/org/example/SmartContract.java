package org.example;

import java.io.Serializable;

public abstract class SmartContract implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract boolean execute(Transaction transaction);

    public static class TransferContract extends SmartContract {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean execute(Transaction transaction) {
            System.out.println("Executing transaction: " + transaction);
            // Check if sender has enough balance
            if (Ledger.getBalance(transaction.sender) < transaction.value) {
                System.out.println("Insufficient balance for transaction: " + transaction);
                return false;
            }

            // Deduct the amount from sender's balance
            Ledger.updateBalance(transaction.sender, -transaction.value);
            System.out.println("Deducted " + transaction.value + " from " + StringUtil.getStringFromKey(transaction.sender));

            // Add the amount to recipient's balance
            Ledger.updateBalance(transaction.recipient, transaction.value);
            System.out.println("Added " + transaction.value + " to " + StringUtil.getStringFromKey(transaction.recipient));

            System.out.println("Transaction executed successfully: " + transaction);
            return true;
        }
    }
}
