package org.example.dto;

public class TicketDTO {
    private Integer id;
    private GameDTO game;
    private String customerName;
    private String customerAddress;
    private CashierDTO seller;
    private int noOfSeats;

    public TicketDTO() {
    }

    public TicketDTO(Integer id, GameDTO game, String customerName, String customerAddress, CashierDTO seller, int noOfSeats) {
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

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
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

    public CashierDTO getSeller() {
        return seller;
    }

    public void setSeller(CashierDTO seller) {
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
