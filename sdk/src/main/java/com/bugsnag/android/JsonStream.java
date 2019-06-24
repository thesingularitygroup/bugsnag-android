package com.bugsnag.android;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class JsonStream extends JsonWriter {

    private final ObjectJsonStreamer objectJsonStreamer;

    public interface Streamable {
        void toStream(@NonNull JsonStream stream) throws IOException;
    }

    private final Writer out;

    /**
     * Constructs a JSONStream
     *
     * @param out the writer
     */
    public JsonStream(@NonNull Writer out) {
        super(out);
        setSerializeNulls(false);
        this.out = out;
        objectJsonStreamer = new ObjectJsonStreamer();
    }

    /**
     * Allow chaining name().value()
     *
     * @param name the name
     */
    @NonNull
    public JsonStream name(@Nullable String name) throws IOException {
        super.name(name);
        Logger.warn("Name being written to stream: " + name);
        return this;
    }

    /**
     * This gives the Streamable the JsonStream instance and
     * allows lets it write itself into the stream.
     */
    public void value(@Nullable Streamable streamable) throws IOException {
        Logger.warn("Streamable value being written to stream");
        if (streamable == null) {
            nullValue();
            return;
        }
        streamable.toStream(this);
    }

    /**
     * Serialises an arbitrary object as JSON, handling primitive types as well as
     * Collections, Maps, and arrays.
     */
    public void value(@NonNull Object object) throws IOException {
        Logger.warn("Object being written to stream");
        objectJsonStreamer.objectToStream(object, this);
    }

    /**
     * Writes a File (its content) into the stream
     */
    public void value(@NonNull File file) throws IOException {
        Logger.warn("File being written to stream");
        if (file == null || file.length() <= 0) {
            return;
        }

        super.flush();
        beforeValue(); // add comma if in array

        // Copy the file contents onto the stream
        Reader input = null;
        try {
            Logger.warn("Attempting to write file to stream");
            FileInputStream fis = new FileInputStream(file);
            input = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            IOUtils.copy(input, out);
        } catch (Exception exception) {
            Logger.warn("Exception occurred while writing to stream: ", exception);
        } finally {
            Logger.warn("Closing stream");
            IOUtils.closeQuietly(input);
        }

        out.flush();
    }
}
