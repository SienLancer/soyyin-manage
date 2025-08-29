package com.example.soyyinmanage.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateTofuActivity extends AppCompatActivity {
    FirebaseRepository myDB;
    TextView date_up_control;
    EditText quantity_tf_up_edt, comment_cl_up_edt;


    Button update_cl_up_btn, back_up_cl_btn;
    String id, typeOfSale, date, comment, batch_id, id_p, timeOfBatch, description;
    int quantity_before, quantity_after, quantity_batch, quantity_result, quantity_total, initialQuantity;;
    RadioGroup rg_up;
    RadioButton ship_up_rb, shop_up_rb, shopee_up_rb;
    private int startHour, startMinute;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tofu);
        date_up_control = findViewById(R.id.date_up_control);
        quantity_tf_up_edt = findViewById(R.id.quantity_tf_up_edt);
        comment_cl_up_edt = findViewById(R.id.comment_cl_up_edt);
        update_cl_up_btn = findViewById(R.id.update_cl_up_btn);
        back_up_cl_btn = findViewById(R.id.back_update_cl_btn);
        rg_up = findViewById(R.id.rg_up);
        ship_up_rb = findViewById(R.id.ship_up_rb);
        shop_up_rb = findViewById(R.id.shop_up_rb);
        shopee_up_rb = findViewById(R.id.shopee_up_rb);

        myDB = new FirebaseRepository(this);
        getAndSetIntentData();
        getBatchFromFirebase(batch_id);
        date_up_control.setOnClickListener(v -> showStartTimePicker());

        back_up_cl_btn.setOnClickListener(view -> finish());

        update_cl_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity_after = Integer.parseInt(quantity_tf_up_edt.getText().toString().trim());
                date = date_up_control.getText().toString().trim();
                comment = comment_cl_up_edt.getText().toString().trim();
//                batch_id = id_p;



                if (quantity_after < quantity_before) {
                    quantity_result = quantity_before - quantity_after;
                    quantity_total = quantity_batch + quantity_result;
                } else if (quantity_after > quantity_before) {
                    quantity_result = quantity_after - quantity_before;
                    quantity_total = quantity_batch - quantity_result;
                } else {
                    quantity_total = quantity_batch;
                }

                if (quantity_after <= 0) {
                    displayFillAll();
                } else if (date.matches("Bấm vào đây để chọn giờ")) {
                    displayFillAll();
                } else if (quantity_after > quantity_batch) {
                    displayQuantity();
                } else {
                    int id_btn = rg_up.getCheckedRadioButtonId();
                    RadioButton rb = findViewById(id_btn);
                    typeOfSale = rb.getText().toString();

                    myDB.updateTofu(new Tofu(id, quantity_after, date, typeOfSale, comment, batch_id ));
                    myDB.updateBatch(new Batch(batch_id, timeOfBatch, quantity_total, initialQuantity, description));
                    finish();
//                    Intent intent = new Intent(UpdateTofuActivity.this, MainActivity.class);
//                    startActivity(intent);
                }

            }
        });


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
        if (getIntent().hasExtra("tf_id") && getIntent().hasExtra("quantity")
                && getIntent().hasExtra("date") && getIntent().hasExtra("typeOfSale")
                && getIntent().hasExtra("comment") && getIntent().hasExtra("batch_id") ) {

            // Geting Data from Intent
            id = getIntent().getStringExtra("tf_id");
            quantity_before = getIntent().getIntExtra("quantity", 0);
            date = getIntent().getStringExtra("date");
            typeOfSale = getIntent().getStringExtra("typeOfSale");
            comment = getIntent().getStringExtra("comment");
            batch_id = getIntent().getStringExtra("batch_id");

            // Setting Intent Data
            quantity_tf_up_edt.setText(quantity_before+"");
            date_up_control.setText(date);
            comment_cl_up_edt.setText(comment);

            if (typeOfSale.matches("Đi Ship")){
                ship_up_rb.setChecked(true);
            }else if (typeOfSale.matches("Cửa Hàng")){
                shop_up_rb.setChecked(true);
            }else if (typeOfSale.matches("Đơn Shopee")){
                shopee_up_rb.setChecked(true);
            }


        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBatchFromFirebase(String batchId) {
        DatabaseReference batchRef = FirebaseDatabase.getInstance()
                .getReference("batches")
                .child(batchId);

        batchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Batch batch = snapshot.getValue(Batch.class);
                    if (batch != null) {
                        quantity_batch = batch.getQuantity();
                        initialQuantity = batch.getInitialQuantity();
                        timeOfBatch = batch.getTimeOfBatch();
                        description = batch.getDescription();
                        Log.d(TAG, "Batch loaded: " + batchId + " - quantity: " + quantity_batch);
                    }
                } else {
                    Toast.makeText(UpdateTofuActivity.this, "Batch not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to load batch: " + error.getMessage());
            }
        });
    }




    private void showStartTimePicker() {
        // Lấy giờ hiện tại làm mặc định
        Calendar calendar = Calendar.getInstance();
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);

        // Tạo TimePickerDialog cho thời gian bắt đầu
        TimePickerDialog startTimePicker = new TimePickerDialog(UpdateTofuActivity.this, (view, hourOfDay, minute) -> {
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
        date_up_control.setText(timeRange);
    }

    public void displayFillAll(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(
                        "You need to fill all required fields!"
                )
                .setNeutralButton("Close", (dialogInterface, i) -> {

                })
                .show();
    }
}