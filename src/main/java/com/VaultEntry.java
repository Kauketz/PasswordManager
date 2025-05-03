package com;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class VaultEntry {

    private final StringProperty domain;
    private final StringProperty username;
    private final StringProperty password;

    public VaultEntry(String domain, String username, String password) {
        this.domain = new SimpleStringProperty(domain);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    public String getDomain() {
        return domain.get();
    }

    public void setDomain(String domain) {
        this.domain.set(domain);
    }

    public StringProperty domainProperty() {
        return domain;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }
}
