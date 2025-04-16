package mg.itu.adapter;

import java.lang.reflect.Type;
import java.time.LocalTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Sérialisation : convertir LocalDate en chaîne de caractères (par exemple, "yyyy-MM-dd")
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Désérialisation : convertir une chaîne de caractères en LocalDate
        return LocalTime.parse(json.getAsString());
    }

}
