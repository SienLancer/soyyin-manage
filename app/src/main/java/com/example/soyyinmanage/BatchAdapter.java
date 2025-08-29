package com.example.soyyinmanage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.activities.TofuActivity;
import com.example.soyyinmanage.activities.DetailActivity;
import com.example.soyyinmanage.activities.MainActivity;
import com.example.soyyinmanage.activities.UpdateActivity;
import com.example.soyyinmanage.models.Batch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.MyViewHolder>{

    private Context context;
    ArrayList<Batch> batches;


    public BatchAdapter(Context context, ArrayList<Batch> batches) {
        this.context = context;
        this.batches = batches;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Batch batch = batches.get(position);
        holder.tob_row_txt.setText(batch.getTimeOfBatch());
        if (batch.getQuantity() == 0) {
            holder.quantity_row_txt.setText("ĐÃ BÁN HẾT " + batch.getInitialQuantity() + " HỦ");
            holder.quantity_row_txt.setTextColor(
                    context.getResources().getColor(android.R.color.holo_red_dark)
            );
        } else {
            holder.quantity_row_txt.setText(String.valueOf(batch.getQuantity()));
            holder.quantity_row_txt.setTextColor(
                    context.getResources().getColor(android.R.color.black) // hoặc màu mặc định
            );
        }

        //holder.dow_row_txt.setText(batch.getDescription());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Batch batch1 = batches.get(position);
                intent.putExtra("id", batch1.getId());
                intent.putExtra("timeOfBatch", batch1.getTimeOfBatch());
                intent.putExtra("quantity", batch1.getQuantity());
                intent.putExtra("initialQuantity", batch1.getInitialQuantity());
                intent.putExtra("description", batch1.getDescription());

                context.startActivity(intent);
            }
        });

        holder.delete_btn_one.setOnClickListener(view -> {
            showPasswordDialog("Xác nhận mật khẩu để xóa", () -> {
                FirebaseRepository myDB = new FirebaseRepository(context);
                myDB.deleteBatch(batch.getId());
                Intent intent = new Intent(context, MainActivity.class);
                ((Activity) context).startActivity(intent);
            });
        });

        holder.update_btn_out.setOnClickListener(view -> {
            showPasswordDialog("Xác nhận mật khẩu để cập nhật", () -> {
                Intent intent = new Intent(context, UpdateActivity.class);
                Batch batch1 = batches.get(position);
                intent.putExtra("id", batch1.getId());
                intent.putExtra("timeOfBatch", batch1.getTimeOfBatch());
                intent.putExtra("quantity", batch1.getQuantity());
                intent.putExtra("initialQuantity", batch1.getInitialQuantity());
                intent.putExtra("description", batch1.getDescription());
                context.startActivity(intent);
            });
        });



        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TofuActivity.class);
                Batch batch1 = batches.get(position);
                intent.putExtra("id", batch1.getId());
                intent.putExtra("quantity", batch1.getQuantity());
                intent.putExtra("initialQuantity", batch1.getInitialQuantity());
                intent.putExtra("timeOfBatch", batch1.getTimeOfBatch());
                intent.putExtra("description", batch1.getDescription());

                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return batches.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tob_row_txt, quantity_row_txt, dow_row_txt;
        Button delete_btn_one, update_btn_out, more_btn;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tob_row_txt = itemView.findViewById(R.id.tob_row_txt);
            quantity_row_txt = itemView.findViewById(R.id.quantity_row_txt);
//            dow_row_txt = itemView.findViewById(R.id.dow_row_txt);
            delete_btn_one = itemView.findViewById(R.id.delete_btn_row);
            update_btn_out = itemView.findViewById(R.id.update_btn_out);
            more_btn = itemView.findViewById(R.id.more_btn);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }


    private void showPasswordDialog(String title, Runnable onSuccess) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);

        // Inflate layout custom
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);
        builder.setView(dialogView);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String password = etPassword.getText().toString().trim();
            if (password.equals("270701")) {
                onSuccess.run(); // chạy callback nếu đúng mật khẩu
            } else {
                Toast.makeText(context, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }





}
