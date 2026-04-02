package com.example.Lab5.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpCilent {
    public static HttpURLConnection openConnection(String method, String url) throws IOException{
        var connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8");
        connection.setRequestMethod(method);
        return connection;
    }

    public static byte[] readData(HttpURLConnection connection) throws IOException{
        if(connection.getResponseCode() == 200){
            var out = new ByteArrayOutputStream();
            var is = connection.getInputStream();
            var block = new byte[4*1024];
            while (true){
                int n = is.read(block);
                if(n <= 0){
                    break;
                }
                out.write(block,0,n);
            }
            connection.disconnect();
            return out.toByteArray();
        }
        connection.disconnect();
        throw new IOException("No response from server");
    }

    public static byte[] writeData(HttpURLConnection connection, byte[] data) throws IOException{
        connection.setDoOutput(true);
        connection.getOutputStream().write(data);
        return readData(connection);
    }
}
