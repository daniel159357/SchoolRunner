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
                    "received_time TIMESTAMP," +
                    "completed_time TIMESTAMP," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "update_time TIMESTAMP," +
                    "publisher_score REAL," +
                    "publisher_comment TEXT," +
                    "runner_score REAL," +
                    "runner_comment TEXT," +
                    "runner_limit_type INTEGER DEFAULT 0," +
                    "min_runner_score REAL" +
                    ")";


    private static volatile SqliteUtils sInstance;
    private SQLiteDatabase db;

    public SqliteUtils() {
        super(AppUtils.getApplication(), "school_runner.db", null, 8); // 升级版本号
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
        if (oldVersion < 4) {
            // 只添加不存在的字段
            db.execSQL("ALTER TABLE tb_order ADD COLUMN received_time TIMESTAMP");
            db.execSQL("ALTER TABLE tb_order ADD COLUMN completed_time TIMESTAMP");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE tb_order ADD COLUMN publisher_score REAL");
            db.execSQL("ALTER TABLE tb_order ADD COLUMN publisher_comment TEXT");
            db.execSQL("ALTER TABLE tb_order ADD COLUMN runner_score REAL");
            db.execSQL("ALTER TABLE tb_order ADD COLUMN runner_comment TEXT");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE tb_order ADD COLUMN only_new_runner INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE tb_order ADD COLUMN min_runner_score REAL");
        }
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE tb_order ADD COLUMN runner_limit_type INTEGER DEFAULT 0");
        }
        // 其他升级逻辑...
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
