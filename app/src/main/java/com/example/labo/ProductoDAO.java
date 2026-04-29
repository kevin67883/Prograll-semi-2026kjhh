package com.example.labo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private DatabaseHelper dbHelper;

    public ProductoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertar(Producto p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CODIGO, p.getCodigo());
        cv.put(DatabaseHelper.COL_NOMBRE, p.getNombre());
        cv.put(DatabaseHelper.COL_MARCA, p.getMarca());
        cv.put(DatabaseHelper.COL_TALLA, p.getTalla());
        cv.put(DatabaseHelper.COL_PRECIO, p.getPrecio());
        cv.put(DatabaseHelper.COL_DESCRIPCION, p.getDescripcion());
        cv.put(DatabaseHelper.COL_FOTO, p.getFotoPath());
        cv.put(DatabaseHelper.COL_COUCH_ID, p.getCouchId() != null ? p.getCouchId() : "");
        cv.put(DatabaseHelper.COL_SINCRONIZADO, p.isSincronizado() ? 1 : 0);
        cv.put(DatabaseHelper.COL_COSTO, p.getCosto());   // ← nuevo
        cv.put(DatabaseHelper.COL_STOCK, p.getStock());   // ← nuevo
        // getGanancia() se calcula automáticamente, NO se guarda en SQLite
        long result = db.insert(DatabaseHelper.TABLE, null, cv);
        db.close();
        return result;
    }

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE, null, null,
                null, null, null, DatabaseHelper.COL_NOMBRE + " ASC");
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToProducto(cursor)); }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<Producto> buscar(String texto) {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = DatabaseHelper.COL_NOMBRE + " LIKE ? OR " +
                DatabaseHelper.COL_CODIGO + " LIKE ? OR " +
                DatabaseHelper.COL_MARCA + " LIKE ?";
        String[] args = {"%" + texto + "%", "%" + texto + "%", "%" + texto + "%"};
        Cursor cursor = db.query(DatabaseHelper.TABLE, null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToProducto(cursor)); }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int actualizar(Producto p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CODIGO, p.getCodigo());
        cv.put(DatabaseHelper.COL_NOMBRE, p.getNombre());
        cv.put(DatabaseHelper.COL_MARCA, p.getMarca());
        cv.put(DatabaseHelper.COL_TALLA, p.getTalla());
        cv.put(DatabaseHelper.COL_PRECIO, p.getPrecio());
        cv.put(DatabaseHelper.COL_DESCRIPCION, p.getDescripcion());
        cv.put(DatabaseHelper.COL_FOTO, p.getFotoPath());
        cv.put(DatabaseHelper.COL_COUCH_ID, p.getCouchId() != null ? p.getCouchId() : "");
        cv.put(DatabaseHelper.COL_SINCRONIZADO, p.isSincronizado() ? 1 : 0);
        cv.put(DatabaseHelper.COL_COSTO, p.getCosto());   // ← nuevo
        cv.put(DatabaseHelper.COL_STOCK, p.getStock());   // ← nuevo
        // getGanancia() se calcula automáticamente, NO se guarda en SQLite
        int rows = db.update(DatabaseHelper.TABLE, cv,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(p.getId())});
        db.close();
        return rows;
    }

    public int eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public Producto buscarPorCouchId(String couchId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE, null,
                DatabaseHelper.COL_COUCH_ID + "=?", new String[]{couchId},
                null, null, null);
        Producto p = null;
        if (cursor.moveToFirst()) p = cursorToProducto(cursor);
        cursor.close();
        db.close();
        return p;
    }

    public void actualizarCouchId(int id, String couchId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_COUCH_ID, couchId);
        cv.put(DatabaseHelper.COL_SINCRONIZADO, 1);
        db.update(DatabaseHelper.TABLE, cv,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Producto> obtenerNoSincronizados() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE, null,
                DatabaseHelper.COL_SINCRONIZADO + "=0", null, null, null, null);
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToProducto(cursor)); }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    private Producto cursorToProducto(Cursor c) {
        Producto p = new Producto();
        p.setId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        p.setCodigo(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CODIGO)));
        p.setNombre(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOMBRE)));
        p.setMarca(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_MARCA)));
        p.setTalla(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TALLA)));
        p.setPrecio(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRECIO)));
        p.setDescripcion(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPCION)));
        p.setFotoPath(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_FOTO)));
        p.setCouchId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_COUCH_ID)));
        p.setSincronizado(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_SINCRONIZADO)) == 1);
        p.setCosto(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_COSTO)));   // ← nuevo
        p.setStock(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_STOCK)));       // ← nuevo
        // p.getGanancia() se calcula solo con precio y costo, no necesita leerse
        return p;
    }
}