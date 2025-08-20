package command;

/**
 * Command Pattern: Order Command Interface
 * Defines the contract for order-related commands
 */
public interface OrderCommand {
    /**
     * Execute the command
     * @return true if command executed successfully, false otherwise
     */
    boolean execute();
    
    /**
     * Undo the command (if possible)
     * @return true if command was undone successfully, false otherwise
     */
    boolean undo();
    
    /**
     * Get command description
     * @return human-readable description of the command
     */
    String getDescription();
    
    /**
     * Get command type
     * @return the type of command
     */
    String getCommandType();
} 