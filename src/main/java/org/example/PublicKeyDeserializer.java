package org.example;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyDeserializer implements JsonDeserializer<PublicKey> {
    @Override
    public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            String algorithm = jsonObject.get("algorithm").getAsString();
            String format = jsonObject.get("format").getAsString();
            byte[] encoded = Base64.getDecoder().decode(jsonObject.get("encoded").getAsString());

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize PublicKey", e);
        }
    }
}
