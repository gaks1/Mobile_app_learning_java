package com.example.paypoint;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    TextView tvUnitPrice;
    EditText etQty;

    Button btnAdd, GrandTotalButton, ReceiptButton;
    TextView tvReceipt, GrandTotal;

    HashMap<String, Double> items = new HashMap<>();
    String[] itemsList = { "Select items", "Apples", "Milk", "Bread", "Eggs", "Cheese" };

    List<String> addedLines = new ArrayList<>();
    double runningTotal = 0.00;

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

        spinner = findViewById(R.id.spinner1);
        tvUnitPrice = findViewById(R.id.tvUnitPrice1);
        etQty = findViewById(R.id.etQty1);

        btnAdd = findViewById(R.id.btnAdd);
        GrandTotalButton = findViewById(R.id.button);
        GrandTotal = findViewById(R.id.textView6);
        tvReceipt = findViewById(R.id.tvReceipt);

        ReceiptButton = findViewById(R.id.button2);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemsList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selected = itemsList[position];
                double price = items.get(selected);
                tvUnitPrice.setText(String.format(Locale.US, "%.2f", price));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAdd.setOnClickListener(v -> addCurrentItem());

        GrandTotalButton.setOnClickListener(v -> calculateGrandTotal());
        ReceiptButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra( "Receipt", buildReceipt() );
            startActivity(intent);
        });

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addCurrentItem(){
        String name = spinner.getSelectedItem().toString();
        if (name.equals("Select items")) {
            return;
        }

        double quantity = parseOrDefault(etQty.getText().toString(), 0.00);
        if (quantity <= 0) {
            return;
        }

        double unitPrice = items.get(name);
        double rowTotal = unitPrice * quantity;
        runningTotal += rowTotal;

        addedLines.add(String.format(Locale.US, "%-12s %3d x %6.2f %8.2f%n",
                name, (int) quantity, unitPrice, rowTotal));

        refreshReceiptList();
        calculateGrandTotal();

        spinner.setSelection(0);
        etQty.setText("0");
    }

    public void refreshReceiptList(){
        StringBuilder sb = new StringBuilder();
        for (String added : addedLines) {
            sb.append(added);
        }
        tvReceipt.setText(sb.toString());
    }

    public void calculateGrandTotal (){
        GrandTotal.setText(String.format(Locale.US, "%.2f", runningTotal));
    }

    public String buildReceipt(){
        String line = "------------------------------------------\n";
        String divider = "==========================================\n";

        StringBuilder receipt = new StringBuilder();
        receipt.append(line);
        receipt.append(String.format(Locale.US, "%28s%n", "MIAMI SUPERMARKET"));
        receipt.append(String.format(Locale.US, "%26s%n", "STORE RECEIPT"));
        receipt.append(line);
        receipt.append(String.format(Locale.US, "%-18s %4s %8s %9s%n", "ITEM", "QTY", "PRICE", "TOTAL"));
        receipt.append(line);

        for (String added : addedLines) {
            receipt.append(added);
        }

        receipt.append(line);
        receipt.append(String.format(Locale.US, "%-32s %9.2f%n", "TOTAL", runningTotal));
        receipt.append(divider);
        receipt.append(String.format(Locale.US, "%26s%n", "THANK YOU!"));
        receipt.append(line);
        return receipt.toString();
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
