package com.example.soyyinmanage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.models.Note;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private ArrayList<Note> notes;

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.tvContent.setText(note.getContent());
        holder.cbDone.setChecked(note.isDone());

        // Nếu đã tick thì gạch ngang text
        if (note.isDone()) {
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvContent.setPaintFlags(holder.tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            note.setDone(isChecked);

            // Cập nhật trạng thái lên Firebase Realtime DB
            FirebaseDatabase.getInstance()
                    .getReference("notes")
                    .child(note.getId())
                    .child("done")
                    .setValue(isChecked);

            notifyItemChanged(position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn xóa ghi chú này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        FirebaseDatabase.getInstance()
                                .getReference("notes")
                                .child(note.getId())
                                .removeValue()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        holder.tvContent.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Sửa ghi chú");

            final EditText input = new EditText(context);
            input.setText(note.getContent());
            builder.setView(input);

            builder.setPositiveButton("Lưu", (dialog, which) -> {
                String newContent = input.getText().toString().trim();
                if (!newContent.isEmpty()) {
                    FirebaseDatabase.getInstance()
                            .getReference("notes")
                            .child(note.getId())
                            .child("content")
                            .setValue(newContent);
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.show();
        });



    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvContent;
        ImageButton btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cbDone);
            tvContent = itemView.findViewById(R.id.tvContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
