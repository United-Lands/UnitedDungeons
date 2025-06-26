package org.unitedlands.utils.serializers;

import org.bukkit.util.BoundingBox;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BoundingBoxSerializer implements JsonSerializer<BoundingBox> {
    public JsonElement serialize(BoundingBox src, Type typeOfSrc, JsonSerializationContext context) {

        var jsonBBox = new JsonObject();

        jsonBBox.addProperty("minX", src.getMinX());
        jsonBBox.addProperty("minY", src.getMinY());
        jsonBBox.addProperty("minZ", src.getMinZ());
        jsonBBox.addProperty("maxX", src.getMaxX());
        jsonBBox.addProperty("maxY", src.getMaxY());
        jsonBBox.addProperty("maxZ", src.getMaxZ());

        return jsonBBox;
    }
}
