package com.crilu.gothandroid.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String getFileContents(final File file) throws IOException {
        return getFileContents(file, false);
    }

    public static String getFileContents(final File file, boolean appendBrAtEndOfLine) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        boolean done = false;

        while (!done) {
            final String line = reader.readLine();
            done = (line == null);

            if (line != null) {
                stringBuilder.append(line);
                if (appendBrAtEndOfLine) {
                    stringBuilder.append("<br>");
                }
            }
        }

        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }
}