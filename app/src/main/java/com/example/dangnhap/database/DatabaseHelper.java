package com.example.dangnhap.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2; // Tăng version lên 2

    // Tên bảng và các cột
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id"; // Sửa thành _id theo convention
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password_hash"; // Đổi tên để rõ ràng
    public static final String COLUMN_CREATED_AT = "created_at"; // Thêm trường thời gian
    public static final String COLUMN_LAST_LOGIN = "last_login"; // Thêm trường mới

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD + " TEXT NOT NULL," + // Sẽ lưu password đã hash
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_LAST_LOGIN + " DATETIME" +
                    ")";

    // Câu lệnh tạo index cho email
    private static final String CREATE_EMAIL_INDEX =
            "CREATE INDEX idx_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_EMAIL_INDEX);
            db.setTransactionSuccessful();
            Log.d(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();

            if (oldVersion < 2) {
                // Migration từ version 1 lên 2
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " +
                        COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " +
                        COLUMN_LAST_LOGIN + " DATETIME");
                db.execSQL(CREATE_EMAIL_INDEX);
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Database upgraded from " + oldVersion + " to " + newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
            // Fallback - xóa và tạo lại nếu có lỗi
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Bật foreign key constraints và WAL mode
        db.setForeignKeyConstraintsEnabled(true);
        db.enableWriteAheadLogging();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Bật foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}