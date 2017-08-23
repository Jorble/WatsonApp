package com.giant.watsonapp.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jorble on 2017/8/23.
 */

public class Hotel {

    /**
     * errCode : 200
     * msg : 请求成功
     * hotelList : [{"id":"26754","name":"三亚海韵度假酒店","price":"¥760","star":4.68,"location":"三亚湾路168号（近海虹路）","imgList":["https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg","https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg","https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg"],"roomList":[{"name":"豪华海景套房","price":"¥760","desc":"85-90m² 大/双床 6-19层","impression":"视野开阔","img":"https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg"}]}]
     */

    private int errCode;
    private String msg;
    private List<HotelListBean> hotelList;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<HotelListBean> getHotelList() {
        return hotelList;
    }

    public void setHotelList(List<HotelListBean> hotelList) {
        this.hotelList = hotelList;
    }

    public static class HotelListBean implements Serializable{
        /**
         * id : 26754
         * name : 三亚海韵度假酒店
         * price : ¥760
         * star : 4.68
         * location : 三亚湾路168号（近海虹路）
         * imgList : ["https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg","https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg","https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg"]
         * roomList : [{"name":"豪华海景套房","price":"¥760","desc":"85-90m² 大/双床 6-19层","impression":"视野开阔","img":"https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg"}]
         */

        private String id;
        private String name;
        private String price;
        private float star;
        private String location;
        private List<String> imgList;
        private List<RoomListBean> roomList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public float getStar() {
            return star;
        }

        public void setStar(float star) {
            this.star = star;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public List<String> getImgList() {
            return imgList;
        }

        public void setImgList(List<String> imgList) {
            this.imgList = imgList;
        }

        public List<RoomListBean> getRoomList() {
            return roomList;
        }

        public void setRoomList(List<RoomListBean> roomList) {
            this.roomList = roomList;
        }

        public static class RoomListBean implements Serializable{
            /**
             * name : 豪华海景套房
             * price : ¥760
             * desc : 85-90m² 大/双床 6-19层
             * impression : 视野开阔
             * img : https://gss2.bdstatic.com/9fo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=33cfb34ddb1373f0e13267cdc566209e/a8773912b31bb0511a95981e3c7adab44aede057.jpg
             */

            private String name;
            private String price;
            private String desc;
            private String impression;
            private String img;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getImpression() {
                return impression;
            }

            public void setImpression(String impression) {
                this.impression = impression;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }
        }
    }
}
