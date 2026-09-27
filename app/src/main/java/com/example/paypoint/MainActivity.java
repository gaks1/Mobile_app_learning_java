package com.example.paypoint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import  android.widget.Button;
import  android.widget.EditText;
import  android.widget.Spinner;
import  android.widget.TextView;
import  android.view.View;
import  android.widget.ArrayAdapter;
import  android.widget.AdapterView;
import  android.text.Editable;
import  android.text.TextWatcher;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Spinner[] spinners = new Spinner[5];
    TextView[] unitPrices = new TextView[5];
    EditText[] quantities = new EditText[5];
    TextView[] totals = new TextView[5];

    Button GrandTotalButton, ReceiptButton;
    TextView GrandTotal;

    HashMap<String, Double> items = new HashMap<>();
    String[] itemsList = { "Select items", "Apples", "Milk", "Bread", "Eggs", "Cheese" };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        items.put("Select items", 0.00);
        items.put("Apples", 1.00);
        items.put("Milk", 2.00);
        items.put("Bread", 1.00);
        items.put("Eggs", 2.00);
        items.put("Cheese", 3.00);

        spinners[0] = findViewById(R.id.spinner1);
        spinners[1] = findViewById(R.id.spinner2);
        spinners[2] = findViewById(R.id.spinner3);
        spinners[3] = findViewById(R.id.spinner4);
        spinners[4] = findViewById(R.id.spinner5);

        unitPrices[0] = findViewById(R.id.tvUnitPrice1);
        unitPrices[1] = findViewById(R.id.tvUnitPrice2);
        unitPrices[2] = findViewById(R.id.tvUnitPrice3);
        unitPrices[3] = findViewById(R.id.tvUnitPrice4);
        unitPrices[4] = findViewById(R.id.tvUnitPrice5);

        quantities[0] = findViewById(R.id.etQty1);
        quantities[1] = findViewById(R.id.etQty2);
        quantities[2] = findViewById(R.id.etQty3);
        quantities[3] = findViewById(R.id.etQty4);
        quantities[4] = findViewById(R.id.etQty5);

        totals[0] = findViewById(R.id.tvTotal1);
        totals[1] = findViewById(R.id.tvTotal2);
        totals[2] = findViewById(R.id.tvTotal3);
        totals[3] = findViewById(R.id.tvTotal4);
        totals[4] = findViewById(R.id.tvTotal5);

        GrandTotalButton = findViewById(R.id.button);
        GrandTotal = findViewById(R.id.textView6);

        ReceiptButton = findViewById(R.id.button2);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemsList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        for (int i = 0; i < 5 ; i++){
            spinners[i].setAdapter(adapter);

            final int index = i;
            spinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                    String selected = itemsList[position];
                    double price = items.get(selected);
                    unitPrices[index].setText(String.format(Locale.US, "%.2f", price));
                    calculateRowTotal(index);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            final int qtyIndex = i;
            quantities[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    calculateRowTotal(qtyIndex);
                }
            });
        }

        GrandTotalButton.setOnClickListener(v -> calculateGrandTotal());

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void calculateRowTotal(int index){
        double unitPrice = parseOrDefault(unitPrices[index].getText().toString(), 0.00);
        double quantity = parseOrDefault(quantities[index].getText().toString(), 0.00);

        double total = unitPrice * quantity;
        totals[index].setText(String.format(Locale.US, "%.2f", total));
    }

    public void calculateGrandTotal (){
        double total = 0.00;
        for( int i = 0; i < 5 ; i++){
            double pricetotal = parseOrDefault(totals[i].getText().toString(), 0.00);
            total += pricetotal;
        }
        GrandTotal.setText(String.format(Locale.US, "%.2f", total));
    }

    public double parseOrDefault (String text, Double nodefaults){
        if( text == null || text.isEmpty()){
            return nodefaults;
        }
        try {
            return Double.parseDouble(text);
        }catch(NumberFormatException e){
            return nodefaults;
        }
    }
}