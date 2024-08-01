package org.example;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;
    public SmartContract smartContract;

    // Constructor for regular transactions
    public Transaction(PublicKey sender, PublicKey recipient, float value) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
    }

    // Constructor for smart contract transactions
    public Transaction(SmartContract smartContract, PublicKey sender, PublicKey recipient, float value) {
        this.smartContract = smartContract;
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    @Override
    public String toString() {
        return "{ sender: " + StringUtil.getStringFromKey(sender) + ", recipient: " + StringUtil.getStringFromKey(recipient) + ", value: " + value + ", contract: " + (smartContract != null) + " }";
    }
}
