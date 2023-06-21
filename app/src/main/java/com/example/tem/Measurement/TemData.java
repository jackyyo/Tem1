package com.example.tem.Measurement;

/*
 *a
 */public class TemData {

    private int point;//保证唯一性
    private int line;
    private int point_distence;
    private int line_distence;
    private int current_show;
    private int voltage_show;
    private int time_show;
    private int fre_show;



    private String file_name;//各点存储文件名
    private String data;


    public TemData(){}

    public TemData(String file_name,int point,int line,int point_distence,int line_distence,int current_show,int voltage_show,int time_show,int fre_show,String data ){
        this.point=point;
        this.line=line;
        this.point_distence=point_distence;
        this.line_distence=line_distence;
        this.current_show=current_show;
        this.voltage_show=voltage_show;
        this.time_show=time_show;
        this.fre_show=fre_show;
        this.file_name=file_name;
        this.data=data;
    }

    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }

    public void setLine(int line) {
        this.line = line;
    }
    public int getLine() {
        return line;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_name() {
        return file_name;
    }


    public int getPoint_distence() {
        return point_distence;
    }

    public int getLine_distence() {
        return line_distence;
    }

    public int getCurrent_show() {
        return current_show;
    }

    public int getVoltage_show() {
        return voltage_show;
    }

    public int getTime_show() {
        return time_show;
    }

    public int getFre_show() {
        return fre_show;
    }
    public String getData() {
        return data;
    }

    public void setPoint_distence(int point_distence) {
        this.point_distence = point_distence;
    }

    public void setLine_distence(int line_distence) {
        this.line_distence = line_distence;
    }

    public void setCurrent_show(int current_show) {
        this.current_show = current_show;
    }

    public void setVoltage_show(int voltage_show) {
        this.voltage_show = voltage_show;
    }

    public void setTime_show(int time_show) {
        this.time_show = time_show;
    }

    public void setFre_show(int fre_show) {
        this.fre_show = fre_show;
    }
    public void setData(String data) {
        this.data = data;
    }
}
