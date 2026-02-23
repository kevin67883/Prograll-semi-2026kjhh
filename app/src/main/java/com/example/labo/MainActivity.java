package com.example.labo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    Spinner spn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(v->calcular());
    }
    private void calcular(){
        tempVal = findViewById(R.id.txtNum1);
        Double num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        Double num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = 0;

        spn = findViewById(R.id.cboOpciones);
        switch (spn.getSelectedItemPosition()){
            case 0: //suma
                respuesta = num1 + num2;
                break;
            case 1: //Resta
                respuesta = num1 - num2;
                break;
            case 2: //Multiplicacion
                respuesta = num1 * num2;
                break;
            case 3: //division
                respuesta = num1 / num2;
                break;
            case 4: //Factorial (usa solo num1)
                long fact = 1;
                for (int i = 1; i <= num1.intValue(); i++) {
                    fact *= i;
                }
                respuesta = fact;
                break;
            case 5: //Porcentaje (num1 % de num2)
                respuesta = (num1 / 100) * num2;
                break;
            case 6: //Exponenciacion (num1 ^ num2)
                respuesta = Math.pow(num1, num2);
                break;
            case 7: //Raiz (raiz num2 de num1)
                respuesta = Math.pow(num1, 1.0 / num2);
                break;
        }
        tempVal = findViewById(R.id.lblRespuesta);
        tempVal.setText("Respuesta: "+ respuesta);
    }
}