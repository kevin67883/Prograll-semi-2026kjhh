package com.example.labo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {


    private static final String CHANNEL_ID = "canal_conversion";
    private int notifId = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearCanal();

        TabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        agregarTab(tabHost, "Moneda",  R.id.tab1);
        agregarTab(tabHost, "Masa",    R.id.tab2);
        agregarTab(tabHost, "Tiempo",  R.id.tab3);
        agregarTab(tabHost, "Volumen", R.id.tab4);
        agregarTab(tabHost, "Longitud",R.id.tab5);
        agregarTab(tabHost, "Almacen", R.id.tab6);

        // ── TAB 1: MONEDA (base: USD) ──────────────────────────────────────
        configurarConversion(R.id.etMoneda1, R.id.btnMoneda1, R.id.tvMoneda1, 0.92,    "EUR  (€)");
        configurarConversion(R.id.etMoneda2, R.id.btnMoneda2, R.id.tvMoneda2, 0.79,    "GBP  (£)");
        configurarConversion(R.id.etMoneda3, R.id.btnMoneda3, R.id.tvMoneda3, 149.50,  "JPY  (¥)");
        configurarConversion(R.id.etMoneda4, R.id.btnMoneda4, R.id.tvMoneda4, 17.15,   "MXN");
        configurarConversion(R.id.etMoneda5, R.id.btnMoneda5, R.id.tvMoneda5, 4.97,    "BRL  (R$)");

        // ── TAB 2: MASA (base: kg) ─────────────────────────────────────────
        configurarConversion(R.id.etMasa1, R.id.btnMasa1, R.id.tvMasa1, 2.20462,  "lb");
        configurarConversion(R.id.etMasa2, R.id.btnMasa2, R.id.tvMasa2, 1000.0,   "g");
        configurarConversion(R.id.etMasa3, R.id.btnMasa3, R.id.tvMasa3, 35.274,   "oz");
        configurarConversion(R.id.etMasa4, R.id.btnMasa4, R.id.tvMasa4, 0.001,    "t (tonelada)");
        configurarConversion(R.id.etMasa5, R.id.btnMasa5, R.id.tvMasa5, 1000000.0,"mg");

        // ── TAB 3: TIEMPO (base: horas) ────────────────────────────────────
        configurarConversion(R.id.etTiempo1, R.id.btnTiempo1, R.id.tvTiempo1, 60.0,     "min");
        configurarConversion(R.id.etTiempo2, R.id.btnTiempo2, R.id.tvTiempo2, 3600.0,   "seg");
        configurarConversion(R.id.etTiempo3, R.id.btnTiempo3, R.id.tvTiempo3, 0.041667, "días");
        configurarConversion(R.id.etTiempo4, R.id.btnTiempo4, R.id.tvTiempo4, 0.005952, "semanas");
        configurarConversion(R.id.etTiempo5, R.id.btnTiempo5, R.id.tvTiempo5, 0.001369, "meses");

        // ── TAB 4: VOLUMEN (base: litros) ──────────────────────────────────
        configurarConversion(R.id.etVolumen1, R.id.btnVolumen1, R.id.tvVolumen1, 1000.0,   "mL");
        configurarConversion(R.id.etVolumen2, R.id.btnVolumen2, R.id.tvVolumen2, 0.264172, "gal (US)");
        configurarConversion(R.id.etVolumen3, R.id.btnVolumen3, R.id.tvVolumen3, 1.05669,  "qt (US)");
        configurarConversion(R.id.etVolumen4, R.id.btnVolumen4, R.id.tvVolumen4, 33.814,   "fl oz");
        configurarConversion(R.id.etVolumen5, R.id.btnVolumen5, R.id.tvVolumen5, 0.001,    "m³");

        // ── TAB 5: LONGITUD (base: metros) ─────────────────────────────────
        configurarConversion(R.id.etLongitud1, R.id.btnLongitud1, R.id.tvLongitud1, 100.0,     "cm");
        configurarConversion(R.id.etLongitud2, R.id.btnLongitud2, R.id.tvLongitud2, 1000.0,    "mm");
        configurarConversion(R.id.etLongitud3, R.id.btnLongitud3, R.id.tvLongitud3, 3.28084,   "ft");
        configurarConversion(R.id.etLongitud4, R.id.btnLongitud4, R.id.tvLongitud4, 39.3701,   "in");
        configurarConversion(R.id.etLongitud5, R.id.btnLongitud5, R.id.tvLongitud5, 0.001,     "km");

        // ── TAB 6: ALMACENAMIENTO (base: GB) ───────────────────────────────
        configurarConversion(R.id.etAlmacen1, R.id.btnAlmacen1, R.id.tvAlmacen1, 1024.0,         "MB");
        configurarConversion(R.id.etAlmacen2, R.id.btnAlmacen2, R.id.tvAlmacen2, 1048576.0,      "KB");
        configurarConversion(R.id.etAlmacen3, R.id.btnAlmacen3, R.id.tvAlmacen3, 1073741824.0,   "Bytes");
        configurarConversion(R.id.etAlmacen4, R.id.btnAlmacen4, R.id.tvAlmacen4, 0.0009765625,   "TB");
        configurarConversion(R.id.etAlmacen5, R.id.btnAlmacen5, R.id.tvAlmacen5, 8589934592.0,   "bits");
    }

// ───────────────────────────────────────────────────────────────────────────
//  Helpers
// ───────────────────────────────────────────────────────────────────────────

    private void agregarTab(TabHost host, String titulo, int id) {
        TabHost.TabSpec spec = host.newTabSpec(titulo);
        spec.setContent(id);
        spec.setIndicator(titulo);
        host.addTab(spec);
    }

    private void configurarConversion(int etId, int btnId, int tvId,
                                      double factor, String unidad) {
        EditText et  = findViewById(etId);
        Button   btn = findViewById(btnId);
        TextView tv  = findViewById(tvId);

        btn.setOnClickListener(v -> {
            String texto = et.getText().toString().trim();

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingrese un valor", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double valor     = Double.parseDouble(texto);
                double resultado = valor * factor;

                // Formatea con hasta 6 decimales sin ceros innecesarios
                String resultadoStr = formatearResultado(resultado);
                String mensaje      = resultadoStr + " " + unidad;

                tv.setText("Resultado: " + mensaje);
                mostrarNotificacion("Conversión: " + valor + " → " + mensaje);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valor no válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Evita notación científica y recorta decimales innecesarios. */
    private String formatearResultado(double valor) {
        if (valor == Math.floor(valor) && !Double.isInfinite(valor)) {
            return String.valueOf((long) valor);
        }
        // hasta 6 decimales sin ceros al final
        String str = String.format("%.6f", valor).replaceAll("0+$", "").replaceAll("\\.$", "");
        return str;
    }

    private void crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal Conversiones",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones de resultados de conversión");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacion(String mensaje) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Conversor Universal")
                        .setContentText(mensaje)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(this).notify(notifId++, builder.build());
        } catch (SecurityException e) {
            // Permiso POST_NOTIFICATIONS no concedido (Android 13+)
            Toast.makeText(this, "Permiso de notificación requerido", Toast.LENGTH_SHORT).show();
        }
    }


}