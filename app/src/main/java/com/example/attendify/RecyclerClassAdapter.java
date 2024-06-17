package com.example.attendify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerClassAdapter extends RecyclerView.Adapter<RecyclerClassAdapter.ViewHolder> {

    private OnClickListener onClickListener;
    Context context;
    ArrayList<ClassModel> classList;

    private int position;
    RecyclerClassAdapter(Context context,ArrayList<ClassModel> classList){
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.classes_row, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {

        holder.classTitle.setText(classList.get(position).classTitle);
        holder.classBranch.setText(classList.get(position).classBranch);
        holder.classSem.setText(classList.get(position).classSem);
        holder.studentCount.setText(classList.get(position).studentCount);



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.onClick(position,classList.get(position));
                }
            }
        });
    }

    public int getPosition(){
        return  this.position;
    }
    public  void setPosition(int position){
        this.position = position;
    }



    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener{
        void onClick(int position,ClassModel classDetail);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView classTitle,classBranch,classSem,studentCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            classTitle = itemView.findViewById(R.id.txtClassTitle);
            classBranch = itemView.findViewById(R.id.txtClassBranch);
            classSem = itemView.findViewById(R.id.txtClassSem);
            studentCount = itemView.findViewById(R.id.txtStudentCount);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), v.getId(), 0, "Edit");
            menu.add(this.getAdapterPosition(), v.getId(), 1, "Delete");
        }

    }
}
