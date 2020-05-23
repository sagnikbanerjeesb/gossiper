package com.sagnik.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class Util {
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    public static<T> T deSerialize(String serializedContent, Class<T> type) {
        return new Gson().fromJson(serializedContent, type);
    }

    public static<T> T deSerialize(String serializedContent, Type type) {
        return new Gson().fromJson(serializedContent, type);
    }
}
