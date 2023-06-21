package com.example.tem.iot.responseObject;

import java.util.List;

public class IotMessage {
    private String resource;
    private String event;
    private String event_time;
    private NotifyDataBean notify_data;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public NotifyDataBean getNotify_data() {
        return notify_data;
    }

    public void setNotify_data(NotifyDataBean notify_data) {
        this.notify_data = notify_data;
    }

    public static class NotifyDataBean {
        /**
         * header : {"app_id":"HtnWEDUfOsN331egyAY6DZwc024a","device_id":"5eed76e4da222a02eac70a5f_865057040824694","node_id":"865057040824694","product_id":"5eed76e4da222a02eac70a5f","gateway_id":"5eed76e4da222a02eac70a5f_865057040824694"}
         * body : {"services":[{"service_id":"ControlSIP","properties":{"ObjectName":"ABCDE","FrequencyNum":"10","Resistivity":"       4.000000","Phase":"       0.040000"},"event_time":"20210311T133057Z"}]}
         */

        private HeaderBean header;
        private BodyBean body;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public static class HeaderBean {
            /**
             * app_id : HtnWEDUfOsN331egyAY6DZwc024a
             * device_id : 5eed76e4da222a02eac70a5f_865057040824694
             * node_id : 865057040824694
             * product_id : 5eed76e4da222a02eac70a5f
             * gateway_id : 5eed76e4da222a02eac70a5f_865057040824694
             */

            private String app_id;
            private String device_id;
            private String node_id;
            private String product_id;
            private String gateway_id;

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
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

            public String getProduct_id() {
                return product_id;
            }

            public void setProduct_id(String product_id) {
                this.product_id = product_id;
            }

            public String getGateway_id() {
                return gateway_id;
            }

            public void setGateway_id(String gateway_id) {
                this.gateway_id = gateway_id;
            }
        }

        public static class BodyBean {
            private List<ServicesBean> services;

            public List<ServicesBean> getServices() {
                return services;
            }

            public void setServices(List<ServicesBean> services) {
                this.services = services;
            }


            public static class ServicesBean {
                /**
                 * service_id : 远程采控
                 * properties : {"Voltage":null,"CheckBit":null,"Current":null}
                 * event_time : 20210311T133057Z
                 */
                private String service_id;
                private PropertiesBean properties;
                private String event_time;
                public String getService_id() {
                    return service_id;
                }

                public void setService_id(String service_id) {
                    this.service_id = service_id;
                }

                public PropertiesBean getProperties() {
                    return properties;
                }

                public void setProperties(PropertiesBean properties) {
                    this.properties = properties;
                }

                public String getEvent_time() {
                    return event_time;
                }

                public void setEvent_time(String event_time) {
                    this.event_time = event_time;
                }

                public static class PropertiesBean {
                    /**
                     * Voltage:null,
                     * CheckBit:null
                     * Current:null
                     */
                    private String Voltage;
                    private String CheckBit;
                    private String Current;

                    public String getCheckBit() {
                        return CheckBit;
                    }

                    public void setCheckBit(String CheckBit) {
                        this.CheckBit = CheckBit;
                    }

                    public String getCurrent() {
                        return Current;
                    }
                    public void setCurrent(String Current) {
                        this.Current = Current;
                    }


                    public String getVoltage() {
                        return Voltage;
                    }
                    public void setVoltage(String voltage) {
                        this.Voltage = voltage;
                    }

                }
            }
            /*public static class ServicesBean {
                *//**
                 * service_id : ControlSIP
                 * properties : {"ObjectName":"ABCDE","FrequencyNum":"10","Resistivity":"       4.000000","Phase":"       0.040000"}
                 * event_time : 20210311T133057Z
                 *//*

                private String service_id;
                private PropertiesBean properties;
                private String event_time;

                public String getService_id() {
                    return service_id;
                }

                public void setService_id(String service_id) {
                    this.service_id = service_id;
                }

                public PropertiesBean getProperties() {
                    return properties;
                }

                public void setProperties(PropertiesBean properties) {
                    this.properties = properties;
                }

                public String getEvent_time() {
                    return event_time;
                }

                public void setEvent_time(String event_time) {
                    this.event_time = event_time;
                }

                public static class PropertiesBean {
                    public String getGroupID() {
                        return GroupID;
                    }

                    public String getVoltage() {
                        return Voltage;
                    }

                    public void setGroupID(String groupID) {
                        GroupID = groupID;
                    }

                    public void setVoltage(String voltage) {
                        Voltage = voltage;
                    }

                    *//**
                     * ObjectName : ABCDE
                     * FrequencyNum : 10
                     * Resistivity :        4.000000
                     * Phase :        0.040000
                     *//*

                    private String GroupID;
                    private String Voltage;


                }
            }*/
        }
    }

}
