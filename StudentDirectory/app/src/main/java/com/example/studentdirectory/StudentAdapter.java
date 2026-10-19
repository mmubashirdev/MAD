package com.example.studentdirectory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;

    public interface OnStudentActionListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
        void onOverflowClick(View anchorView, int position);
    }

    private final OnStudentActionListener listener;

    public StudentAdapter(List<Student> studentList, OnStudentActionListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvStudentDept, btnItemOverflow;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentDept = itemView.findViewById(R.id.tvStudentDept);
            btnItemOverflow = itemView.findViewById(R.id.btnItemOverflow);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(position);
                }
                return true;
            });

            btnItemOverflow.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOverflowClick(v, position);
                }
            });
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student s = studentList.get(position);
        holder.tvStudentName.setText(s.getName());
        holder.tvStudentDept.setText(s.getDepartment());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void sortByName() {
        Collections.sort(studentList, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        notifyDataSetChanged();
    }
}
