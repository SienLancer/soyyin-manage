package com.example.soyyinmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.TofuAdapter;
import com.example.soyyinmanage.FirebaseRepository;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Tofu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TofuActivity extends AppCompatActivity {
    FloatingActionButton add_fab;
    FirebaseRepository myDB;
    RecyclerView tf_rv;
    SearchView name_tf_sv;
    TofuAdapter tofuAdapter;;
    ArrayList<Tofu> tofus;
    Button back_btn;
    int quantity_batch, initialQuantity;
    String id, timeOfBatch, description;
    FirebaseDatabase database;
    DatabaseReference tofusRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tofu);

        add_fab = findViewById(R.id.add_fab);
        tf_rv = findViewById(R.id.tf_rv);
        back_btn = findViewById(R.id.back_ob_btn);
        name_tf_sv = findViewById(R.id.name_tf_sv);
        back_btn.setOnClickListener(view -> finish());


        myDB = new FirebaseRepository(this);
        tofus = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        tofusRef = database.getReference("tofus");
        getTofusFromFirebase();
        //storeDataInArrays();
        getAndSetIntentData();
        ArrayList<Tofu> tofuList = new ArrayList<>();
        for (Tofu tf : tofus){
            if (tf.getBatch_id().matches(id)){
                tofuList.add(tf);
            }else {
                ArrayList<Tofu> emptyList = new ArrayList<>();
                tofus = emptyList;
            }
        }
        tofus = tofuList;

        tofuAdapter = new TofuAdapter(this, tofus);
        //getAndSetIntentData();
        tf_rv.setAdapter(tofuAdapter);
        tf_rv.setLayoutManager(new LinearLayoutManager(this));

        name_tf_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tofuAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tofuAdapter.getFilter().filter(newText);
                return false;
            }
        });

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity_batch == 0){
                    Toast.makeText(TofuActivity.this, "Hết hàng", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(TofuActivity.this, AddTofuActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("quantity_batch", quantity_batch);
                    intent.putExtra("initialQuantity", initialQuantity);
                    intent.putExtra("timeOfBatch", timeOfBatch);
                    intent.putExtra("description", description);
                    startActivity(intent);
                }
            }
        });




    }


    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("quantity")
                && getIntent().hasExtra("initialQuantity")
                && getIntent().hasExtra("timeOfBatch")
                && getIntent().hasExtra("description")) {

            // Geting Data from Intent
            id = getIntent().getStringExtra("id");
            quantity_batch = getIntent().getIntExtra("quantity", 0);
            initialQuantity = getIntent().getIntExtra("initialQuantity", 0);
            timeOfBatch = getIntent().getStringExtra("timeOfBatch");
            description = getIntent().getStringExtra("description");



        }else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }


    private void getTofusFromFirebase() {
        tofusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Tofu> tofuList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Tofu tofu = snapshot.getValue(Tofu.class);
                        if (tofu != null && tofu.getBatch_id().equals(id)) {
                            tofuList.add(tofu);
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                    }
                }

                // Cập nhật lại list chính
                tofus.clear();
                tofus.addAll(tofuList);

                // Cập nhật RecyclerView
                if (tofuAdapter == null) {
                    tofuAdapter = new TofuAdapter(TofuActivity.this, tofus);
                    tf_rv.setAdapter(tofuAdapter);
                    tf_rv.setLayoutManager(new LinearLayoutManager(TofuActivity.this));
                } else {
                    tofuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TofuActivity.this, "Failed to load tofus from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }


}