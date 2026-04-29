package com.example.labo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductoDAO dao;
    private SyncManager syncManager;
    private ProductoAdapter adapter;
    private List<Producto> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dao = new ProductoDAO(this);
        syncManager = new SyncManager(this);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAgregar);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AgregarActivity.class)));

        sincronizarAlInicio();
        cargarLista();
    }

    private void sincronizarAlInicio() {
        syncManager.verificarConexion(new SyncManager.SyncCallback() {
            @Override
            public void onSuccess(String mensaje) {
                syncManager.descargarProductos(new SyncManager.SyncCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        cargarLista();
                        syncManager.sincronizarPendientes(new SyncManager.SyncCallback() {
                            @Override
                            public void onSuccess(String m) { cargarLista(); }
                            @Override
                            public void onError(String e) {}
                        });
                    }
                    @Override
                    public void onError(String error) {}
                });
            }
            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this,
                        "Modo offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarLista() {
        lista = dao.obtenerTodos();
        if (adapter == null) {
            adapter = new ProductoAdapter(this, lista,
                    new ProductoAdapter.OnItemClickListener() {
                        @Override
                        public void onEditar(Producto p) {
                            Intent i = new Intent(MainActivity.this, EditarActivity.class);
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
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Eliminar")
                                    .setMessage("¿Eliminar " + p.getNombre() + "?")
                                    .setPositiveButton("Sí", (d, w) -> {
                                        dao.eliminar(p.getId());
                                        if (p.getCouchId() != null && !p.getCouchId().isEmpty()) {
                                            syncManager.eliminarProducto(p,
                                                    new SyncManager.SyncCallback() {
                                                        @Override public void onSuccess(String msg) {}
                                                        @Override public void onError(String e) {}
                                                    });
                                        }
                                        cargarLista();
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }
                    });
            RecyclerView rv = findViewById(R.id.recyclerView);
            rv.setAdapter(adapter);
        } else {
            adapter.actualizarLista(lista);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_buscar) {
            startActivity(new Intent(this, BuscarActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}