package com.bugsnag.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class DefaultDelivery implements Delivery {

    private static final int HTTP_REQUEST_FAILED = 0;
    private final Connectivity connectivity;
    private final Context androidContext;

    DefaultDelivery(Connectivity connectivity, Context androidContext) {
        this.connectivity = connectivity;
        this.androidContext = androidContext;
    }

    @Override
    public void deliver(@NonNull SessionTrackingPayload payload,
                        @NonNull Configuration config) throws DeliveryFailureException {
//        String endpoint = config.getSessionEndpoint();
//        int status = deliver(endpoint, payload, config.getSessionApiHeaders());
//
//        if (status != 202) {
//            Logger.warn("Session API request failed with status " + status, null);
//        } else {
//            Logger.info("Completed session tracking request");
//        }
    }

    @Override
    public void deliver(@NonNull Report report,
                        @NonNull Configuration config) throws DeliveryFailureException {
        String endpoint = config.getEndpoint();
        int status = deliver(endpoint, report, config.getErrorApiHeaders());

        if (status / 100 != 2) {
            Logger.warn("Error API request failed with status " + status, null);
        } else {
            Logger.info("Completed error API request");
        }
    }

    int deliver(String urlString,
                JsonStream.Streamable streamable,
                Map<String, String> headers) throws DeliveryFailureException {

        File outputDir = new File(androidContext.getCacheDir().getAbsolutePath(), "crashes");
        outputDir.mkdirs();
        Log.i("Bugsnag-g4g", "overriding crash delivery, instead of upload, save to folder" + outputDir);

        try {
            String name = "crash_" + System.currentTimeMillis() + ".json";
            File outputJsonFile = new File(outputDir, name);
            OutputStream out = new FileOutputStream(outputJsonFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
            JsonStream stream = new JsonStream(writer);
            streamable.toStream(stream);
            Log.i("Bugsnag-g4g", "saved bugsnag crash json to file: " + outputJsonFile);
            IOUtils.closeQuietly(stream);

            // saving to file succeeded
            return 200;

        } catch (IOException exception) {
            throw new DeliveryFailureException("IOException encountered in delivery", exception);
        } catch (Exception exception) {
            Logger.warn("Unexpected error delivering payload", exception);
            return HTTP_REQUEST_FAILED;
        }
    }

}
