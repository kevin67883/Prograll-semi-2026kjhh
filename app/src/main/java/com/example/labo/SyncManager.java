package com.example.labo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import okhttp3.*;

public class SyncManager {

    private static final String TAG = "SyncManager";
    private static final MediaType JSON_TYPE = MediaType.get("application/json");
    private ProductoDAO dao;
    private OkHttpClient client;

    public interface SyncCallback {
        void onSuccess(String mensaje);
        void onError(String error);
    }

    public SyncManager(Context context) {
        this.dao = new ProductoDAO(context);
        this.client = new OkHttpClient();
    }

    public void verificarConexion(SyncCallback callback) {
        new Thread(() -> {
            try {
                String url = CouchDBHelper.getUrl();
                Log.e(TAG, "Conectando a: " + url);
                Log.e(TAG, "Credenciales: " + CouchDBHelper.getCredenciales());
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .build();
                Response response = client.newCall(request).execute();
                Log.e(TAG, "Código respuesta: " + response.code());
                Log.e(TAG, "Respuesta: " + response.body().string());
                boolean conectado = response.isSuccessful();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (conectado) callback.onSuccess("Conectado");
                    else callback.onError("Sin conexión");
                });
            } catch (Exception e) {
                Log.e(TAG, "Error de conexión: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Sin conexión"));
            }
        }).start();
    }

    public void descargarProductos(SyncCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(CouchDBHelper.getUrl() + "/_all_docs?include_docs=true")
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .build();
                Response response = client.newCall(request).execute();
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONArray rows = json.getJSONArray("rows");

                for (int i = 0; i < rows.length(); i++) {
                    JSONObject doc = rows.getJSONObject(i).getJSONObject("doc");
                    String id = doc.getString("_id");
                    if (id.startsWith("_design")) continue;

                    Producto p = new Producto();
                    p.setCodigo(doc.optString("codigo", ""));
                    p.setNombre(doc.optString("nombre", ""));
                    p.setMarca(doc.optString("marca", ""));
                    p.setTalla(doc.optString("talla", ""));
                    p.setPrecio(doc.optDouble("precio", 0));
                    p.setCosto(doc.optDouble("costo", 0));        // ← nuevo
                    p.setStock(doc.optInt("stock", 0));            // ← nuevo
                    p.setDescripcion(doc.optString("descripcion", ""));
                    p.setFotoPath(doc.optString("foto_path", ""));
                    p.setCouchId(id);
                    p.setSincronizado(true);

                    if (dao.buscarPorCouchId(id) == null) {
                        dao.insertar(p);
                    }
                }
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess("Productos descargados"));
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage()));
            }
        }).start();
    }

    public void subirProducto(Producto p, SyncCallback callback) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("codigo", p.getCodigo());
                json.put("nombre", p.getNombre());
                json.put("marca", p.getMarca());
                json.put("talla", p.getTalla());
                json.put("precio", p.getPrecio());
                json.put("costo", p.getCosto());                   // ← nuevo
                json.put("ganancia", p.getGanancia());             // ← nuevo (calculada)
                json.put("stock", p.getStock());                   // ← nuevo
                json.put("descripcion", p.getDescripcion());
                json.put("presentacion", "Unidad");
                json.put("foto_path", p.getFotoPath() != null ? p.getFotoPath() : "");

                RequestBody body = RequestBody.create(json.toString(), JSON_TYPE);
                Request request = new Request.Builder()
                        .url(CouchDBHelper.getUrl())
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                JSONObject respJson = new JSONObject(response.body().string());
                String couchId = respJson.optString("id", "");
                dao.actualizarCouchId(p.getId(), couchId);

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess("Subido correctamente"));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage()));
            }
        }).start();
    }

    public void actualizarProducto(Producto p, SyncCallback callback) {
        new Thread(() -> {
            try {
                Request getRequest = new Request.Builder()
                        .url(CouchDBHelper.getUrl() + "/" + p.getCouchId())
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .build();
                Response getResponse = client.newCall(getRequest).execute();
                JSONObject docActual = new JSONObject(getResponse.body().string());
                String rev = docActual.getString("_rev");

                JSONObject json = new JSONObject();
                json.put("_id", p.getCouchId());
                json.put("_rev", rev);
                json.put("codigo", p.getCodigo());
                json.put("nombre", p.getNombre());
                json.put("marca", p.getMarca());
                json.put("talla", p.getTalla());
                json.put("precio", p.getPrecio());
                json.put("costo", p.getCosto());                   // ← nuevo
                json.put("ganancia", p.getGanancia());             // ← nuevo (calculada)
                json.put("stock", p.getStock());                   // ← nuevo
                json.put("descripcion", p.getDescripcion());
                json.put("presentacion", "Unidad");
                json.put("foto_path", p.getFotoPath() != null ? p.getFotoPath() : "");

                RequestBody body = RequestBody.create(json.toString(), JSON_TYPE);
                Request request = new Request.Builder()
                        .url(CouchDBHelper.getUrl() + "/" + p.getCouchId())
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .put(body)
                        .build();
                client.newCall(request).execute();

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess("Actualizado en servidor"));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage()));
            }
        }).start();
    }

    public void eliminarProducto(Producto p, SyncCallback callback) {
        new Thread(() -> {
            try {
                Request getRequest = new Request.Builder()
                        .url(CouchDBHelper.getUrl() + "/" + p.getCouchId())
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .build();
                Response getResponse = client.newCall(getRequest).execute();
                JSONObject docActual = new JSONObject(getResponse.body().string());
                String rev = docActual.getString("_rev");

                Request request = new Request.Builder()
                        .url(CouchDBHelper.getUrl() + "/" + p.getCouchId() + "?rev=" + rev)
                        .header("Authorization", "Basic " + CouchDBHelper.getCredenciales())
                        .delete()
                        .build();
                client.newCall(request).execute();

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess("Eliminado del servidor"));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error: " + e.getMessage()));
            }
        }).start();
    }

    public void sincronizarPendientes(SyncCallback callback) {
        List<Producto> pendientes = dao.obtenerNoSincronizados();
        if (pendientes.isEmpty()) {
            callback.onSuccess("Todo sincronizado");
            return;
        }
        for (Producto p : pendientes) {
            subirProducto(p, new SyncCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    callback.onSuccess("Sincronizado: " + p.getNombre());
                }
                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        }
    }
}