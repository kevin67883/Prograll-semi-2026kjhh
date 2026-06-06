package com.example.labo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.smartfridgelite.databinding.ActivityMainBinding;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProductRepository repository;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        android.content.SharedPreferences prefs =
                getSharedPreferences("session", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "");
        if (!nombre.isEmpty()) {
            Toast.makeText(this, "¡Bienvenido " + nombre + "! 👋",
                    Toast.LENGTH_SHORT).show();
        }

        repository = new ProductRepository(getApplication());

        adapter = new ProductAdapter(
                product -> {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Eliminar producto")
                            .setMessage("¿Seguro que quieres eliminar \"" + product.name + "\"?")
                            .setPositiveButton("Sí, eliminar", (dialog, which) ->
                                    repository.delete(product))
                            .setNegativeButton("Cancelar", null)
                            .show();
                },
                product -> {
                    product.inShoppingList = true;
                    repository.update(product);
                },
                product -> {
                    Intent intent = new Intent(this, EditProductActivity.class);
                    intent.putExtra("id", product.id);
                    intent.putExtra("name", product.name);
                    intent.putExtra("category", product.category);
                    intent.putExtra("quantity", product.quantity);
                    intent.putExtra("expirationDate", product.expirationDate);
                    intent.putExtra("inShoppingList", product.inShoppingList);
                    startActivity(intent);
                }
        );

        binding.recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerProducts.setAdapter(adapter);

        repository.getAllProducts().observe(this, products -> {
            adapter.setProducts(products);
        });

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));

        binding.btnRecipes.setOnClickListener(v ->
                startActivity(new Intent(this, RecipesActivity.class)));

        binding.btnShopping.setOnClickListener(v ->
                startActivity(new Intent(this, ShoppingListActivity.class)));

        binding.btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        NotificationHelper.scheduleDaily(this);

        // Sincronizar con Firebase
        String userId = String.valueOf(prefs.getInt("id", 0));
        FirebaseManager.syncProducts(userId, new FirebaseManager.OnProductsSyncedListener() {
            @Override
            public void onSynced(java.util.List<Product> products) {
                android.util.Log.d("Firebase", "Sincronizado: " + products.size() + " productos");
            }
            @Override
            public void onError(String error) {
                android.util.Log.e("Firebase", "Error sync: " + error);
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkExpiringProducts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Product> expiring = repository.getExpiringSoonSync();
            for (Product p : expiring) {
                NotificationHelper.notifyProductExpiringSoon(this, p);
            }
        });
    }
}