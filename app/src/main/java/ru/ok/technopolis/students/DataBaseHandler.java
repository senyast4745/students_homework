package ru.ok.technopolis.students;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ru.ok.technopolis.students.BuildConfig.LOG;
import static ru.ok.technopolis.students.BuildConfig.LOG_TAG;

public class DataBaseHandler extends SQLiteOpenHelper implements IDataBaseHendler {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "studentsManager";
    private static final String TABLE_STUDENTS = "students";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_L_NAME = "lastName";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PH_ID = "photoId";

    DataBaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_STUDENTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_L_NAME + " TEXT," + KEY_PH_ID + " TEXT," +
                KEY_GENDER + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);

        onCreate(db);
    }

    @Override
    public void addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (LOG) {
            Log.d(LOG_TAG, "Student information " + student.getFirstName() + " " + student.getSecondName() + " " + student.isMaleGender() + " " + student.getPhoto());
        }
        values.put(KEY_NAME, student.getFirstName());
        values.put(KEY_L_NAME, student.getSecondName());
        int studentIntGender = 0;
        if (student.isMaleGender()) {
            studentIntGender = 1;
        }
        values.put(KEY_PH_ID, String.valueOf(student.getPhoto()));
        values.put(KEY_GENDER, studentIntGender);

        db.insert(TABLE_STUDENTS, null, values);
        db.close();
    }

    @Override
    public Student getStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_NAME, KEY_L_NAME, KEY_PH_ID, KEY_GENDER}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Student student;

        if (cursor != null) {
            cursor.moveToFirst();
            int _id = Integer.parseInt(cursor.getString(0));
            String firstName = cursor.getString(1);
            String secondName = cursor.getString(2);
            int photoID = Integer.parseInt(cursor.getString(3));
            boolean gender = false;
            if (Integer.parseInt(cursor.getString(4)) == 1) {
                gender = true;
            }

            student = new Student(_id, firstName,
                    secondName, gender, photoID);
            cursor.close();
        } else {
            student = null;
        }


        return student;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(0));
                student.setFirstName(cursor.getString(1));
                student.setSecondName(cursor.getString(2));
                boolean gender = false;
                if (cursor.getInt(4) == 1) {
                    gender = true;
                }
                student.setMaleGender(gender);

                student.setPhoto(Integer.parseInt(cursor.getString(3)));
                if (LOG)
                    Log.d(LOG_TAG, "Data Base student " + student.getFirstName() + " " + student.getSecondName() + " " + student.getPhoto());

                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentList;
    }

    @Override
    public int getStudentCount() {
        String countQuery = "SELECT * FROM " + TABLE_STUDENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();

    }

    @Override
    public int updateStudentList(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, student.getFirstName());
        values.put(KEY_L_NAME, student.getSecondName());
        int studentIntGender = 0;
        if (student.isMaleGender()) {
            studentIntGender = 1;
        }
        values.put(KEY_GENDER, studentIntGender);
        values.put(KEY_PH_ID, student.getPhoto());


        return db.update(TABLE_STUDENTS, values, KEY_ID + "=?", new String[]{String.valueOf(student.getID())});
    }

    @Override
    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, KEY_ID + "=?", new String[]{String.valueOf(student.getID())});

        db.close();

    }

    @Override
    public void clearStudentList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, null, null);
        db.close();
    }
}
