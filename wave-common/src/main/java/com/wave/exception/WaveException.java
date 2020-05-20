package com.wave.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lijinyang
 */
public class WaveException extends Exception{
    private static final long serialVersionUID = -3913902031489277776L;
    private int errCode;
    private String errMsg;
    private Throwable causeThrowable;
    public static final int CLIENT_INVALID_PARAM = -400;
    public static final int CLIENT_OVER_THRESHOLD = -503;
    public static final int INVALID_PARAM = 400;
    public static final int NO_RIGHT = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int OVER_THRESHOLD = 503;

    public WaveException() {
    }

    public WaveException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public WaveException(int errCode, Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
        this.setCauseThrowable(throwable);
    }

    public WaveException(int errCode, String errMsg, Throwable throwable) {
        super(errMsg, throwable);
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.setCauseThrowable(throwable);
    }

    public int getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        if (!StringUtils.isBlank(this.errMsg)) {
            return this.errMsg;
        } else {
            return this.causeThrowable != null ? this.causeThrowable.getMessage() : "";
        }
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setCauseThrowable(Throwable throwable) {
        this.causeThrowable = this.getCauseThrowable(throwable);
    }

    private Throwable getCauseThrowable(Throwable t) {
        return t.getCause() == null ? t : this.getCauseThrowable(t.getCause());
    }

    @Override
    public String toString() {
        return "ErrCode:" + this.getErrCode() + ", ErrMsg:" + this.getErrMsg();
    }
}
