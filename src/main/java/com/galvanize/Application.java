package com.galvanize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Application {

    public static void main(String[] args) {
        Gson builder = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        JsonObject object = new JsonObject();

        System.getProperties().stringPropertyNames().iterator().forEachRemaining(propertyName ->
                object.addProperty(propertyName, System.getProperty(propertyName))
        );

        System.out.println(builder.toJson(object));
    }

}
