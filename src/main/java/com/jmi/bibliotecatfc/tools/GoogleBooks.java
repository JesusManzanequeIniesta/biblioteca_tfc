package com.jmi.bibliotecatfc.tools;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;

public class GoogleBooks {

    public static final String url = "https://www.googleapis.com/books/v1/volumes";
   
    // Devuelve un objeto JSON con titulo y los autores
    // >> recodificar en java 7
    public static JSONObject buscaISBN(String isbn) throws IOException {
        try {
        	String postParams = "q=isbn:"+isbn;
            URL obj = new URL(url+"?"+postParams);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // Pongo la codificación del stream sino salen los acentos mal
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;
            StringBuffer respuesta = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                respuesta.append(inputLine);
            }
            in.close();
            String jsonString=respuesta.toString(); 
            // Tengo que parsear la respuesta
            JSONObject js = new JSONObject(jsonString);
            int totalItems = js.getInt("totalItems");
            if (totalItems!=1) {
            	// Debería lanzar una excepción
            	return null;
            }
            JSONObject volumeInfo=js.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
            String titulo=volumeInfo.getString("title");
            // Si tiene subtitulo se lo pongo entre paréntesis
            if (volumeInfo.has("subtitle")) titulo=titulo+" ( "+volumeInfo.getString("subtitle")+" )";
            // Puede producir error, hay que empezar con un search
            JSONArray autores;
            if (volumeInfo.has("authors")) autores=volumeInfo.getJSONArray("authors");
            else autores=new JSONArray("[\"\"]");
            JSONObject devolver=new JSONObject();
            devolver.put("titulo", titulo);
            devolver.put("autores", autores);
            return devolver;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}