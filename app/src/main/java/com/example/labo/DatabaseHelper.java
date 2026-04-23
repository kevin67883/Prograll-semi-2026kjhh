package com.example.labo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tienda_ropa.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE = "productos";
    public static final String COL_ID = "id";
    public static final String COL_CODIGO = "codigo";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_MARCA = "marca";
    public static final String COL_TALLA = "talla";
    public static final String COL_PRECIO = "precio";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_FOTO = "foto_path";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CODIGO + " TEXT UNIQUE NOT NULL, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_MARCA + " TEXT, " +
                COL_TALLA + " TEXT, " +
                COL_PRECIO + " REAL, " +
                COL_DESCRIPCION + " TEXT, " +
                COL_FOTO + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}