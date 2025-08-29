package com.example.soyyinmanage.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.R;

public class DetailActivity extends AppCompatActivity {
    String id, timeOfBatch, description;
    int quantity, initialQuantity;
    Button back_btn;
    TextView timeob_d_content, quantity_d_content, des_content, initial_quantity_d_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        timeob_d_content = findViewById(R.id.timeob_d_content);
        quantity_d_content = findViewById(R.id.quantity_d_content);
        initial_quantity_d_content = findViewById(R.id.initial_quantity_d_content);

        des_content = findViewById(R.id.des_d_content);
        back_btn = findViewById(R.id.back_ob_btn);

        getAndSetIntentData();

        back_btn.setOnClickListener(view -> finish());



    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("timeOfBatch")
                && getIntent().hasExtra("initialQuantity")
                && getIntent().hasExtra("quantity") && getIntent().hasExtra("description")) {
            // Geting Data from Intent
            id = getIntent().getStringExtra("id");
            timeOfBatch = getIntent().getStringExtra("timeOfBatch");
            quantity = getIntent().getIntExtra("quantity", 0);
            initialQuantity = getIntent().getIntExtra("initialQuantity", 0);

            description = getIntent().getStringExtra("description");
            // Setting Intent Data
            timeob_d_content.setText(timeOfBatch);
            quantity_d_content.setText(quantity+"");
            initial_quantity_d_content.setText(initialQuantity+"");
            des_content.setText(description);



        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}