package com.example.soyyinmanage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.soyyinmanage.models.Batch;
import com.example.soyyinmanage.models.Tofu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRepository {
    private Context context;
    private DatabaseReference databaseBatchRef;
    private DatabaseReference databaseTofuRef;

    public FirebaseRepository(Context context) {
        this.context = context;
        databaseBatchRef = FirebaseDatabase.getInstance().getReference("batches");
        databaseTofuRef = FirebaseDatabase.getInstance().getReference("tofus");
    }

    // ========================== BATCH ==========================

    // Thêm batch
    public void addBatch(Batch batch) {
        if (batch.getId() == null || batch.getId().isEmpty()) {
            // Nếu chưa có id thì tự tạo key Firebase
            String key = databaseBatchRef.push().getKey();
            batch.setId(key);
        }
        databaseBatchRef.child(batch.getId()).setValue(batch)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Batch added successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add batch.", Toast.LENGTH_SHORT).show()
                );
    }

    // Lấy toàn bộ batch
    public void getAllBatches(ValueEventListener listener) {
        databaseBatchRef.addValueEventListener(listener);
    }

    // Cập nhật batch
    public void updateBatch(Batch batch) {
        databaseBatchRef.child(batch.getId()).setValue(batch)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Batch updated successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update batch.", Toast.LENGTH_SHORT).show()
                );
    }

    // Xóa 1 batch + các tofu liên quan
    public void deleteBatch(String batchId) {
        // Xóa tất cả tofu liên quan trước
        databaseTofuRef.orderByChild("batch_id").equalTo(batchId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                        // Xóa batch
                        databaseBatchRef.child(batchId).removeValue()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(context, "Batch and related Tofu deleted!", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed to delete batch.", Toast.LENGTH_SHORT).show()
                                );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to delete related tofu: " + error.getMessage());
                    }
                });
    }

    // Xóa tất cả batches và tofus
    public void deleteAllBatches() {
        databaseBatchRef.removeValue();
        databaseTofuRef.removeValue();
        Toast.makeText(context, "Deleted all batches and tofus!", Toast.LENGTH_SHORT).show();
    }

    // ========================== TOFU ==========================

    public void addTofu(Tofu tofu) {
        if (tofu.getId() == null || tofu.getId().isEmpty()) {
            String key = databaseTofuRef.push().getKey();
            tofu.setId(key);
        }
        databaseTofuRef.child(tofu.getId()).setValue(tofu)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Tofu added successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add tofu.", Toast.LENGTH_SHORT).show()
                );
    }

    public void getAllTofus(ValueEventListener listener) {
        databaseTofuRef.addValueEventListener(listener);
    }

    public void updateTofu(Tofu tofu) {
        databaseTofuRef.child(tofu.getId()).setValue(tofu)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Tofu updated successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update tofu.", Toast.LENGTH_SHORT).show()
                );
    }

    public void deleteTofu(String tofuId) {
        databaseTofuRef.child(tofuId).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Tofu deleted successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to delete tofu.", Toast.LENGTH_SHORT).show()
                );
    }
}
