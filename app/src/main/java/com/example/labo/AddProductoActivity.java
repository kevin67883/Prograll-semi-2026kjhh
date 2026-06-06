package com.example.labo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartfridgelite.databinding.ActivityAddProductBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private ProductRepository repository;
    private Calendar selectedDate = Calendar.getInstance();
    private boolean dateSelected = false;
    private String selectedCategory = "";

    // Lista de categorías
    private final String[] categories = {
            "Selecciona una categoría",
            "🥛 Lácteo",
            "🥩 Carne",
            "🐟 Pescado y Mariscos",
            "🥦 Verdura",
            "🍎 Fruta",
            "🥚 Huevos",
            "🍞 Pan y Cereales",
            "🥫 Enlatados",
            "🧂 Condimentos",
            "🧃 Bebidas",
            "🍰 Postres",
            "❄️ Congelados",
            "🌾 Granos y Legumbres",
            "🫙 Conservas",
            "Otro"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = new ProductRepository(getApplication());

        // Configurar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCategory = "";
                } else {
                    selectedCategory = categories[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        // Selector de fecha
        binding.btnPickDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedDate.set(year, month, day, 23, 59, 0);
                        dateSelected = true;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        binding.tvSelectedDate.setText("Fecha: " + sdf.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        // Guardar producto
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String qtyStr = binding.etQuantity.getText().toString().trim();

            if (name.isEmpty() || selectedCategory.isEmpty() ||
                    qtyStr.isEmpty() || !dateSelected) {
                Toast.makeText(this, "Completa todos los campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            Product product = new Product(name, selectedCategory,
                    selectedDate.getTimeInMillis(), qty);
            repository.insert(product);

            if (product.isExpiringSoon() || product.isExpired()) {
                NotificationHelper.notifyProductExpiringSoon(this, product);
            }

            Toast.makeText(this, "Producto guardado ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
