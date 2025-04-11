package org.example.dto;

public class Ticket {
    private Integer id;
    private Game game;
    private String customerName;
    private String customerAddress;
    private Cashier seller;
    private int noOfSeats;

    public Ticket() {
    }

    public Ticket(Integer id, Game game, String customerName, String customerAddress, Cashier seller, int noOfSeats) {
        this.id = id;
        this.game = game;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.seller = seller;
        this.noOfSeats = noOfSeats;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Cashier getSeller() {
        return seller;
    }

    public void setSeller(Cashier seller) {
        this.seller = seller;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    @Override
    public String toString() {
        return "TicketDTO{" +
                "id=" + id +
                ", game='" + game + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", seller='" + seller + '\'' +
                ", noOfSeats=" + noOfSeats +
                '}';
    }
}
