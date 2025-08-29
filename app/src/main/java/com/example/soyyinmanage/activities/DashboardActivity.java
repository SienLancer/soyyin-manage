package com.example.soyyinmanage.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Batch;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private BarChart barChartMonth, barChartYear;
    private TextView tvTotal;
    private Button btnSelectMonth;

    private DatabaseReference batchesRef;

    private int selectedMonth; // 1-12
    private int selectedYear;  // vd 2025

    private final SimpleDateFormat sdfInput = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        barChartMonth = findViewById(R.id.barChartMonth);
        barChartYear = findViewById(R.id.barChartYear);
        tvTotal = findViewById(R.id.tvTotal);
        btnSelectMonth = findViewById(R.id.btnSelectMonth);

        batchesRef = FirebaseDatabase.getInstance().getReference("batches");

        // Khởi tạo tháng/năm mặc định = hiện tại
        Calendar today = Calendar.getInstance();
        selectedMonth = today.get(Calendar.MONTH) + 1;
        selectedYear = today.get(Calendar.YEAR);
        btnSelectMonth.setText("Tháng " + selectedMonth + "/" + selectedYear);

        // Load dữ liệu lần đầu
        loadDataFromFirebase();

        btnSelectMonth.setOnClickListener(v -> openMonthYearPickerDialog());
    }

    private void openMonthYearPickerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_month_year_picker, null);

        final NumberPicker npMonth = dialogView.findViewById(R.id.npMonth);
        final NumberPicker npYear = dialogView.findViewById(R.id.npYear);

        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) months[i] = String.format(Locale.getDefault(), "%02d", i + 1);
        npMonth.setDisplayedValues(months);
        npMonth.setWrapSelectorWheel(false);
        npMonth.setValue(selectedMonth);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        npYear.setMinValue(2020);
        npYear.setMaxValue(currentYear + 5);
        npYear.setWrapSelectorWheel(false);
        npYear.setValue(selectedYear);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Chọn tháng/năm")
                .setView(dialogView)
                .setPositiveButton("OK", (d, which) -> {
                    selectedMonth = npMonth.getValue();
                    selectedYear = npYear.getValue();
                    btnSelectMonth.setText("Tháng " + selectedMonth + "/" + selectedYear);
                    loadDataFromFirebase();
                })
                .setNegativeButton("Hủy", null)
                .create();

        npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        dialog.show();
    }

    private void loadDataFromFirebase() {
        batchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalInMonth = 0;
                int[] monthlyTotals = new int[12]; // index 0 = Jan, ..., 11 = Dec

                for (DataSnapshot child : snapshot.getChildren()) {
                    Batch b = child.getValue(Batch.class);
                    if (b == null || b.getTimeOfBatch() == null) continue;

                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(sdfInput.parse(b.getTimeOfBatch()));

                        int m = cal.get(Calendar.MONTH) + 1;
                        int y = cal.get(Calendar.YEAR);

                        if (y == selectedYear) {
                            monthlyTotals[m - 1] += b.getInitialQuantity();
                            if (m == selectedMonth) {
                                totalInMonth += b.getInitialQuantity();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Dashboard", "Parse date error: " + e.getMessage());
                    }
                }

                tvTotal.setText("Tổng: " + totalInMonth);

                showMonthChart(totalInMonth);
                showYearChart(monthlyTotals);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Dashboard", "Firebase error: " + error.getMessage());
            }
        });
    }

    /** Biểu đồ tháng */
    private void showMonthChart(int total) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, total));

        BarDataSet dataSet = new BarDataSet(entries, "Tháng " + selectedMonth + "/" + selectedYear);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChartMonth.setData(barData);
        Description desc = new Description();
        desc.setText("Tổng trong tháng đã chọn");
        barChartMonth.setDescription(desc);
        barChartMonth.getXAxis().setDrawLabels(false);
        barChartMonth.getAxisLeft().setAxisMinimum(0f);
        barChartMonth.getAxisRight().setAxisMinimum(0f);
        barChartMonth.invalidate();
    }

    /** Biểu đồ năm */
    private void showYearChart(int[] monthlyTotals) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i + 1, monthlyTotals[i])); // x=1..12
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tổng Initial Quantity theo tháng - " + selectedYear);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        barChartYear.setData(barData);
        Description desc = new Description();
        desc.setText("Thống kê năm " + selectedYear);
        barChartYear.setDescription(desc);

        barChartYear.getXAxis().setGranularity(1f);
        barChartYear.getXAxis().setAxisMinimum(0f);
        barChartYear.getXAxis().setAxisMaximum(13f);

        barChartYear.getAxisLeft().setAxisMinimum(0f);
        barChartYear.getAxisRight().setAxisMinimum(0f);

        barChartYear.invalidate();
    }
}
