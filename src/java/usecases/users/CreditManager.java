package usecases.users;

import entities.Item;
import entities.Trade;
import entities.Transaction;
import entities.User;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreditManager {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final MapsRelations relationMapper;

    /**
     * Instantiates an instance of this class.
     *
     * @param gateway        The persistence method
     * @param relationMapper A relational mapper to map relations between entities
     */
    public CreditManager(PersistenceInterface gateway, MapsRelations relationMapper) {
        this.gateway = gateway;
        this.relationMapper = relationMapper;
    }

    /**
     * Updates the number of credits a user has.
     *
     * @param userId               The unique id of the user
     * @param completeTransactions The list of transactions that has been completed.
     * @param failedTransactions   The list of transactions that have failed.
     * @throws IOException An IOException
     */
    public void updatePoint(int userId, List<Transaction> completeTransactions,
                            List<Transaction> failedTransactions) throws IOException {
        User user = getUser(userId);
        user.setCredit(calculatePoint(completeTransactions, failedTransactions));
        gateway.update(wrap(user), User.class);
    }


    /**
     * Get the number of credits a user has.
     *
     * @param userId The unique id of the user
     * @return The number of credits the user has.
     * @throws IOException An IOException
     */
    public int getCredit(int userId) throws IOException {
        User user = getUser(userId);
        return user.getCredit();
    }


    /********************************************************************************************************
     *
     * Helper methods
     *
     *********************************************************************************************************/

    // Private functions used as helper functions
    private User getUser(int userId) throws IOException {
        List<Integer> ids = new ArrayList<>();
        ids.add(userId);
        return gateway.get(ids, User.class).get(0);

    }

    private List<User> wrap(User user) {
        List<User> users = new ArrayList<>();
        users.add(user);
        return users;
    }

    private int calculatePoint(List<Transaction> completeTransactions, List<Transaction> failedTransactions)
            throws IOException {

        int point = 0;
        for (Transaction t : completeTransactions) {
            point += calculatePointPerTransaction(t);
        }

        for (Transaction t : failedTransactions) {
            point -= calculatePointPerTransaction(t) * 5;
        }

        return point;
    }

    private int calculatePointPerTransaction(Transaction transaction) throws IOException {
        int point = 0;
        List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
        if (trades.get(0).getSell()) {
            for (Item i : trades.get(0).relation(relationMapper, "items", Item.class))
                point += i.getPrice();
        } else {
            for (Trade trade : trades) {
                for (Item i : trade.relation(relationMapper, "items", Item.class))
                    point += i.getPrice() / 2;
            }

        }

        return point;
    }

}
