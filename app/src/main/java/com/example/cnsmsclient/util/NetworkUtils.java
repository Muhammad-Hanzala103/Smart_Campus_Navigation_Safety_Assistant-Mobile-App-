package com.example.cnsmsclient.util;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class NetworkUtils {

    public static String getErrorMessage(ResponseBody errorBody) {
        if (errorBody == null) {
            return "An unknown error occurred.";
        }
        try {
            String errorJson = errorBody.string();
            JSONObject jsonObject = new JSONObject(errorJson);
            // Assuming the server returns an error in the format {"message": "..."}
            return jsonObject.optString("message", "Could not parse server error.");
        } catch (Exception e) {
            return "An error occurred while parsing the server response.";
        }
    }
}
