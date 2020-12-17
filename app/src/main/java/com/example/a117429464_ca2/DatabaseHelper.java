package com.example.a117429464_ca2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    private SQLiteDatabase db;
    private static DatabaseHelper instance = null;

    private DatabaseHelper(@Nullable Context context) {
        super(context, "assignment_tracker_db", null, VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
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
        db = getWritableDatabase();
        try{
            String q = "SELECT * FROM " + ASSIGNMENT_TABLE + " WHERE " + COLUMN_ASSIGNMENT_TRACKER_TITLE + "= ?" ;
            Cursor cursor =  db.rawQuery(q,new String[]{assignment.getTitle()});
            if(cursor.getCount() > 0){
                return false;
            }
        }catch (SQLException e){
            Log.e("Database Error", "Failed to check if title exists" + e.getMessage());
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ASSIGNMENT_TRACKER_TITLE, assignment.getTitle());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION, assignment.getDescription());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DUEDATE, assignment.getDueDate());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE, assignment.getImportance());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_COMPLETED, assignment.isCompleted());

            long insert = db.insert(ASSIGNMENT_TABLE, null, cv);

            return insert != -1;
        } catch (Exception e) {
            Log.e("Database Error", "Failed to add to the database" + e.getMessage());
            throw e;
        }

    }

    public List<AssignmentModel> getAssignments() {
        try {
            String q = "SELECT * FROM " + ASSIGNMENT_TABLE;
            db = getReadableDatabase();

            Cursor cursor = db.rawQuery(q, null);
            return assignmentHelper(cursor);
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
            db = getReadableDatabase();

            Cursor cursor = db.rawQuery(q, null);
            return assignmentHelper(cursor);
        } catch (Exception e) {
            Log.e("Database Error:", "Failed to get assignments from the database" + e.getMessage());
            throw e;
        }
    }

    private List<AssignmentModel>  assignmentHelper(Cursor cursor){
        List<AssignmentModel> assignmentModelList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int assingmentId = cursor.getInt(0);
            String assingmentTitle = cursor.getString(1);
            String assingmentDescription = cursor.getString(2);
            String assingmentDueDate = cursor.getString(3);
            String assingmentImportance = cursor.getString(4);
            boolean assingmentCompleted = cursor.getInt(5) == (1);
            assignmentModelList.add(new AssignmentModel(assingmentId, assingmentTitle, assingmentImportance, assingmentDueDate, assingmentDescription, assingmentCompleted));
        }

        return assignmentModelList;
    }

    public boolean deleteAssignments(int id) {
        try {

             db = getWritableDatabase();
            int completed = db.delete(ASSIGNMENT_TABLE, COLUMN_ASSIGNMENT_ID + "=?", new String[]{String.valueOf(id)});
            return completed == 1;
        } catch (Exception e) {
            Log.e("Database error:", "Failed to delete assignment" + e.getMessage());
            throw e;
        }
    }

    public boolean updateAssignment(AssignmentModel assignment) {
        try{
             db = getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ASSIGNMENT_TRACKER_TITLE, assignment.getTitle());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DESCRIPTION, assignment.getDescription());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_DUEDATE, assignment.getDueDate());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_IMPORTANCE, assignment.getImportance());
            cv.put(COLUMN_ASSIGNMENT_TRACKER_COMPLETED, assignment.isCompleted());

            long completed = db.update(ASSIGNMENT_TABLE, cv, "ID = '" + assignment.getId() + "'", null);
            return completed == 1;
        }catch (Exception e){
            Log.e("Database Error", "Error Updating Assignment" + e.getMessage());
            throw e;
        }
    }

    public void closeInstances(){
        instance = null;
        getReadableDatabase().close();
        getWritableDatabase().close();
    }
}
