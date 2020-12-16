package com.example.a117429464_ca2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String ASSIGNMENT_TABLE = "ASSIGNMENT_TABLE";
    public static final int VERSION = 1;
    public static final String COLUMN_ASSIGNMENT_ID = "ID";
    public static final String COLUMN_ASSIGNMENT_TRACKER_TITLE = "ASSIGNMENT_TRACKER_TITLE";
    public static final String COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION = "ASSIGNMENT_TRACKER_DESCRIPTION";
    public static final String COLUMN_ASSIGNMENT_TRACKER_DUEDATE = "ASSIGNMENT_TRACKER_DUEDATE";
    public static final String COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE = "ASSIGNMENT_TRACKER_IMPORTANCE";
    public static final String COLUMN_ASSIGNMENT_TRACKER_COMPLETED = "ASSIGNMENT_TRACKER_COMPLETED";

    //https://www.youtube.com/watch?v=hDSVInZ2JCs&ab_channel=shadsluiter this video helped me make this class
    Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, "assignment_tracker_db", null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createTableStatement = "CREATE TABLE " + ASSIGNMENT_TABLE + " ( " + COLUMN_ASSIGNMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ASSIGNMENT_TRACKER_TITLE + " TEXT," +
                    COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION + " TEXT, " + COLUMN_ASSIGNMENT_TRACKER_DUEDATE + " TEXT, " +
                    COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE + " TEXT, " + COLUMN_ASSIGNMENT_TRACKER_COMPLETED + " BOOL)";
            db.execSQL(createTableStatement);

        } catch (Exception e) {
            Log.e("Database Error", "Failed to create database" + e.getMessage());
            throw e;
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addAssignment(AssignmentModel assignment) {
        SQLiteDatabase dbCheck  = this.getReadableDatabase();
        try{
            String q = "SELECT * FROM " + ASSIGNMENT_TABLE + "WHERE " + COLUMN_ASSIGNMENT_TRACKER_TITLE + "=" + assignment.getTitle();
            Cursor cursor =  dbCheck.rawQuery(q,null);
            if(cursor.getCount() > 0){
                Toast.makeText(context, "This Assignment already exists", Toast.LENGTH_SHORT).show();
                return false;
            }

        }catch (SQLException e){
            Log.e("Database Error", "Failed to check if title exists" + e.getMessage());
        }
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ASSIGNMENT_TRACKER_TITLE, assignment.getTitle());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION, assignment.getDescription());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DUEDATE, assignment.getDueDate());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE, assignment.getImportance());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_COMPLETED, assignment.isCompleted());

            long insert = db.insert(ASSIGNMENT_TABLE, null, cv);
            if (insert == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e("Database Error", "Failed to add to the database" + e.getMessage());
            throw e;
        }

    }

    public List<AssignmentModel> getAssignments() {
        try {
            String q = "SELECT * FROM " + ASSIGNMENT_TABLE;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(q, null);
            List<AssignmentModel> assignmentModelList = assignmentHelper(cursor);

            return assignmentModelList;
        } catch (Exception e) {
            Log.e("Database Error:", "Failed to get assignments from the database " + e.getMessage());
            throw e;
        }
    }

    public List<AssignmentModel> getAssignments(String filter) {
        String filterSQL;
        switch (filter) {
            case "To Do":
                filterSQL = " WHERE NOT(" + COLUMN_ASSIGNMENT_TRACKER_COMPLETED + ")";
                break;
            case "Completed":
                filterSQL = " WHERE " + COLUMN_ASSIGNMENT_TRACKER_COMPLETED;
                break;
            default:
                filterSQL = "";
                break;
        }

        try {
            String q = "SELECT * FROM " + ASSIGNMENT_TABLE + filterSQL;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(q, null);
            List<AssignmentModel> assignmentModelList = assignmentHelper(cursor);

            return assignmentModelList;
        } catch (Exception e) {
            Log.e("Database Error:", "Failed to get assignments from the database" + e.getMessage());
            throw e;
        }
    }

    private List<AssignmentModel>  assignmentHelper(Cursor cursor){
        List<AssignmentModel> assignmentModelList = new ArrayList<AssignmentModel>();

        while (cursor.moveToNext()) {
            int assingmentId = cursor.getInt(0);
            String assingmentTitle = cursor.getString(1);
            String assingmentDescription = cursor.getString(2);
            String assingmentDueDate = cursor.getString(3);
            String assingmentImportance = cursor.getString(4);
            Boolean assingmentCompleted = cursor.getInt(5) == (1) ? true : false;
            assignmentModelList.add(new AssignmentModel(assingmentId, assingmentTitle, assingmentImportance, assingmentDueDate, assingmentDescription, assingmentCompleted));
        }

        return assignmentModelList;
    }

    public boolean deleteAssignments(int id) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            int completed = db.delete(ASSIGNMENT_TABLE, COLUMN_ASSIGNMENT_ID + "=?", new String[]{String.valueOf(id)});
            if (completed != 1) {

                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e("Database error:", "Failed to delete assignment" + e.getMessage());
            throw e;
        }
    }

    public boolean updateAssignment(AssignmentModel assignment) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            String ID = String.valueOf(assignment.getId());

            cv.put(COLUMN_ASSIGNMENT_TRACKER_TITLE, assignment.getTitle());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION, assignment.getDescription());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DUEDATE, assignment.getDueDate());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE, assignment.getImportance());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_COMPLETED, assignment.isCompleted());

            long completed = db.update(ASSIGNMENT_TABLE, cv, "ID = '" + assignment.getId() + "'", null);
            if(completed != 1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            Log.e("Database Error", "Error Updating Assignment" + e.getMessage());
            throw e;
        }
    }



}
