package com.example.a117429464_ca2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class viewAssignments extends Fragment implements AdapterView.OnItemSelectedListener {
    List<AssignmentModel> assignmentModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Spinner spFilter;
    RecyclerAdapter rAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            View v = inflater.inflate(R.layout.fragment_view_assignments, container, false);
            recyclerView = v.findViewById(R.id.rvAssignments);

            ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();

            assert actionBar != null;
            actionBar.setTitle("View Assignments");
            spFilter = v.findViewById(R.id.spFilter);
            spFilter.setOnItemSelectedListener(this);
            return v;
        }catch (Exception e){
            Log.e("View Assignments", "Failed to create view: " +  e.getMessage());
        }
        return null;
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Intent singleAssignment = new Intent(getActivity(), singleAssignment.class);
            String convertedAssignment = new Gson().toJson(assignmentModels.get(position));
            singleAssignment.putExtra("assignment", convertedAssignment);
            startActivity(singleAssignment);
        }
    };

    @Override
    public void onResume() {
        try {
            super.onResume();
            DatabaseHelper db = DatabaseHelper.getInstance(this.getContext());
            assignmentModels = db.getAssignments();
            createRecylerView();
        } catch (Exception e) {
            Log.e("View Assignments", "Error on Resume" + e.getMessage());
            throw e;
        }

    }

    public void createRecylerView() {
        try {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(layoutManager);
             rAdapter = new RecyclerAdapter(assignmentModels, this.getContext());
            recyclerView.setAdapter(rAdapter);
            rAdapter.setOnItemClickListener(onItemClickListener);

        } catch (Exception e) {
            Log.e("Recycler View Fragment", "Error creating the recycler view" + e.getMessage());
            throw e;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        DatabaseHelper db = DatabaseHelper.getInstance(this.getContext());
        assignmentModels = spFilter.getItemAtPosition(pos) == "ALL" ? db.getAssignments() : db.getAssignments(String.valueOf(spFilter.getItemAtPosition(pos)));
        rAdapter.notifyDataSetChanged();
        createRecylerView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}