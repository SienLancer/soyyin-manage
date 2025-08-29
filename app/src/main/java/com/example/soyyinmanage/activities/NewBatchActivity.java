package com.example.soyyinmanage.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Batch;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class NewBatchActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference batchesRef;;
    EditText timeRangeEditText, quantity_edt, des_edt;
    Button save_btn;
    String id, timeOfBatch, description;
    int quantity, initialQuantity;
    FirebaseRepository myDB;
    ArrayList<Batch> batches;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_batch);
        // Khởi tạo Firebase Database và đường dẫn lưu trữ
        database = FirebaseDatabase.getInstance();
        batchesRef = database.getReference("batches");

        // Các khai báo khác và thiết lập giao diện
        quantity_edt = findViewById(R.id.quantity_edt);
        des_edt = findViewById(R.id.des_edt);
        save_btn = findViewById(R.id.save_btn);
        myDB = new FirebaseRepository(this);
        batches = new ArrayList<>();

        timeRangeEditText = findViewById(R.id.timeRangeEditText);

        // Khi nhấn vào EditText, sẽ mở TimePicker để chọn giờ bắt đầu và kết thúc
        //timeRangeEditText.setOnClickListener(v -> showStartTimePicker());
        timeRangeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(NewBatchActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener, year, month, day);
                dialog.show();
            }
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            timeRangeEditText.setText(date);

        };

        quantity_edt.setText("0");


        save_btn.setOnClickListener(view -> {
            quantity = Integer.parseInt(quantity_edt.getText().toString().trim());
            initialQuantity = quantity;
            description = des_edt.getText().toString().trim();
            timeOfBatch = timeRangeEditText.getText().toString().trim();

            // Kiểm tra điều kiện nhập dữ liệu
            if (timeOfBatch.equals("Click here to select time of course")
                    || quantity == 0 ) {
                displayFillAll();
            } else {

                String batchId = batchesRef.push().getKey();
                Batch batch = new Batch(batchId, timeOfBatch, quantity, initialQuantity, description);

                // Add course to SQLite (using the same ID)
                myDB.addBatch(batch);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    public void displayFillAll() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("You need to fill all required fields!")
                .setNeutralButton("Close", null)
                .show();
    }
}