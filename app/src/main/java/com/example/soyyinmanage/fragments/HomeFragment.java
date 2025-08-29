package com.example.soyyinmanage.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.BatchAdapter;
import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.activities.NewBatchActivity;
import com.example.soyyinmanage.models.Batch;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RecyclerView rv;
    FirebaseRepository myDB;
    ArrayList<Batch> batches;
    BatchAdapter batchAdapter;
    FloatingActionButton add_new_batch_fab;

    // Firebase references
    FirebaseDatabase database;
    DatabaseReference batchesRef;

    // Shimmer
    ShimmerFrameLayout shimmerFrameLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        add_new_batch_fab = view.findViewById(R.id.add_new_batch_fab);
        rv = view.findViewById(R.id.rv);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        myDB = new FirebaseRepository(getActivity());
        batches = new ArrayList<>();
        batchAdapter = new BatchAdapter(getActivity(), batches);

        rv.setAdapter(batchAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Start shimmer when loading
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        getBatchesFromFirebase();

        add_new_batch_fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewBatchActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void getBatchesFromFirebase() {
        database = FirebaseDatabase.getInstance();
        batchesRef = database.getReference("batches");

        batchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                batches.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Batch batch = snapshot.getValue(Batch.class);
                        if (batch != null) {
                            batches.add(batch);
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                    }
                }

                // Sort by time (newest first)
                Collections.sort(batches, (b1, b2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
                        Date d1 = sdf.parse(b1.getTimeOfBatch());
                        Date d2 = sdf.parse(b2.getTimeOfBatch());
                        return d2.compareTo(d1);
                    } catch (Exception e) {
                        return 0;
                    }
                });

                batchAdapter.notifyDataSetChanged();

                // Stop shimmer and show RecyclerView
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load data", Toast.LENGTH_SHORT).show();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        });
    }
}
