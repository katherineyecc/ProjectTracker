package com.example.projecttracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private LayoutInflater inflater;
    private Map<Integer, Project> projects;
    private Map<Integer, Integer> adapterMap = new HashMap<>();
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    Adapter(Context context, Map<Integer, Project> projects){
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
        if(projects.get(i) != null){
            System.out.println("Inside the onBindViewHolder...");
            System.out.println("i = "+i);
            System.out.println("projects size is: "+projects.size());
            System.out.println("project file name: " + projects.get(i).getCourseTitle());
            String courseName = projects.get(i).getCourseTitle();
            Date dueDate = projects.get(i).getDueDate();
            String pDueDate = "";
            try{
                pDueDate = ft.format(dueDate);
            }catch (Exception e){
                System.out.println("Exception occured");
            }
            Boolean isCompleted = projects.get(i).getIsCompleted();
            String projectId = projects.get(i).getProjectNumber();
            adapterMap.put(i, Integer.parseInt(projectId));
            System.out.println("adapterMap is: <" + i + ", " + Integer.parseInt(projectId) + ">");
            viewHolder.pCourseName.setText(courseName);
            viewHolder.pDueDate.setText(pDueDate);
            if (isCompleted == true) {
                viewHolder.pStatus.setText("Completed");
            } else {
                viewHolder.pStatus.setText("In Progress");
            }
            viewHolder.listId.setText(projectId);
            //adapterIndex++;
        }

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
                    System.out.println("Adapter position is: "+ getAdapterPosition());
                    Intent i = new Intent(v.getContext(), Detail.class);
                    int projectId = adapterMap.get(getAdapterPosition());
                    System.out.println("projectId: "+projectId);
                    i.putExtra("ID", Integer.toString(projectId));
                    v.getContext().startActivity(i);
                }
            });
        }
    }


}
