package memento;

import model.*;

public class BillMemento {
    private final Bill billState;

    public BillMemento(Bill bill) {
        this.billState = new Bill(bill); // Assumes Bill has a copy constructor
    }

    public Bill getSavedState() {
        return billState;
    }
} 