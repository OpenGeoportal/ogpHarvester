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

    private static String readAll(Reader rd) throws IOException {
      StringBuilder sb = new StringBuilder();
      int cp;
      while ((cp = rd.read()) != -1) {
        sb.append((char) cp);
      }
      return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, ParseException {
      InputStream is = new URL(url).openStream();
      try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        JSONParser jsonParser = new JSONParser();       
        JSONObject json = (JSONObject) jsonParser.parse(jsonText);
        return json;
      } finally {
        is.close();
      }
    }
    
}