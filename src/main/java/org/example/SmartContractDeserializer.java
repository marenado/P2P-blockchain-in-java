package org.example;


import com.google.gson.*;

import java.lang.reflect.Type;

public class SmartContractDeserializer implements JsonDeserializer<SmartContract> {
    @Override
    public SmartContract deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get("className").getAsString();

        try {
            Class<?> clazz = Class.forName(className);
            return context.deserialize(jsonObject, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }
}
