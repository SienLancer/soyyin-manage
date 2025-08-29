package com.example.soyyinmanage.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.NoteAdapter;
import com.example.soyyinmanage.R;
import com.example.soyyinmanage.models.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> notes;
    private EditText etNote;
    private ImageButton btnAdd;

    private DatabaseReference notesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        etNote = findViewById(R.id.etNote);
        btnAdd = findViewById(R.id.btnAdd);

        notes = new ArrayList<>();
        adapter = new NoteAdapter(this, notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        notesRef = FirebaseDatabase.getInstance().getReference("notes");

        // Lắng nghe dữ liệu realtime
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Note note = data.getValue(Note.class);
                    if (note != null) {
                        notes.add(note);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NoteActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm ghi chú mới
        btnAdd.setOnClickListener(v -> {
            String content = etNote.getText().toString().trim();
            if (!content.isEmpty()) {
                String id = notesRef.push().getKey(); // tạo key tự động
                Note newNote = new Note(id, content, false);

                notesRef.child(id).setValue(newNote)
                        .addOnSuccessListener(unused -> {
                            etNote.setText("");
                            Toast.makeText(this, "Đã thêm ghi chú!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
