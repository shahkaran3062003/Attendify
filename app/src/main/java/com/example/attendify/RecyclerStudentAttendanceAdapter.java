package com.example.attendify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RecyclerStudentAttendanceAdapter extends RecyclerView.Adapter<RecyclerStudentAttendanceAdapter.ViewHolder> {

    Context context;
    ArrayList<StudentAttendanceModel> studentList;

    private OnItemClickListener mListner;

    private String baseImagePath = "https://testing306.000webhostapp.com/";





    RecyclerStudentAttendanceAdapter(Context context,ArrayList<StudentAttendanceModel> studentList){
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_attendance_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListner);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtStudentName.setText(studentList.get(position).name);
        holder.txtStudentRollNo.setText(studentList.get(position).rollNo);

        if(studentList.get(position).isPresent.equals("0")){
            holder.ll.setBackground(context.getDrawable(R.drawable.absent_gradiant_bg));
        }else{
            holder.ll.setBackground(context.getDrawable(R.drawable.present_gradiant_bg));
        }

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

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public  interface OnItemClickListener{
        void onPresentClick(int position);
        void onAbsentClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListner = listener;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtStudentName,txtStudentRollNo;
        MaterialButton btnPresent,btnAbsent;
        ShapeableImageView profilePic;
        LinearLayout ll;

        public ViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);

            ll = itemView.findViewById(R.id.llParent);
            profilePic = itemView.findViewById(R.id.imgStudentProfilePicAttendance);
            txtStudentName = itemView.findViewById(R.id.txtStudentNameAttendance);
            txtStudentRollNo = itemView.findViewById(R.id.txtStudentRollNoAttendance);
            btnPresent = itemView.findViewById(R.id.btnPresent);
            btnAbsent = itemView.findViewById(R.id.btnAbsent);

            btnPresent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
//                            ll.setBackground(context.getDrawable(R.drawable.present_gradiant_bg));
                            studentList.get(position).setPresent();
                            listener.onPresentClick(position);
                            notifyItemChanged(position);
                        }
                    }
                }
            });

            btnAbsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
//                            ll.setBackground(context.getDrawable(R.drawable.absent_gradiant_bg));
                            studentList.get(position).setAbsent();
                            listener.onAbsentClick(position);
                            notifyItemChanged(position);
                        }
                    }
                }
            });
        }
    }
}
