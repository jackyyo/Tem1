package com.example.tem.Measurement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.util.HashMap;

/*
 *a
 */public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String TABLE_NAME = "MyTestData";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DBHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }
    //一个工区一个数据库文件
    public DBHelper(Context context, String work_space_name) {
        this(context, Environment.getExternalStorageDirectory() +"/TEM"+ "/"+work_space_name+ "/"+work_space_name+".db", VERSION);
    }

    //新建一个TemData类用于存储测区的信息

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String TemData_sql = "create table " + TABLE_NAME + " (_id integer primary key autoincrement,"+
                "file_name varchar(60),"+
                "point varchar(60)," +
                "line varchar(60)," +
                "point_distence varchar(60)," +
                "line_distence varchar(60)," +
                "current varchar(60),"  +
                "current1 varchar(60),"+
                "time0 varchar(60),"+
                "time1 varchar(60)," +
                "data varchar(180))";
        sqLiteDatabase.execSQL(TemData_sql);
    }

    //插入输入的参数
    public void InsertData(TemData t){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_name",t.getFile_name()+"");
        contentValues.put("point",t.getPoint()+"");
        contentValues.put("line",t.getLine()+"");
        contentValues.put("point_distence",t.getPoint_distence()+"");
        contentValues.put("line_distence",t.getLine_distence()+"");
        contentValues.put("current0",t.getCurrent_show()+"");
        contentValues.put("current1",t.getVoltage_show()+"");
        contentValues.put("time0",t.getTime_show()+"");
        contentValues.put("time1",t.getFre_show()+"");
        contentValues.put("data",t.getData()+"");
        DB.insert(TABLE_NAME,null,contentValues);
        DB.close();
    }


    //查询出该条记录的可能需要的所有信息
    public HashMap<String,String> getAllInformation(String Point,String Line){
        SQLiteDatabase DB = this.getReadableDatabase();
        HashMap<String,String> allInformationMap = new HashMap<>();
        String querySql = "select * from "+TABLE_NAME + " where point = "+Point+" and line = "+ Line;
        Cursor cursor = DB.rawQuery(querySql,null);
        Log.e("sqlsql", "3333333333333333333");
        Log.e("sqlsql", (cursor != null) +"  "+ (cursor.getCount()));
        if (cursor != null && cursor.getCount() > 0){
            Log.e("sqlsql", "22222222222222222");
            while (cursor.moveToNext()) {
                Log.e("sqlsql", "llllllllllllllllllll");
                allInformationMap.put("file_name", cursor.getString(cursor.getColumnIndex("file_name")));
                allInformationMap.put("point", cursor.getString(cursor.getColumnIndex("point")));
                allInformationMap.put("line", cursor.getString(cursor.getColumnIndex("line")));
                allInformationMap.put("point_distence", cursor.getString(cursor.getColumnIndex("point_distence")));
                allInformationMap.put("line_distence", cursor.getString(cursor.getColumnIndex("line_distence")));
                allInformationMap.put("current0", cursor.getString(cursor.getColumnIndex("current0")));
                allInformationMap.put("current1", cursor.getString(cursor.getColumnIndex("current1")));
                allInformationMap.put("time0", cursor.getString(cursor.getColumnIndex("time0")));
                allInformationMap.put("time1", cursor.getString(cursor.getColumnIndex("time1")));
                allInformationMap.put("data", cursor.getString(cursor.getColumnIndex("data")));
                Log.e("sqlsql", (String)cursor.getString(cursor.getColumnIndex("current1")));
            }
        }
        cursor.close();
        DB.close();
        return allInformationMap;
    }

    public void updateData(String file_name,String data,String point,String line){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_name",file_name);
        contentValues.put("data",data);
        DB.update(TABLE_NAME,contentValues,"point = ? and line = ?",
                new String[]{point,line});
        Log.d("updateData:i ", "updateData: ");
    }


    //删除对应的记录
    public void delete(String fileName){
        SQLiteDatabase DB = this.getReadableDatabase();
        DB.delete(TABLE_NAME,"file_Name = ?",new String[]{fileName});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
