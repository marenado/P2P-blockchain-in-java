package org.example;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SmartContractSerializer implements JsonSerializer<SmartContract> {
    @Override
    public JsonElement serialize(SmartContract src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = (JsonObject) context.serialize(src, src.getClass());
        jsonObject.addProperty("className", src.getClass().getName());
        return jsonObject;
    }
}
