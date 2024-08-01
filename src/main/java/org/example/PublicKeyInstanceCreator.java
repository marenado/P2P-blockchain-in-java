package org.example;

import com.google.gson.InstanceCreator;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyInstanceCreator implements InstanceCreator<PublicKey> {
    @Override
    public PublicKey createInstance(java.lang.reflect.Type type) {
        try {

            //a dummy key for demonstration purposes
            String dummyKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMZC1EvkFgMGv6lsuK1i2A0z4X8nHcX7";
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(dummyKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PublicKey instance", e);
        }
    }
}
