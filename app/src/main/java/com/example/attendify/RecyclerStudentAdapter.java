package com.example.attendify;

import android.content.Context;
import android.content.res.Configuration;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RecyclerStudentAdapter extends RecyclerView.Adapter<RecyclerStudentAdapter.ViewHolder> {

    Context context;
    ArrayList<StudentModel> studentList;
    private int position;
    private String baseImagePath = "https://testing306.000webhostapp.com/";


    RecyclerStudentAdapter(Context context,ArrayList<StudentModel> studentList){
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.student_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtStudentName.setText(studentList.get(position).name);
        holder.txtStudentRollNo.setText(studentList.get(position).rollNo);
        holder.txtStudentContact.setText(studentList.get(position).contact);

        String imgPath = studentList.get(position).profilePicPath;
        imgPath = imgPath.substring(6);
        imgPath = baseImagePath+imgPath;

        int errorDrawable;

        if((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES){
            errorDrawable = R.drawable.student_profile_dark;
        }else{
            errorDrawable = R.drawable.student_profile_light;
        }

        Glide.with(context)
                .load(imgPath)
                .placeholder(errorDrawable)
                .error(errorDrawable)
                .into(holder.profilePic);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return  this.position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView txtStudentName,txtStudentRollNo,txtStudentContact;
        ShapeableImageView profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.imgStudentProfilePic);
            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentRollNo = itemView.findViewById(R.id.txtStudentRollNo);
            txtStudentContact = itemView.findViewById(R.id.txtStudentContact);




            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),v.getId(),0,"Remove");
        }
    }
}
