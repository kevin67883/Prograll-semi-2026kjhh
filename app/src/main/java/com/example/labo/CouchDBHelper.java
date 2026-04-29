package com.example.labo;

public class CouchDBHelper {

    private static final String BASE_URL = "http://192.168.1.5:5984";
    private static final String DATABASE = "david";
    private static final String USUARIO = "kevin2026";
    private static final String PASSWORD = "KevinCampos";

    public static String getUrl() {
        return BASE_URL + "/" + DATABASE;
    }

    public static String getCredenciales() {
        String credenciales = USUARIO + ":" + PASSWORD;
        return android.util.Base64.encodeToString(
                credenciales.getBytes(), android.util.Base64.NO_WRAP);
    }
}