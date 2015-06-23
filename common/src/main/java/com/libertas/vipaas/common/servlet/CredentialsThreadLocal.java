package com.libertas.vipaas.common.servlet;

import org.json.simple.JSONObject;

public class CredentialsThreadLocal {
    private static final ThreadLocal<JSONObject> credentials = new ThreadLocal<JSONObject>();

    public static JSONObject getCredentials() {
        return credentials.get();
    }

    public static void setCredentials(final JSONObject myCredentials) {
        credentials.set(myCredentials);
    }
}