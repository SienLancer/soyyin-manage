package com.example.soyyinmanage.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.activities.MainActivity;
import com.example.soyyinmanage.models.Batch;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AddFragment extends Fragment {
    private View mView;
    FirebaseDatabase database;
    DatabaseReference batchesRef;;
    EditText timeRangeEditText, quantity_edt, des_edt;
    Button save_btn;
    String id, timeOfBatch, description;
    int quantity;
    FirebaseRepository myDB;
    ArrayList<Batch> batches;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add, container, false);

        // Khởi tạo Firebase Database và đường dẫn lưu trữ
        database = FirebaseDatabase.getInstance();
        batchesRef = database.getReference("batches");

        // Các khai báo khác và thiết lập giao diện
        quantity_edt = mView.findViewById(R.id.quantity_edt);
        des_edt = mView.findViewById(R.id.des_edt);
        save_btn = mView.findViewById(R.id.save_btn);
        myDB = new FirebaseRepository(getActivity());
        batches = new ArrayList<>();

        timeRangeEditText = mView.findViewById(R.id.timeRangeEditText);

        // Khi nhấn vào EditText, sẽ mở TimePicker để chọn giờ bắt đầu và kết thúc
        //timeRangeEditText.setOnClickListener(v -> showStartTimePicker());
        timeRangeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar kal = Calendar.getInstance();
                int year = kal.get(Calendar.YEAR);
                int month = kal.get(Calendar.MONTH);
                int day = kal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog,
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




        return mView;
    }

    public void displayFillAll() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage("You need to fill all required fields!")
                .setNeutralButton("Close", null)
                .show();
    }
}
