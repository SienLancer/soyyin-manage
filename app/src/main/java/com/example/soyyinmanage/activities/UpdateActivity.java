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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Batch;

import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {
    EditText timeRangeEditText_up, quantity_edt_up, des_edt_up;

    Button update_btn_in, back_btn;
    String id, timeOfBatch, description;
    int quantity, initialQuantity;
    private DatePickerDialog.OnDateSetListener dateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        timeRangeEditText_up = findViewById(R.id.timeRangeEditText_up);
        quantity_edt_up = findViewById(R.id.quantity_edt_up);
        des_edt_up = findViewById(R.id.des_edt_up);

        update_btn_in = findViewById(R.id.update_btn_in);
        back_btn = findViewById(R.id.back_btn);
        getAndSetIntentData();
        timeRangeEditText_up = findViewById(R.id.timeRangeEditText_up);

        // Khi nhấn vào EditText, sẽ mở TimePicker để chọn giờ bắt đầu và kết thúc
        timeRangeEditText_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(UpdateActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener, year, month, day);
                dialog.show();
            }
        });
        dateSetListener = (datePicker, year, month, day) -> {
            month = month +1;
            Log.d(TAG, "onDateSet: dd/mm/yyyy " + day + "/" + month + "/" + year);
            String date = day + "/" + month + "/" + year;
            timeRangeEditText_up.setText(date);

        };

        back_btn.setOnClickListener(view -> finish());
        update_btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseRepository myDB = new FirebaseRepository(UpdateActivity.this);
                quantity = Integer.parseInt(quantity_edt_up.getText().toString().trim());
                description = des_edt_up.getText().toString().trim();
                timeOfBatch = timeRangeEditText_up.getText().toString().trim();





                if (timeOfBatch.matches("Bấm vào đây để chọn thời gian")) {
                    displayFillAll();
                } else if (quantity ==0) {
                    displayFillAll();
                } else {

                    myDB.updateBatch(new Batch(id, timeOfBatch, quantity, initialQuantity, description));
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });



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
            timeRangeEditText_up.setText(timeOfBatch);
            quantity_edt_up.setText(quantity+"");

            des_edt_up.setText(description);

        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayFillAll(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(
                        "You need to fill all required fields or fill in the correct email!"
                )
                .setNeutralButton("Close", (dialogInterface, i) -> {

                })
                .show();
    }




}