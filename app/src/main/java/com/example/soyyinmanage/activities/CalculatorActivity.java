package com.example.soyyinmanage.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.R;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvInput, tvResult;
    private String currentInput = "";
    private String operator = "";
    private double firstValue = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tvInput = findViewById(R.id.tvInput);
        tvResult = findViewById(R.id.tvResult);

        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv,
                R.id.btnClear, R.id.btnEqual
        };

        for (int id : btnIds) findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String t = b.getText().toString();

        int id = v.getId();

        if (id == R.id.btnClear) {
            resetAll();
        } else if (id == R.id.btnAdd || id == R.id.btnSub || id == R.id.btnMul || id == R.id.btnDiv) {
            handleOperator(t);
        } else if (id == R.id.btnEqual) {
            handleEqual();
        } else if (id == R.id.btnDot) {
            handleDot();
        } else {
            // số 0-9
            appendNumber(t);
        }
    }


    private void appendNumber(String n) {
        currentInput += n;
        tvInput.setText(tvInput.getText().toString() + n);
    }

    private void handleDot() {
        // Không cho nhập 2 dấu chấm trong một số
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) {
                currentInput = "0.";
                tvInput.setText(tvInput.getText().toString() + "0.");
            } else {
                currentInput += ".";
                tvInput.setText(tvInput.getText().toString() + ".");
            }
        }
    }

    private void handleOperator(String op) {
        // Nếu chưa nhập số, cho phép thay đổi operator cuối
        if (currentInput.isEmpty() && !Double.isNaN(firstValue)) {
            // thay operator hiển thị
            String display = tvInput.getText().toString();
            if (display.endsWith("+") || display.endsWith("-") || display.endsWith("×") || display.endsWith("÷")) {
                tvInput.setText(display.substring(0, display.length() - 1) + op);
            }
            operator = op;
            return;
        }

        if (!currentInput.isEmpty()) {
            if (Double.isNaN(firstValue)) {
                firstValue = Double.parseDouble(currentInput);
            } else {
                calculate(Double.parseDouble(currentInput));
            }
            operator = op;
            currentInput = "";
            tvInput.setText(firstValue + " " + operator);
            tvResult.setText(String.valueOf(firstValue));
        }
    }

    private void handleEqual() {
        if (!currentInput.isEmpty() && !Double.isNaN(firstValue) && !operator.isEmpty()) {
            calculate(Double.parseDouble(currentInput));
            tvInput.setText("");
            tvResult.setText(String.valueOf(firstValue));
            operator = "";
            currentInput = "";
        }
    }

    private void calculate(double secondValue) {
        switch (operator) {
            case "+": firstValue = firstValue + secondValue; break;
            case "-": firstValue = firstValue - secondValue; break;
            case "×": firstValue = firstValue * secondValue; break;
            case "÷":
                if (secondValue == 0) {
                    tvResult.setText("Error");
                    firstValue = Double.NaN;
                    return;
                } else {
                    firstValue = firstValue / secondValue;
                }
                break;
        }
    }

    private void resetAll() {
        currentInput = "";
        operator = "";
        firstValue = Double.NaN;
        tvInput.setText("");
        tvResult.setText("0");
    }
}
