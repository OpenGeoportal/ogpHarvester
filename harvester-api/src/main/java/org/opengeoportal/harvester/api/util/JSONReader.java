package org.opengeoportal.harvester.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {

    private static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(final String url)
            throws IOException, ParseException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = JSONReader.readAll(rd);
            final JSONParser jsonParser = new JSONParser();
            final JSONObject json = (JSONObject) jsonParser.parse(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

}