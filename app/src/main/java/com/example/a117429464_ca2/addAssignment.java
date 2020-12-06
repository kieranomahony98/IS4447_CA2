package com.example.a117429464_ca2;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class addAssignment extends Fragment {
    Button btnCreateAssignment;
    EditText etTitle, etmlDescription;
    Spinner spImportance;
    DatePicker spDatePicker;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_assignment, container, false);
        attachResources(v);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Add Assignment");

        btnCreateAssignment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                try {
                    Boolean isAdded = createAssignment();
                    if (isAdded == true) {
                        etTitle.setText("");
                        etmlDescription.setText("");
                        spImportance.setSelection(0);
                        Toast.makeText(v.getContext(), "Assignment Successfully Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "ERROR: Failed to create new assignment", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("Add Assignment", "Failed to add assignment" + e.getMessage());
                    throw e;
                }
            }
        });
        return v;
    }

    public boolean createAssignment(){
        String title = etTitle.getText().toString();
        String duedate = (spDatePicker.getDayOfMonth()) + "/" + (spDatePicker.getMonth() + 1) + "/" + spDatePicker.getYear();
        String description = etmlDescription.getText().toString();
        String importance = spImportance.getSelectedItem().toString();
        Boolean isValidated = validation(title, description, duedate);
        if(isValidated == true){
            AssignmentModel newAssignment = new AssignmentModel(-1, title, importance, duedate, description, false);

            DatabaseHelper db = new DatabaseHelper(this.getContext());
            boolean isAdded = db.addAssignment(newAssignment);
            return isAdded;
        }
        return false;
    }

    private boolean validation(String title, String description, String date) {
        Date today = new Date();
        if(title.matches("")){
            Toast.makeText(this.getContext(), "Please enter a valid title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(description.matches("")){
            Toast.makeText(this.getContext(), "Please enter a valid description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(today.after(parseDate(date))){
            Toast.makeText(this.getContext(), "You cannot make historical assignments", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void attachResources(View v){
        btnCreateAssignment = v.findViewById(R.id.btnCreateAssignment);
        etTitle = v.findViewById(R.id.etTitle);
        etmlDescription = v.findViewById(R.id.etmlDescription);
        spImportance = v.findViewById(R.id.spImportance);
        spDatePicker = v.findViewById(R.id.spDatePicker);
    }

    public Date parseDate(String date){
        try{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }catch (ParseException e){
            Log.e("Add Assignment", "Failed to parse date: " + e.getMessage());
        }
        return null;
    }
}
