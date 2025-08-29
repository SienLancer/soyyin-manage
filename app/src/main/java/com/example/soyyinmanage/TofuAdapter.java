package com.example.soyyinmanage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soyyinmanage.activities.MainActivity;
import com.example.soyyinmanage.activities.UpdateTofuActivity;
import com.example.soyyinmanage.models.Tofu;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class TofuAdapter extends RecyclerView.Adapter<TofuAdapter.MyViewHolder> implements Filterable {

    private Context context;
    String id;
    ArrayList<Tofu> tofus;
    ArrayList<Tofu> tofusSearches;

    public TofuAdapter(Context context, ArrayList<Tofu> tofus) {
        this.context = context;
        this.tofus = tofus;
        this.tofusSearches = tofus;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewo = inflater.inflate(R.layout.tofu_row, parent, false);
        return new MyViewHolder(viewo);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tofu tf =tofus.get(position);
        holder.quantity_tofu_row_txt.setText("Số lượng: " + String.valueOf(tf.getQuantity()));
        holder.date_tofu_row_txt.setText("Giờ bán: " + tf.getDate());
        holder.typeofsale_tofu_row_txt.setText("Loại bán: " + tf.getTypeOfSale());
        holder.comment_row_tofu_txt.setText("Mô tả: " + tf.getComment());




        holder.update_btn_ob.setOnClickListener(view -> {
            showPasswordDialog("Xác nhận mật khẩu để cập nhật", () -> {
                Intent intent = new Intent(context, UpdateTofuActivity.class);
                Tofu tofu1 =tofus.get(position);
                intent.putExtra("tf_id", tofu1.getId());
                intent.putExtra("quantity", tofu1.getQuantity());
                intent.putExtra("date", tofu1.getDate());
                intent.putExtra("typeOfSale", tofu1.getTypeOfSale());
                intent.putExtra("comment", tofu1.getComment());
                intent.putExtra("batch_id", tofu1.getBatch_id());


                context.startActivity(intent);
            });
        });

        holder.delete_btn_ob_row.setOnClickListener(view -> {
            showPasswordDialog("Xác nhận mật khẩu để xóa", () -> {
                FirebaseRepository myDB = new FirebaseRepository(context);
                myDB.deleteTofu(tf.getId());
                Intent intent = new Intent(context, MainActivity.class);
                ((Activity) context).startActivity(intent);
            });
        });

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


    @Override
    public int getItemCount() {
        return tofus.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quantity_tofu_row_txt, date_tofu_row_txt, typeofsale_tofu_row_txt, comment_row_tofu_txt;
        Button delete_btn_ob_row, update_btn_ob;
        LinearLayout mainLayout_ob;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity_tofu_row_txt = itemView.findViewById(R.id.quantity_tofu_row_txt);
            date_tofu_row_txt = itemView.findViewById(R.id.date_tofu_row_txt);
            typeofsale_tofu_row_txt = itemView.findViewById(R.id.typeofsale_tofu_row_txt);
            update_btn_ob = itemView.findViewById(R.id.update_btn_ob);
            delete_btn_ob_row = itemView.findViewById(R.id.delete_btn_ob_row);
            comment_row_tofu_txt = itemView.findViewById(R.id.comment_row_tofu_txt);
            mainLayout_ob = itemView.findViewById(R.id.mainLayout_ob);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString();
                if (searchText.isEmpty()){
                    tofus = tofusSearches;
                }else {
                    ArrayList<Tofu> hl = new ArrayList<>();
                    for (Tofu tf : tofusSearches){
                        if (tf.getDate().toLowerCase().contains(searchText.toLowerCase())){
                            hl.add(tf);
                        }else {
                            ArrayList<Tofu> emptyList = new ArrayList<>();
                            tofus = emptyList;
                        }
                    }
                    tofus = hl;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tofus;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tofus = (ArrayList<Tofu>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
