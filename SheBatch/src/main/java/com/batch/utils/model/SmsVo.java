package com.batch.utils.model;

public class SmsVo {
    private String sndr;

    private String callback;

    private String rcvr;

    private String rcvrnum;

    private String msg;

    public String getSndr() {
        return sndr;
    }

    public void setSndr(String sndr) {
        this.sndr = sndr;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getRcvr() {
        return rcvr;
    }

    public void setRcvr(String rcvr) {
        this.rcvr = rcvr;
    }

    public String getRcvrnum() {
        return rcvrnum;
    }

    public void setRcvrnum(String rcvrnum) {
        this.rcvrnum = rcvrnum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
