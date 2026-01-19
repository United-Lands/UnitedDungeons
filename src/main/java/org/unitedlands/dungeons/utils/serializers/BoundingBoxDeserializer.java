package org.unitedlands.dungeons.utils.serializers;

import java.lang.reflect.Type;

import org.bukkit.util.BoundingBox;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class BoundingBoxDeserializer implements JsonDeserializer<BoundingBox> {
    public BoundingBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        var jsonObj = json.getAsJsonObject();

        var minX = jsonObj.get("minX").getAsDouble();
        var minY = jsonObj.get("minY").getAsDouble();
        var minZ = jsonObj.get("minZ").getAsDouble();
        var maxX = jsonObj.get("maxX").getAsDouble();
        var maxY = jsonObj.get("maxY").getAsDouble();
        var maxZ = jsonObj.get("maxZ").getAsDouble();

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
