package com.example.labo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditarActivity extends AppCompatActivity {

    private EditText etCodigo, etNombre, etMarca, etTalla, etPrecio,
            etCosto, etStock, etDescripcion;   // ← nuevos
    private ImageView imgPreview;
    private String fotoPath = "";
    private Uri fotoUri;
    private int productoId;
    private String couchId;
    private ProductoDAO dao;
    private SyncManager syncManager;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    fotoPath = uri.toString();
                    Glide.with(this).load(uri)
                            .transform(new CircleCrop())
                            .into(imgPreview);
                }
            });

    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && fotoUri != null) {
                    fotoPath = fotoUri.toString();
                    Glide.with(this).load(fotoUri)
                            .transform(new CircleCrop())
                            .into(imgPreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        setTitle("Editar Producto");

        dao = new ProductoDAO(this);
        syncManager = new SyncManager(this);

        etCodigo      = findViewById(R.id.etCodigo);
        etNombre      = findViewById(R.id.etNombre);
        etMarca       = findViewById(R.id.etMarca);
        etTalla       = findViewById(R.id.etTalla);
        etPrecio      = findViewById(R.id.etPrecio);
        etCosto       = findViewById(R.id.etCosto);
        etStock       = findViewById(R.id.etStock);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgPreview    = findViewById(R.id.imgPreview);

        // Cargar datos existentes
        productoId = getIntent().getIntExtra("id", -1);
        couchId    = getIntent().getStringExtra("couchId");
        etCodigo.setText(getIntent().getStringExtra("codigo"));
        etNombre.setText(getIntent().getStringExtra("nombre"));
        etMarca.setText(getIntent().getStringExtra("marca"));
        etTalla.setText(getIntent().getStringExtra("talla"));
        etPrecio.setText(String.valueOf(getIntent().getDoubleExtra("precio", 0)));
        etCosto.setText(String.valueOf(getIntent().getDoubleExtra("costo", 0)));
        etStock.setText(String.valueOf(getIntent().getIntExtra("stock", 0)));
        etDescripcion.setText(getIntent().getStringExtra("descripcion"));
        fotoPath = getIntent().getStringExtra("foto");

        if (fotoPath != null && !fotoPath.isEmpty()) {
            Glide.with(this).load(fotoPath)
                    .transform(new CircleCrop())
                    .into(imgPreview);
        }

        findViewById(R.id.btnSeleccionarFoto).setOnClickListener(v -> mostrarOpcionesFoto());

        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            double costo = etCosto.getText().toString().trim().isEmpty()
                    ? 0 : Double.parseDouble(etCosto.getText().toString().trim());
            int stock = etStock.getText().toString().trim().isEmpty()
                    ? 0 : Integer.parseInt(etStock.getText().toString().trim());

            Producto p = new Producto(
                    etCodigo.getText().toString().trim(),
                    etNombre.getText().toString().trim(),
                    etMarca.getText().toString().trim(),
                    etTalla.getText().toString().trim(),
                    Double.parseDouble(etPrecio.getText().toString().trim()),
                    costo,   // ← nuevo
                    stock,   // ← nuevo
                    etDescripcion.getText().toString().trim(),
                    fotoPath
            );
            p.setId(productoId);
            p.setCouchId(couchId);
            dao.actualizar(p);

            if (couchId != null && !couchId.isEmpty()) {
                syncManager.actualizarProducto(p, new SyncManager.SyncCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        Toast.makeText(EditarActivity.this,
                                "Actualizado y sincronizado ✓", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(EditarActivity.this,
                                "Actualizado offline ✓", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Actualizado ✓", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private void mostrarOpcionesFoto() {
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar foto")
                .setItems(new String[]{"Tomar foto", "Elegir de galería"}, (dialog, which) -> {
                    if (which == 0) abrirCamara();
                    else pickImage.launch("image/*");
                }).show();
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            lanzarCamara();
        }
    }

    private void lanzarCamara() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File fotoFile = File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
            fotoUri = FileProvider.getUriForFile(this,
                    "com.example.tiendaropa2.fileprovider", fotoFile);
            takePicture.launch(fotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error al abrir cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            lanzarCamara();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }
}