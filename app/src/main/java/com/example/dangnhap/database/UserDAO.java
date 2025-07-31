package com.example.dangnhap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserDAO {
    private static final String TAG = "UserDAO";
    private final DatabaseHelper dbHelper;
    private static final int BCRYPT_COST = 12; // Độ phức tạp mã hóa

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    /**
     * Đăng ký người dùng mới
     * @param email Email người dùng
     * @param password Mật khẩu (sẽ được mã hóa)
     * @param fullName Tên đầy đủ
     * @return ID của user mới hoặc -1 nếu thất bại
     */
    public long registerUser(String email, String password, String fullName) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // Mã hóa mật khẩu bằng BCrypt
            String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_PASSWORD_HASH, hashedPassword);
            values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);

            long userId = db.insertOrThrow(DatabaseHelper.TABLE_USERS, null, values);
            db.setTransactionSuccessful();
            return userId;
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
            return -1;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                    db.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing database", e);
                }
            }
        }
    }

    /**
     * Xác thực đăng nhập
     * @param email Email người dùng
     * @param password Mật khẩu chưa mã hóa
     * @return true nếu đăng nhập thành công
     */
    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String[] columns = {DatabaseHelper.COLUMN_PASSWORD_HASH};
            String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
            String[] selectionArgs = {email};

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                String hashedPassword = cursor.getString(0);
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
                if (result.verified) {
                    updateLastLogin(email); // Cập nhật thời gian đăng nhập cuối
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String[] columns = {DatabaseHelper.COLUMN_ID};
            String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
            String[] selectionArgs = {email};

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking email existence", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * Cập nhật thời gian đăng nhập cuối
     */
    private void updateLastLogin(String email) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_LAST_LOGIN, System.currentTimeMillis());

            db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{email}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating last login", e);
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Lấy thông tin người dùng
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String[] columns = {
                    DatabaseHelper.COLUMN_ID,
                    DatabaseHelper.COLUMN_EMAIL,
                    DatabaseHelper.COLUMN_FULL_NAME,
                    DatabaseHelper.COLUMN_CREATED_AT
            };

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    columns,
                    DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getLong(0));
                user.setEmail(cursor.getString(1));
                user.setFullName(cursor.getString(2));
                user.setCreatedAt(cursor.getString(3));
                return user;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user", e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }
}