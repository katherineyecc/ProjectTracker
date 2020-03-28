package com.example.projecttracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<Project> projects;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    Adapter(Context context, List<Project> projects){
        this.inflater = LayoutInflater.from(context);
        this.projects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = inflater.inflate(R.layout.list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i){
        System.out.println("Inside the onBindViewHolder...");
        System.out.println("project file name: "+projects.get(i).getCourseTitle());
        String courseName = projects.get(i).getCourseTitle();
        Date dueDate = projects.get(i).getDueDate();
        String pDueDate = ft.format(dueDate);
        Boolean isCompleted = projects.get(i).getIsCompleted();
        String projectId = projects.get(i).getProjectNumber();

        viewHolder.pCourseName.setText(courseName);
        viewHolder.pDueDate.setText(pDueDate);
        if(isCompleted == true){
            viewHolder.pStatus.setText("Completed");
        } else{
            viewHolder.pStatus.setText("In Progress");
        }
        viewHolder.listId.setText(projectId);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pCourseName, pDueDate, pStatus, listId;

        public ViewHolder(@NonNull final View itemView){
            super(itemView);
            pCourseName = itemView.findViewById(R.id.pCourseName);
            pDueDate = itemView.findViewById(R.id.pDueDate);
            pStatus = itemView.findViewById(R.id.pStatus);
            listId = itemView.findViewById(R.id.listId);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent i = new Intent(v.getContext(), Detail.class);
                    i.putExtra("ID", projects.get(getAdapterPosition()).getProjectNumber());
                    v.getContext().startActivity(i);
                }
            });
        }
    }


}
