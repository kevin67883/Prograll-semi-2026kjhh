package com.example.labo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BuscarActivity extends AppCompatActivity {

    private ProductoDAO dao;
    private ProductoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        setTitle("Buscar Producto");

        dao = new ProductoDAO(this);
        RecyclerView rv = findViewById(R.id.recyclerBuscar);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductoAdapter(this, new ArrayList<>(),
                new ProductoAdapter.OnItemClickListener() {
                    @Override public void onEditar(Producto p) {}
                    @Override public void onEliminar(Producto p) {}
                });
        rv.setAdapter(adapter);

        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.actualizarLista(dao.buscar(s.toString()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}