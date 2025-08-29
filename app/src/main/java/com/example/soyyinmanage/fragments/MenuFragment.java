package com.example.soyyinmanage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.soyyinmanage.R;
import com.example.soyyinmanage.activities.CalculatorActivity;
import com.example.soyyinmanage.activities.DashboardActivity;
import com.example.soyyinmanage.activities.LoginActivity;
import com.example.soyyinmanage.activities.NoteActivity;
import com.example.soyyinmanage.activities.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MenuFragment extends Fragment {
    Button note_btn, dashboard_btn, calculator_btn, logout_btn;

    public MenuFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        note_btn = view.findViewById(R.id.note_btn);
        dashboard_btn = view.findViewById(R.id.dashboard_btn);
        calculator_btn = view.findViewById(R.id.calculator_btn);
        logout_btn = view.findViewById(R.id.btnLogout);

        logout_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Quay về LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish(); // đóng MainActivity
        });

        calculator_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                startActivity(intent);
            }
        });

        dashboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        note_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}