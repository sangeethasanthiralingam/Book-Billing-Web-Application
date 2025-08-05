package memento;

import java.util.Stack;
import model.Bill;

public class BillHistory {
    private Stack<BillMemento> history = new Stack<>();

    public void save(Bill bill) {
        history.push(bill.saveToMemento());
    }

    public void undo(Bill bill) {
        if (!history.isEmpty()) {
            bill.restoreFromMemento(history.pop());
        }
    }
} 