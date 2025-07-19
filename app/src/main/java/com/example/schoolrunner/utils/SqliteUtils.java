package com.example.schoolrunner.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gzone.university.utils.AppUtils;

public class SqliteUtils extends SQLiteOpenHelper {

    /**
     * SQL for creating student table
     */
    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE IF NOT EXISTS tb_student (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_no TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "name TEXT" +
                    ")";

    /**
     * SQL for creating order table
     */
    private static final String CREATE_ORDER_TABLE =
            "CREATE TABLE IF NOT EXISTS tb_order (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id INTEGER," +
                    "runner_id INTEGER," +
                    "order_no TEXT UNIQUE NOT NULL," +
                    "order_type TEXT NOT NULL," +
                    "item_description TEXT NOT NULL," +
                    "pickup_address TEXT NOT NULL," +
                    "delivery_address TEXT NOT NULL," +
                    "route_info TEXT," +
                    "contact TEXT NOT NULL," +
                    "delivery_time TIMESTAMP," +
                    "price REAL NOT NULL," +
                    "status INTEGER NOT NULL," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "update_time TIMESTAMP" +
                    ")";


    private static volatile SqliteUtils sInstance;
    private SQLiteDatabase db;

    public SqliteUtils() {
        super(AppUtils.getApplication(), "school_runner.db", null, 4);
    }

    /**
     * Create and get singleton instance
     */
    public static SqliteUtils getInstance() {
        if (sInstance == null) {
            synchronized (SqliteUtils.class) {
                if (sInstance == null) {
                    sInstance = new SqliteUtils();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        // Create student table
        db.execSQL(CREATE_STUDENT_TABLE);
        // Create order table
        db.execSQL(CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        // Delete table
        db.execSQL("DROP TABLE IF EXISTS tb_student");
        db.execSQL("DROP TABLE IF EXISTS tb_order");
        // Recreate table
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        this.db = db;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        this.db = db;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (db == null || !db.isOpen()) {
            db = super.getWritableDatabase();
        }
        return db;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (db == null || !db.isOpen()) {
            db = super.getReadableDatabase();
        }
        return db;
    }
}
