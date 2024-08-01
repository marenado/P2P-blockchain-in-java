package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.util.Base64;

public class PrivateKeySerializer implements JsonSerializer<PrivateKey> {
    @Override
    public JsonElement serialize(PrivateKey src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("algorithm", src.getAlgorithm());
        jsonObject.addProperty("format", src.getFormat());
        jsonObject.addProperty("encoded", Base64.getEncoder().encodeToString(src.getEncoded()));
        return jsonObject;
    }
}
