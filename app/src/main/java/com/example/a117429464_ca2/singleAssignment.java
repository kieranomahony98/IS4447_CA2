package com.example.a117429464_ca2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class singleAssignment extends AppCompatActivity implements View.OnClickListener {
    DatePicker spDatePicker;
    EditText etTitle, etmlDescription;
    CheckBox cbCompleted;
    Spinner spImportance;
    Button btnSave, btnDelete, btnAddToCalender;
    AssignmentModel assignment;
    private int WRITE_CALENDER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_assignment);
        attachResources();
        ActionBar actionBar = getSupportActionBar();

        assignment = new Gson().fromJson(Objects.requireNonNull(getIntent().getExtras()).getString("assignment"), AssignmentModel.class);
        if (actionBar != null) {
            actionBar.setTitle(assignment.getTitle());
        }

        populateActivity(assignment);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnAddToCalender.setOnClickListener(this);
    }

    private String[] formatedDate(String dueDate) {
        return dueDate.split("/");
    }

    private void populateActivity(AssignmentModel assignment) {
        String[] date = formatedDate(assignment.getDueDate());
        spDatePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
        etTitle.setText(assignment.getTitle());
        etmlDescription.setText(assignment.getDescription());
        int importance = getPosition(assignment.getImportance());
        if(importance != -1){
            spImportance.setSelection(getPosition(assignment.getImportance()));
        }
        cbCompleted.setChecked(assignment.isCompleted());


    }

    private int getPosition(String importance) {
        try{
            Integer i = mImportance.get(importance);
            if(i != null){
                return i;
            }else{
                Toast.makeText(singleAssignment.this, "Failed to find importance", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            Log.e("Single Assignment", "Failed to get importance: " + e.getMessage());
        }
        return -1;

    }

    public static Map<String, Integer> mImportance = new HashMap<String, Integer>() {
        {
            put("Highest", 0);
            put("High", 1);
            put("Medium", 2);
            put("Low", 3);
            put("Lowest", 4);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        String title = etTitle.getText().toString();
        String description = etmlDescription.getText().toString();
        String importance = spImportance.getSelectedItem().toString();
        String duedate = (spDatePicker.getDayOfMonth()) + "/" + (spDatePicker.getMonth() + 1) + "/" + spDatePicker.getYear();
        boolean completed = cbCompleted.isChecked();
        if (!title.matches("") && !description.matches("")) {
            int id = assignment.getId();
            DatabaseHelper db = DatabaseHelper.getInstance(v.getContext());
            switch (v.getId()) {
                case R.id.btnSave:


                    AssignmentModel assignment = new AssignmentModel(id, title, importance, duedate, description, completed);
                    boolean updated = db.updateAssignment(assignment);
                    if (updated) {
                        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        break;

                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                        break;
                    }

                case R.id.btnDelete:
                    boolean deleted =  db.deleteAssignments(id);
                    if(deleted){
                        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(this, "Failed to delete assignment", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnAddToCalender:
                    try {
                        checkPermission();
                    } catch (Exception e) {
                        Log.e("Single Assignment", "Error adding to calender: " + e.getMessage());
                    }
            }
        } else {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show();
        }
    }

    public void attachResources() {
        spDatePicker = findViewById(R.id.spDatePicker);
        etTitle = findViewById(R.id.tvTitle);
        etmlDescription = findViewById(R.id.etmlDescription);
        spImportance = findViewById(R.id.spImportance);
        cbCompleted = findViewById(R.id.cbCompleted);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddToCalender = findViewById(R.id.btnAddToCalender);
    }
    //https://developer.android.com/guide/topics/providers/calendar-provider docs for adding to calender
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void addToCalender()  {
//        if (google_sign_in.account != null) {
//            try{
//                long dateInMillis = Objects.requireNonNull(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(assignment.getDueDate())).getTime();
//                ContentResolver cr = this.getContentResolver();
//                ContentValues values = new ContentValues();
//                values.put(CalendarContract.Events.DTSTART, dateInMillis);
//                values.put(CalendarContract.Events.DTEND, dateInMillis + 86400000); //one day in milliseconds
//                values.put(CalendarContract.Events.TITLE, assignment.getTitle());
//                values.put(CalendarContract.Events.DESCRIPTION, assignment.getDescription());
//                values.put(CalendarContract.Events.EVENT_TIMEZONE, "EUROPE/DUBLIN");
//                values.put(CalendarContract.Events.CALENDAR_ID, 1);
//                cr.insert(CalendarContract.Events.CONTENT_URI, values);
//
//                Toast.makeText(this, "Successfully added to your calender", Toast.LENGTH_SHORT).show();
//            }catch (Exception e){
//                Log.e("Calender Error", "Failed to add to calender" + e.getMessage());
//                Toast.makeText(singleAssignment.this, "Failed to add to calender, please try again", Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            Toast.makeText(singleAssignment.this, "Please sign in using the account tab", Toast.LENGTH_SHORT).show();
//        }
//    }
    private void addToCalender()  {
        if (google_sign_in.account != null) {
            try{
                long dateInMillis = Objects.requireNonNull(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(assignment.getDueDate())).getTime();
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateInMillis)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,  dateInMillis + 86400000)
                        .putExtra(CalendarContract.Events.TITLE, assignment.getTitle())
                        .putExtra(CalendarContract.Events.DESCRIPTION, assignment.getDescription())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "UCC");
                startActivity(intent);

                Toast.makeText(this, "Off you go to your calendar", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.e("Calender Error", "Failed to add to calender" + e.getMessage());
                Toast.makeText(singleAssignment.this, "Failed to add to calender, please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(singleAssignment.this, "Please sign in using the account tab", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(singleAssignment.this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            addToCalender();
        }else{
            requestCalenderPermission();
        }
    }

    private void requestCalenderPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(singleAssignment.this,Manifest.permission.WRITE_CALENDAR )){
            new AlertDialog.Builder(singleAssignment.this)
                    .setTitle("Calender Permission")
                    .setMessage("This is required to write to your calender")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(singleAssignment.this, new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDER );
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else {
            ActivityCompat.requestPermissions(singleAssignment.this, new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDER );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_CALENDER) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    addToCalender();
                } catch (Exception e) {
                    Log.e("Request Permission", "Error in Permissions: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d("Calender", "Added to calender");
        }
    }
}
