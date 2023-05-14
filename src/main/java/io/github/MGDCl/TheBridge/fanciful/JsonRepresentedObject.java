package io.github.MGDCl.TheBridge.fanciful;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

interface JsonRepresentedObject {

    public void writeJson(JsonWriter writer) throws IOException;

}
