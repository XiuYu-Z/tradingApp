package controller.forms;

public class CancelTransactionForm {
    private int transactionId;

    /**
     * Get transaction ID for further operation.
     *
     * @return The corresponding transactionId.
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Set the transaction Id.
     *
     * @param transactionId The ID of transaction.
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
