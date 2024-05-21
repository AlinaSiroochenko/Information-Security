package org.example;

public class PasswordEntry {
    private String title;
    private String password;
    private String url;
    private String info;

    public PasswordEntry(String title, String password, String url, String info) {
        this.title = title;
        this.password = password;
        this.url = url;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
