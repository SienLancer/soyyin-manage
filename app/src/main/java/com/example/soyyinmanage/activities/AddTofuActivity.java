package com.example.soyyinmanage.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Batch;
import com.example.soyyinmanage.models.Tofu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class AddTofuActivity extends AppCompatActivity {

    FirebaseRepository myDB;
    TextView date_control;
    EditText quantity_tf_edt, comment_cl_edt;
    DatabaseReference tofuRef;
    FirebaseDatabase database;
    Button save_cl_btn, back_add_cl_btn;
    String id, typeOfSale, date, comment, batch_id, id_p, timeOfBatch, description;
    int quantity, quantity_batch, quantity_total, initialQuantity;
    RadioGroup rg;
    RadioButton ship_rb, shop_rb, shopee_rb;
    private int startHour, startMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tofu);
        database = FirebaseDatabase.getInstance();
        tofuRef = database.getReference("tofus");
        date_control = findViewById(R.id.date_control);
        quantity_tf_edt = findViewById(R.id.quantity_tf_edt);
        comment_cl_edt = findViewById(R.id.comment_cl_edt);
        save_cl_btn = findViewById(R.id.save_cl_btn);
        back_add_cl_btn = findViewById(R.id.back_add_cl_btn);
        rg = findViewById(R.id.rg);
        ship_rb = findViewById(R.id.ship_rb);
        shop_rb = findViewById(R.id.shop_rb);
        shopee_rb = findViewById(R.id.shopee_rb);

        myDB = new FirebaseRepository(this);
        getAndSetIntentData();
        date_control.setOnClickListener(v -> showStartTimePicker());

        quantity_tf_edt.setText("0");

        save_cl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quantity = Integer.parseInt(quantity_tf_edt.getText().toString().trim());
                date = date_control.getText().toString().trim();
                comment = comment_cl_edt.getText().toString().trim();
                batch_id = id_p;
                quantity_total = quantity_batch - quantity;


                if (quantity <= 0 || date.matches("Bấm vào đây để chọn giờ") || rg.getCheckedRadioButtonId() == -1) {
                    displayFillAll();
                } else if (quantity_total < 0) {
                    displayQuantity();
                } else {
                    int id_btn = rg.getCheckedRadioButtonId();
                    RadioButton rb = findViewById(id_btn);
                    typeOfSale = rb.getText().toString();

                    String tofuId = tofuRef.push().getKey();
                    myDB.addTofu(new Tofu(tofuId, quantity, date, typeOfSale, comment, batch_id ));
                    myDB.updateBatch(new Batch(batch_id, timeOfBatch, quantity_total, initialQuantity, description));
                    Intent intent = new Intent(AddTofuActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        back_add_cl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }




    public void displayFillAll(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(
                        "Không được để trống, bạn cần điền đầy đủ thông tin!"
                )
                .setNeutralButton("Đóng", (dialogInterface, i) -> {

                })
                .show();
    }

    public void displayQuantity(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(
                        "Nhập quá số lượng hiện có!"
                )
                .setNeutralButton("Đóng", (dialogInterface, i) -> {

                })
                .show();
    }
    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("quantity_batch")
                && getIntent().hasExtra("initialQuantity")
                && getIntent().hasExtra("timeOfBatch") && getIntent().hasExtra("description")) {
            // Geting Data from Intent
            id_p = getIntent().getStringExtra("id");
            quantity_batch = getIntent().getIntExtra("quantity_batch", 0);
            initialQuantity = getIntent().getIntExtra("initialQuantity", 0);
            timeOfBatch = getIntent().getStringExtra("timeOfBatch");
            description = getIntent().getStringExtra("description");

        }else {
            Toast.makeText(this, "No ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStartTimePicker() {
        // Lấy giờ hiện tại làm mặc định
        Calendar calendar = Calendar.getInstance();
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);

        // Tạo TimePickerDialog cho thời gian bắt đầu
        TimePickerDialog startTimePicker = new TimePickerDialog(AddTofuActivity.this, (view, hourOfDay, minute) -> {
            startHour = hourOfDay;
            startMinute = minute;
            //showEndTimePicker(); // Mở tiếp TimePicker cho giờ kết thúc
            updateTimeRange(); // Cập nhật thời gian vào EditText
        }, startHour, startMinute, true);

        startTimePicker.setTitle("Select Start Time");
        startTimePicker.show();
    }


    private void updateTimeRange() {
        // Định dạng thời gian theo "HH:mm"
        String startTime = String.format("%02d:%02d", startHour, startMinute);
        String timeRange = startTime;

        // Hiển thị thời gian vào EditText
        date_control.setText(timeRange);
    }
}