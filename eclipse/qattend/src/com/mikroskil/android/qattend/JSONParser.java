package com.mikroskil.android.qattend;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url, String request) {

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>(1);
            param.add(new BasicNameValuePair("request", request));
            httpPost.setEntity(new UrlEncodedFormEntity(param, "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            is = httpResponse.getEntity().getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }

    public String sendJSONtoUrl(String url, String request, JSONObject json) {

        String result = "";

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>(1);
            param.add(new BasicNameValuePair("request", request));
            param.add(new BasicNameValuePair("json", json.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(param, "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            result  = EntityUtils.toString(httpResponse.getEntity());

//            is = httpResponse.getEntity().getContent();

//            if (is != null)
//                result  = convertInputStreamToString();
//            else
//                result = "Error";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

//    private static String convertInputStreamToString() {
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        String line = "";
//        String result = "";
//
//        try {
//            while((line = reader.readLine()) != null)
//                result += line;
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
}
