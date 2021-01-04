package com.example.yrsappserver.Common;

public class StatisticsHelper {
    private String date;
    private int login;
    private int register;

    public StatisticsHelper() {
    }

    public StatisticsHelper(String date, int login, int register) {
        this.date = date;
        this.login = login;
        this.register = register;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }
}
