package com.example.tem.iot.command;
/*
设备执行的命令，Json格式，里面是一个个健值对，如果service_id不为空，
每个健都是profile中命令的参数名（paraName）;如果service_id为空则由用户自定义命令格式。
设备命令示例：{"value":"1"}，具体格式需要应用和设备约定， 最大32K。
 */

public class Paras {
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    //private String cofig;//配置命令，经过json化后就会变为"cofig" : "cofig赋的值"
    private String state;

    //private byte[] order;


}
