package com.example.tem.iot.responseObject;

import java.util.List;

public class Devices {
    /**
     * devices : [{"app_id":"1c6fbf26239d460196e64b21986b50bd","app_name":"DefaultApp_hw80146891_iot","device_id":"61aec88fd28ce302885b550f_1638928791864","node_id":"1638928791864","gateway_id":"61aec88fd28ce302885b550f_1638928791864","device_name":"20211207T023559ZNBSimulator","node_type":"GATEWAY","description":null,"fw_version":null,"sw_version":null,"device_sdk_version":null,"product_id":"61aec88fd28ce302885b550f","product_name":"FDM-ERT","status":"ONLINE","tags":[]},{"app_id":"324e001aef0d4533b40c12cbe08d1015","app_name":"booster_c4e793e51c2e47108d6f2538f256dd2f","device_id":"d3eef8e4-db5f-41b1-9179-d340152dec5b","node_id":"324e001aef0d4533b40c12cbe08d1015-0mnpx","gateway_id":"d3eef8e4-db5f-41b1-9179-d340152dec5b","device_name":"BearPiKitSimulator","node_type":"GATEWAY","description":null,"fw_version":null,"sw_version":null,"device_sdk_version":null,"product_id":"5f812ae524e3a102c344fd31","product_name":"BearPiKit_hauwei_model","status":"INACTIVE","tags":[]}]
     * page : {"count":2,"marker":"5f812ae6bbeed40238a5462d"}
     */



    private PageBean page;
    private List<DevicesBean> devices;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<DevicesBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DevicesBean> devices) {
        this.devices = devices;
    }

    public static class PageBean {
        /**
         * count : 2
         * marker : 5f812ae6bbeed40238a5462d
         */

        private int count;
        private String marker;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getMarker() {
            return marker;
        }

        public void setMarker(String marker) {
            this.marker = marker;
        }
    }

    public static class DevicesBean {
        /**
         * app_id : 1c6fbf26239d460196e64b21986b50bd
         * app_name : DefaultApp_hw80146891_iot
         * device_id : 61aec88fd28ce302885b550f_1638928791864
         * node_id : 1638928791864
         * gateway_id : 61aec88fd28ce302885b550f_1638928791864
         * device_name : 20211207T023559ZNBSimulator
         * node_type : GATEWAY
         * description : null
         * fw_version : null
         * sw_version : null
         * device_sdk_version : null
         * product_id : 61aec88fd28ce302885b550f
         * product_name : FDM-ERT
         * status : ONLINE
         * tags : []
         */

        private String app_id;
        private String app_name;
        private String device_id;
        private String node_id;
        private String gateway_id;
        private String device_name;
        private String node_type;
        private Object description;
        private Object fw_version;
        private Object sw_version;
        private Object device_sdk_version;
        private String product_id;
        private String product_name;
        private String status;
        private List<?> tags;

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getNode_id() {
            return node_id;
        }

        public void setNode_id(String node_id) {
            this.node_id = node_id;
        }

        public String getGateway_id() {
            return gateway_id;
        }

        public void setGateway_id(String gateway_id) {
            this.gateway_id = gateway_id;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public String getNode_type() {
            return node_type;
        }

        public void setNode_type(String node_type) {
            this.node_type = node_type;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public Object getFw_version() {
            return fw_version;
        }

        public void setFw_version(Object fw_version) {
            this.fw_version = fw_version;
        }

        public Object getSw_version() {
            return sw_version;
        }

        public void setSw_version(Object sw_version) {
            this.sw_version = sw_version;
        }

        public Object getDevice_sdk_version() {
            return device_sdk_version;
        }

        public void setDevice_sdk_version(Object device_sdk_version) {
            this.device_sdk_version = device_sdk_version;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<?> getTags() {
            return tags;
        }

        public void setTags(List<?> tags) {
            this.tags = tags;
        }
    }
}
