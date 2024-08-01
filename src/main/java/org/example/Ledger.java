package org.example;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Ledger {
    private static Map<String, Float> balances = new HashMap<>();

    public static float getBalance(PublicKey key) {
        String keyString = StringUtil.getStringFromKey(key);
        return balances.getOrDefault(keyString, 0f);
    }

    public static void updateBalance(PublicKey key, float amount) {
        String keyString = StringUtil.getStringFromKey(key);
        balances.put(keyString, getBalance(key) + amount);
    }
}
