package org.example.model;

import java.util.Objects;

public class Ticket extends Entity<Integer> {
    private Game game;
    private String customerName;
    private String customerAddress;
    private Cashier seller;
    private int noOfSeats;

    public Ticket() {
        super(-1);
    }

    public Ticket(Integer integer, Game game, String customerName, String customerAddress, Cashier seller, int noOfSeats) {
        super(integer);
        this.game = game;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.seller = seller;
        this.noOfSeats = noOfSeats;
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
        return "Ticket{" +
                "id=" + id +
                ", noOfSeats=" + noOfSeats +
                ", seller=" + seller +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerName='" + customerName + '\'' +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return noOfSeats == ticket.noOfSeats && Objects.equals(game, ticket.game) && Objects.equals(customerName, ticket.customerName) && Objects.equals(customerAddress, ticket.customerAddress) && Objects.equals(seller, ticket.seller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, customerName, customerAddress, seller, noOfSeats);
    }
}
