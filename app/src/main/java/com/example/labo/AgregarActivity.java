package com.example.labo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.EditText;

public class AgregarActivity extends AppCompatActivity {

    private EditText etCodigo, etNombre, etMarca, etTalla, etPrecio, etDescripcion;
    private ImageView imgPreview;
    private String fotoPath = "";
    private Uri fotoUri;
    private ProductoDAO dao;

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
        setTitle("Agregar Producto");

        dao = new ProductoDAO(this);
        etCodigo      = findViewById(R.id.etCodigo);
        etNombre      = findViewById(R.id.etNombre);
        etMarca       = findViewById(R.id.etMarca);
        etTalla       = findViewById(R.id.etTalla);
        etPrecio      = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgPreview    = findViewById(R.id.imgPreview);
        Button btnFoto    = findViewById(R.id.btnSeleccionarFoto);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnFoto.setOnClickListener(v -> mostrarOpcionesFoto());

        btnGuardar.setOnClickListener(v -> {
            if (validar()) {
                Producto p = new Producto(
                        etCodigo.getText().toString().trim(),
                        etNombre.getText().toString().trim(),
                        etMarca.getText().toString().trim(),
                        etTalla.getText().toString().trim(),
                        Double.parseDouble(etPrecio.getText().toString().trim()),
                        etDescripcion.getText().toString().trim(),
                        fotoPath
                );
                long result = dao.insertar(p);
                if (result > 0) {
                    Toast.makeText(this, "Producto guardado ✓", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error: código duplicado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarOpcionesFoto() {
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar foto")
                .setItems(new String[]{"Tomar foto", "Elegir de galería"}, (dialog, which) -> {
                    if (which == 0) abrirCamara();
                    else pickImage.launch("image/*");
                })
                .show();
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
            // ✅ Authority corregido
            fotoUri = FileProvider.getUriForFile(this,
                    "com.example.labo.fileprovider", fotoFile);
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
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validar() {
        if (etCodigo.getText().toString().trim().isEmpty()) {
            etCodigo.setError("Requerido"); return false;
        }
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Requerido"); return false;
        }
        if (etPrecio.getText().toString().trim().isEmpty()) {
            etPrecio.setError("Requerido"); return false;
        }
        return true;
    }
}