package com.example.parcial1;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── Pestaña 1: Agua ──────────────────────────────
    EditText etMetros;
    TextView tvResultadoAgua;
    Button btnCalcularAgua;

    // ── Pestaña 2: Área ──────────────────────────────
    EditText etValorArea;
    Spinner spinnerDe, spinnerA;
    TextView tvResultadoArea;
    Button btnConvertir;

    // Unidades de área: nombre y equivalencia en m²
    final String[] nombresUnidades = {
            "Pie Cuadrado",
            "Vara Cuadrada",
            "Yarda Cuadrada",
            "Metro Cuadrado",
            "Tarea",
            "Manzana",
            "Hectárea"
    };

    // 1 Manzana = 16 Tareas  →  si 1 Tarea = 6259.419 m²  entonces 1 Manzana = 16 × 6259.419
    final double TAREA_M2    = 6259.419;
    final double MANZANA_M2  = 16 * 6259.419; // = 100,150.704 m²

    final double[] aM2 = {
            0.09290304,   // Pie²
            0.698737,     // Vara² (El Salvador)
            0.83612736,   // Yarda²
            1.0,          // Metro²
            TAREA_M2,     // Tarea  (El Salvador)
            MANZANA_M2,   // Manzana = 16 Tareas
            10000.0       // Hectárea
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarTabs();
        configurarAgua();
        configurarArea();
    }

    // ─────────────────────────────────────────────────
    // TABS
    // ─────────────────────────────────────────────────
    private void configurarTabs() {
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        // Pestaña 1 — Agua
        TabHost.TabSpec tab1 = tabHost.newTabSpec("agua");
        tab1.setIndicator("💧 Agua Potable");
        tab1.setContent(R.id.tabAgua);
        tabHost.addTab(tab1);

        // Pestaña 2 — Área
        TabHost.TabSpec tab2 = tabHost.newTabSpec("area");
        tab2.setIndicator("📐 Conversor de Área");
        tab2.setContent(R.id.tabArea);
        tabHost.addTab(tab2);
    }

    // ─────────────────────────────────────────────────
    // PESTAÑA 1 — CALCULADORA DE AGUA
    // ─────────────────────────────────────────────────
    private void configurarAgua() {
        etMetros        = findViewById(R.id.etMetros);
        tvResultadoAgua = findViewById(R.id.tvResultadoAgua);
        btnCalcularAgua = findViewById(R.id.btnCalcularAgua);

        btnCalcularAgua.setOnClickListener(v -> calcularAgua());
    }

    private void calcularAgua() {
        String input = etMetros.getText().toString().trim();

        if (input.isEmpty()) {
            tvResultadoAgua.setText("⚠ Por favor ingresa los metros consumidos.");
            return;
        }

        double metros;
        try {
            metros = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            tvResultadoAgua.setText("⚠ Ingresa un número válido.");
            return;
        }

        if (metros < 1) {
            tvResultadoAgua.setText("⚠ El consumo mínimo es 1 metro.");
            return;
        }

        double total;
        StringBuilder detalle = new StringBuilder();

        if (metros <= 18) {
            // Tramo 1: cuota fija
            total = 6.0;
            detalle.append("Consumo: ").append((int) metros).append(" mts (dentro de cuota fija)\n\n");
            detalle.append("✔ Cuota fija = $6.00\n\n");
            detalle.append("━━━━━━━━━━━━━━━━━━━━\n");
            detalle.append("💵 Total a pagar: $6.00");

        } else if (metros <= 28) {
            // Tramo 2: exceso sobre 18
            double exceso = metros - 18;
            double cargo  = exceso * 0.45;
            total         = 6.0 + cargo;

            detalle.append("Consumo: ").append((int) metros).append(" mts\n\n");
            detalle.append("✔ Cuota fija (1–18 mts) = $6.00\n");
            detalle.append("✔ Exceso: ").append((int) metros)
                    .append(" - 18 = ").append((int) exceso).append(" mts\n");
            detalle.append("✔ Cargo exceso: ").append((int) exceso)
                    .append(" × $0.45 = $").append(String.format("%.2f", cargo)).append("\n");
            detalle.append("✔ Total: $6.00 + $").append(String.format("%.2f", cargo))
                    .append(" = $").append(String.format("%.2f", total)).append("\n\n");
            detalle.append("━━━━━━━━━━━━━━━━━━━━\n");
            detalle.append("💵 Total a pagar: $").append(String.format("%.2f", total));

        } else {
            // Tramo 3: exceso sobre 28
            double exceso2 = metros - 28;
            double cargo2  = exceso2 * 0.65;
            double exceso1 = 28 - 18;           // siempre 10
            double cargo1  = exceso1 * 0.45;    // siempre $4.50
            total          = 6.0 + cargo1 + cargo2;

            detalle.append("Consumo: ").append((int) metros).append(" mts\n\n");
            detalle.append("✔ Cuota fija (1–18 mts) = $6.00\n");
            detalle.append("✔ Tramo 19–28: ").append((int) exceso1)
                    .append(" mts × $0.45 = $").append(String.format("%.2f", cargo1)).append("\n");
            detalle.append("✔ Exceso sobre 28: ").append((int) metros)
                    .append(" - 28 = ").append((int) exceso2).append(" mts\n");
            detalle.append("✔ Cargo exceso: ").append((int) exceso2)
                    .append(" × $0.65 = $").append(String.format("%.2f", cargo2)).append("\n");
            detalle.append("✔ Total: $6.00 + $").append(String.format("%.2f", cargo1))
                    .append(" + $").append(String.format("%.2f", cargo2))
                    .append(" = $").append(String.format("%.2f", total)).append("\n\n");
            detalle.append("━━━━━━━━━━━━━━━━━━━━\n");
            detalle.append("💵 Total a pagar: $").append(String.format("%.2f", total));
        }

        tvResultadoAgua.setText(detalle.toString());
    }

    // ─────────────────────────────────────────────────
    // PESTAÑA 2 — CONVERSOR DE ÁREA
    // ─────────────────────────────────────────────────
    private void configurarArea() {
        etValorArea      = findViewById(R.id.etValorArea);
        spinnerDe        = findViewById(R.id.spinnerDe);
        spinnerA         = findViewById(R.id.spinnerA);
        tvResultadoArea  = findViewById(R.id.tvResultadoArea);
        btnConvertir     = findViewById(R.id.btnConvertir);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresUnidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDe.setAdapter(adapter);
        spinnerA.setAdapter(adapter);

        // Por defecto: de Metro² a Hectárea
        spinnerDe.setSelection(3); // Metro Cuadrado
        spinnerA.setSelection(6);  // Hectárea

        btnConvertir.setOnClickListener(v -> convertirArea());
    }

    private void convertirArea() {
        String input = etValorArea.getText().toString().trim();

        if (input.isEmpty()) {
            tvResultadoArea.setText("⚠ Ingresa un valor para convertir.");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            tvResultadoArea.setText("⚠ Ingresa un número válido.");
            return;
        }

        if (valor < 0) {
            tvResultadoArea.setText("⚠ El valor debe ser mayor o igual a 0.");
            return;
        }

        int posOrigen  = spinnerDe.getSelectedItemPosition();
        int posDestino = spinnerA.getSelectedItemPosition();

        // Convertir a metros cuadrados primero, luego a destino
        double enM2       = valor * aM2[posOrigen];
        double resultado  = enM2 / aM2[posDestino];

        StringBuilder sb = new StringBuilder();
        sb.append("Conversión:\n\n");
        sb.append(formatearNumero(valor)).append(" ").append(nombresUnidades[posOrigen]).append("\n");
        sb.append("= ").append(formatearNumero(enM2)).append(" m²\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📏 Resultado:\n");
        sb.append(formatearNumero(resultado)).append(" ").append(nombresUnidades[posDestino]);

        tvResultadoArea.setText(sb.toString());
    }

    // Formatea: sin decimales si es entero, hasta 6 decimales si no
    private String formatearNumero(double n) {
        if (n == Math.floor(n) && !Double.isInfinite(n)) {
            return String.format("%,.0f", n);
        }
        // Quitar ceros finales innecesarios
        String s = String.format("%.8f", n).replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }
}