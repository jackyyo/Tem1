package com.example.tem.iot.command;

public class AsynchronousCommand {
    private String service_id;
    private String command_name;
    private Paras paras;
    private int expire_time;
    private String send_strategy;



    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getCommand_name() {
        return command_name;
    }

    public void setCommand_name(String command_name) {
        this.command_name = command_name;
    }

    public Paras getParas() {
        return paras;
    }

    public void setParas(Paras paras) {
        this.paras = paras;
    }


    public String getSend_strategy() {
        return send_strategy;
    }

    public void setSend_strategy(String send_strategy) {
        this.send_strategy = send_strategy;
    }

    public int getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(int expire_time) {
        this.expire_time = expire_time;
    }
}
