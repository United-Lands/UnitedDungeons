package org.unitedlands.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.unitedlands.utils.serializers.BoundingBoxDeserializer;
import org.unitedlands.utils.serializers.BoundingBoxSerializer;
import org.unitedlands.utils.serializers.LocationDeserializer;
import org.unitedlands.utils.serializers.LocationSerializer;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(Location.class, new LocationDeserializer())
            .registerTypeAdapter(BoundingBox.class, new BoundingBoxSerializer())
            .registerTypeAdapter(BoundingBox.class, new BoundingBoxDeserializer())
            .create();

    public static boolean saveObjectToFile(Object obj, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(obj, writer);
            Logger.log("JSON written.");
            return true;
        } catch (IOException e) {
            throw e;
        }
    }

    public static <T> T loadObjectFromFile(File file, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw e;
        }
    }
}