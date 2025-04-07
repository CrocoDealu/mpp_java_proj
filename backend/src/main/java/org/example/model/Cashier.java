package org.example.model;

import java.util.Objects;

public class Cashier extends Entity<Integer> {
    private String name;
    private String username;
    private String password;
    public Cashier(int id, String name, String username, String password) {
        super(id);
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Cashier{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cashier cashier = (Cashier) o;
        return Objects.equals(name, cashier.name) && Objects.equals(username, cashier.username) && Objects.equals(password, cashier.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, username, password);
    }
}
