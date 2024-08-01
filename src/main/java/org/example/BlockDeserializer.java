package org.example;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class BlockDeserializer implements JsonDeserializer<Block> {

    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize transactions
        JsonArray transactionsArray = jsonObject.getAsJsonArray("transactions");
        List<Transaction> transactions = context.deserialize(transactionsArray, new com.google.gson.reflect.TypeToken<List<Transaction>>() {}.getType());

        // Deserialize prevHash
        String prevHash = jsonObject.get("prevHash").getAsString();

        // Deserialize timeStamp
        long timeStamp = jsonObject.get("timeStamp").getAsLong();

        // Create a new Block object
        return new Block(transactions, prevHash, timeStamp);
    }
}
