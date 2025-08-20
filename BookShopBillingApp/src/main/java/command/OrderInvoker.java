package command;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * Command Pattern: Order Command Invoker
 * Manages command execution and maintains command history for undo operations
 */
public class OrderInvoker {
    private final Stack<OrderCommand> commandHistory;
    private final List<OrderCommand> pendingCommands;
    
    public OrderInvoker() {
        this.commandHistory = new Stack<>();
        this.pendingCommands = new ArrayList<>();
    }
    
    /**
     * Execute a command and add to history
     * @param command the command to execute
     * @return true if command executed successfully, false otherwise
     */
    public boolean executeCommand(OrderCommand command) {
        if (command == null) {
            System.err.println("Cannot execute null command");
            return false;
        }
        
        try {
            System.out.println("Invoker executing command: " + command.getDescription());
            
            boolean success = command.execute();
            
            if (success) {
                commandHistory.push(command);
                System.out.println("Command executed successfully and added to history");
            } else {
                System.err.println("Command execution failed");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Undo the last executed command
     * @return true if undo was successful, false otherwise
     */
    public boolean undoLastCommand() {
        if (commandHistory.isEmpty()) {
            System.out.println("No commands to undo");
            return false;
        }
        
        try {
            OrderCommand lastCommand = commandHistory.pop();
            System.out.println("Undoing command: " + lastCommand.getDescription());
            
            boolean success = lastCommand.undo();
            
            if (success) {
                System.out.println("Command undone successfully");
            } else {
                System.err.println("Failed to undo command");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Error undoing command: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add command to pending queue
     * @param command the command to queue
     */
    public void queueCommand(OrderCommand command) {
        if (command != null) {
            pendingCommands.add(command);
            System.out.println("Command queued: " + command.getDescription());
        }
    }
    
    /**
     * Execute all pending commands
     * @return number of successfully executed commands
     */
    public int executePendingCommands() {
        if (pendingCommands.isEmpty()) {
            System.out.println("No pending commands to execute");
            return 0;
        }
        
        int successCount = 0;
        List<OrderCommand> commandsToExecute = new ArrayList<>(pendingCommands);
        pendingCommands.clear();
        
        for (OrderCommand command : commandsToExecute) {
            if (executeCommand(command)) {
                successCount++;
            }
        }
        
        System.out.println("Executed " + successCount + " out of " + commandsToExecute.size() + " pending commands");
        return successCount;
    }
    
    /**
     * Get command history
     * @return list of executed commands
     */
    public List<OrderCommand> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Get pending commands
     * @return list of pending commands
     */
    public List<OrderCommand> getPendingCommands() {
        return new ArrayList<>(pendingCommands);
    }
    
    /**
     * Clear command history
     */
    public void clearHistory() {
        commandHistory.clear();
        System.out.println("Command history cleared");
    }
    
    /**
     * Get history size
     * @return number of commands in history
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
    
    /**
     * Get pending commands count
     * @return number of pending commands
     */
    public int getPendingCount() {
        return pendingCommands.size();
    }
} 