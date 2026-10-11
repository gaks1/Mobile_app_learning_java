package com.example.paypoint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import  android.widget.Button;
import  android.widget.Spinner;
import  android.widget.TextView;
import  android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Spinner itemSpinner, priceSpinner, qtySpinner;

    Button btnAdd, GrandTotalButton, ReceiptButton;
    TextView tvReceipt, GrandTotal;

    String[] itemsList = { "Select items", "Apples", "Milk", "Bread", "Eggs", "Cheese" };

    int[] qtyOptions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    double[] priceOptions = {0.00, 0.50, 1.00, 1.50, 2.00, 2.50, 3.00, 3.50, 4.00 };

    List<String> addedLines = new ArrayList<>();
    double runningTotal = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        itemSpinner = findViewById(R.id.spinner1);
        priceSpinner = findViewById(R.id.spinnerPrice);
        qtySpinner = findViewById(R.id.spinnerQty);

        btnAdd = findViewById(R.id.btnAdd);
        GrandTotalButton = findViewById(R.id.button);
        GrandTotal = findViewById(R.id.textView6);
        tvReceipt = findViewById(R.id.tvReceipt);

        ReceiptButton = findViewById(R.id.button2);

        // Item spinner
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, itemsList);
        itemAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        itemSpinner.setAdapter(itemAdapter);

        // Quantity spinner (1..10)
        List<String> qtyList = new ArrayList<>();
        for (int q : qtyOptions) {
            qtyList.add(String.valueOf(q));
        }
        ArrayAdapter<String> qtyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, qtyList);
        qtyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        qtySpinner.setAdapter(qtyAdapter);

        // Price spinner (0.50..4.00) - same options for every item
        List<String> priceList = new ArrayList<>();
        for (double p : priceOptions) {
            priceList.add(String.format(Locale.US, "%.2f", p));
        }
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, priceList);
        priceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);

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
        String name = itemSpinner.getSelectedItem().toString();
        if (name.equals("Select items")) {
            return;
        }

        int quantity = Integer.parseInt(qtySpinner.getSelectedItem().toString());
        if (quantity <= 0) {
            return;
        }

        double price = parseOrDefault(priceSpinner.getSelectedItem().toString(), 0.00);
        if (price <= 0.00) {
            return;
        }


        double unitPrice = parseOrDefault(priceSpinner.getSelectedItem().toString(), 0.00);
        double rowTotal = unitPrice * quantity;
        runningTotal += rowTotal;

        addedLines.add(String.format(Locale.US, "%-12s %3d x %6.2f %8.2f%n",
                name, quantity, unitPrice, rowTotal));

        refreshReceiptList();
        calculateGrandTotal();

        itemSpinner.setSelection(0);
        qtySpinner.setSelection(0);
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
