package com.example.labo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private final DatabaseHelper dbHelper;

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
            do {
                lista.add(cursorToProducto(cursor));
            } while (cursor.moveToNext());
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
            do {
                lista.add(cursorToProducto(cursor));
            } while (cursor.moveToNext());
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
        return p;
    }
}