package com.sat.web;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorInfo {

    //BAD REQUEST EXCEPTION

    //UserController
    MAIL_ALREADY_IN_USE(4000, "Mail already in use"),
    MAIL_INVALID(4001, "Invalid mail"),
    LOGIN_ALREADY_IN_USE(4002, "Login already in use"),
    USER_NOT_FOUND(4003, "User not found"),
    //EventController
    EVENT_OWNER_NOT_FOUND(4101, "There is no such owner"),
    EVENT_NOT_FOUND(4102, "Event not found"),
    ALREADY_JOINED(4103, "User already joined event"),

    //Other exceptions
    CONFLICT(4999, "Conflict");

    private final int errCode;
    private final String errDescr;

    ErrorInfo(int errCode, String errDescr) {
        this.errCode = errCode;
        this.errDescr = errDescr;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrDescr() {
        return errDescr;
    }
}
