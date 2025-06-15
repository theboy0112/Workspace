package com.example.dangerous;

public class CallLog {
    public int id;
    public String companyName;
    public String number;
    public String callTime;

    public CallLog(int id, String companyName, String number, String callTime) {
        this.id = id;
        this.companyName = companyName;
        this.number = number;
        this.callTime = callTime;
    }
}
