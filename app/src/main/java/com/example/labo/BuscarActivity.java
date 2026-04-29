package com.example.labo;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
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
                    @Override
                    public void onEditar(Producto p) {
                        Intent i = new Intent(BuscarActivity.this, EditarActivity.class);
                        i.putExtra("id",          p.getId());
                        i.putExtra("codigo",      p.getCodigo());
                        i.putExtra("nombre",      p.getNombre());
                        i.putExtra("marca",       p.getMarca());
                        i.putExtra("talla",       p.getTalla());
                        i.putExtra("precio",      p.getPrecio());
                        i.putExtra("costo",       p.getCosto());
                        i.putExtra("stock",       p.getStock());
                        i.putExtra("descripcion", p.getDescripcion());
                        i.putExtra("foto",        p.getFotoPath());
                        i.putExtra("couchId",     p.getCouchId());
                        startActivity(i);
                    }

                    @Override
                    public void onEliminar(Producto p) {
                        new androidx.appcompat.app.AlertDialog.Builder(BuscarActivity.this)
                                .setTitle("Eliminar")
                                .setMessage("¿Eliminar " + p.getNombre() + "?")
                                .setPositiveButton("Sí", (d, w) -> {
                                    dao.eliminar(p.getId());
                                    if (p.getCouchId() != null && !p.getCouchId().isEmpty()) {
                                        new SyncManager(BuscarActivity.this).eliminarProducto(p,
                                                new SyncManager.SyncCallback() {
                                                    @Override public void onSuccess(String msg) {}
                                                    @Override public void onError(String e) {}
                                                });
                                    }
                                    adapter.actualizarLista(dao.buscar(
                                            ((EditText) findViewById(R.id.etBuscar))
                                                    .getText().toString()
                                    ));
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
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