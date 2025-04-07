package org.example.dto;

public class ClientFilterDTO {
    private String name;
    private String address;

    public ClientFilterDTO(String name, String address) {
        String na = null;
        String ad = null;
        if (!name.isEmpty()) {
            na = name;
        }
        if (!address.isEmpty()) {
            ad = address;
        }
        this.name = na;
        this.address = ad;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
