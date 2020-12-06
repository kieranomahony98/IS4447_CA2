package com.example.a117429464_ca2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    List<AssignmentModel> assignments;
    Context context;
    private View.OnClickListener ocListener;

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        ocListener = itemClickListener;
    }

    public RecyclerAdapter(List<AssignmentModel> assignments, Context context) {
        this.assignments = assignments;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_one_line_assignment, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(assignments.get(position).getTitle());
        holder.tvDueDate.setText(assignments.get(position).getDueDate());
        holder.tvCompleted.setText(String.valueOf(assignments.get(position).isCompleted()));
        holder.tvImportance.setText(assignments.get(position).getImportance());
        holder.tvDescription.setText(assignments.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvTitle, tvDueDate, tvImportance, tvCompleted;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvImportance = itemView.findViewById(R.id.tvImportance);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);
            itemView.setTag(this);
            itemView.setOnClickListener(ocListener);
        }
    }
}
