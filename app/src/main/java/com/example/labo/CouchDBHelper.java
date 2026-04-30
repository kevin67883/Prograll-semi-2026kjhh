package com.example.labo;

public class CouchDBHelper {
    private static final String BASE_URL = "http://192.168.1.7:5984";
    private static final String DATABASE = "kevin";
    private static final String USUARIO = "KevinCampos";
    private static final String PASSWORD = "kevin2026";

    public static String getUrl() {
        return BASE_URL + "/" + DATABASE;
    }

    public static String getVista() {
        return getUrl() + "/_design/osiel/_view/osiel";
    }

    public static String getCredenciales() {
        String credenciales = USUARIO + ":" + PASSWORD;
        return android.util.Base64.encodeToString(
                credenciales.getBytes(), android.util.Base64.NO_WRAP);
    }
}