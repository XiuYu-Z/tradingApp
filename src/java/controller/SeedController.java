package controller;

import entities.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import persistence.PersistenceInterface;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.command.action.AddToWishlist;
import usecases.command.action.ApproveItemToInventory;
import usecases.command.action.InitiateTransaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
public class SeedController extends AbstractBaseController {

    /**
     * Whether this application is seeded
     */
    private boolean seeded;

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;
    private final PersistenceInterface gateway;
    private InitiateTransaction initiateTransaction;
    private final ApproveItemToInventory approveItemToInventory;
    private final AddToWishlist addToWishlist;


    /**
     * Creates a test controller
     *
     * @param tradingFacade A facade that relates to trading.
     * @param systemFacade A facade that relates to system functions
     * @param gateway A class that handles persistence
     * @param approveItemToInventory The action of approving items to inventory
     * @param addToWishlist The action of adding to wishlist
     * @param initiateTransaction The action of initiating a transaction
     */
    public SeedController(TradingFacade tradingFacade, SystemFacade systemFacade,
                          PersistenceInterface gateway, ApproveItemToInventory approveItemToInventory,
                          AddToWishlist addToWishlist, InitiateTransaction initiateTransaction) {
        super(systemFacade);
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
        this.gateway = gateway;
        this.approveItemToInventory = approveItemToInventory;
        this.addToWishlist = addToWishlist;
        this.initiateTransaction = initiateTransaction;
    }

    /**
     * Deletes all data.
     * @param request A HTTP Request
     * @param redirectAttributes Spring redirect attributes object
     * @return A url to redirect to
     */
    @PostMapping("/data/delete")
    public String delete(HttpServletRequest request, RedirectAttributes redirectAttributes,
                         TemplateEngine templateEngine) {

        templateEngine.clearTemplateCache();

        request.getSession().invalidate();

        gateway.remove(Config.class);
        gateway.remove(History.class);
        gateway.remove(Item.class);
        gateway.remove(Meeting.class);
        gateway.remove(Tag.class);
        gateway.remove(Trade.class);
        gateway.remove(Transaction.class);
        gateway.remove(User.class);
        gateway.remove(WishList.class);

        this.seeded = false;

        redirectAttributes.addFlashAttribute("deleteSuccess", true);

        return "redirect:/";

    }


    /**
     * Create fake date for testing
     *
     * @return A url to redirect to
     */
    @PostMapping("/data/seed")
    public String seed(RedirectAttributes redirectAttributes) {

        if(!this.seeded) {
            try {
                /**
                 * CREATE FAKE USERS.
                 */
                int admin = this.systemFacade.auth().register("1@admin.com", "123", "Toronto");
                int userId1 = this.systemFacade.auth().register("1@1.com", "123", "Toronto");
                int userId2 = this.systemFacade.auth().register("2@2.com", "123", "Toronto");
                int userId3 = this.systemFacade.auth().register("3@3.com", "123", "Toronto");


                /**
                 * CREATE FAKE ITEMS.
                 */
                int itemId = tradingFacade.editItems().addItemToInventory("iPhone 7", "An old iPhone 7 that has a cracked screen.", userId1, 100, false);
                int itemId2 = tradingFacade.editItems().addItemToInventory("Instapot", "A pressure cooker specifically designed for lazy undergraduate students.", userId1, 200, false);
                int itemId3 = tradingFacade.editItems().addItemToInventory("Tricycle", "A special bike for those who don't like bicycles.", userId2, 300, false);
                int itemId4 = tradingFacade.editItems().addItemToInventory("USB Necklace", "A USB necklace containing a mysterious SSH private key.", userId2, 400, false);
                int itemId5 = tradingFacade.editItems().addItemToInventory("The Feynman Lectures on Physics", "An beginner level physics textbook.", userId3, 500, false);
                int itemId6 = tradingFacade.editItems().addItemToInventory("Linear Algebra Done Wrong", "An alternative to Linear Algebra Done Right", userId3, 600, false);
                int itemId7 = tradingFacade.editItems().addItemToInventory("Toyota Camry 2004", "Entry-level car for CS students", userId3, 7000, true);
                int itemId8 = tradingFacade.editItems().addItemToInventory("Ferrari 488 Pista", "Ideal car for a Rotman MBA student", userId3, 8000, true);

                //Add Tags
                tradingFacade.manageTags().tagItem("electronics,used", itemId);
                tradingFacade.manageTags().tagItem("kitchen,new", itemId2);
                tradingFacade.manageTags().tagItem("sports,outdoors", itemId3);
                tradingFacade.manageTags().tagItem("jewellry", itemId4);
                tradingFacade.manageTags().tagItem("book, physics", itemId5);
                tradingFacade.manageTags().tagItem("book, math", itemId6);
                tradingFacade.manageTags().tagItem("vehicle, toyota", itemId7);
                tradingFacade.manageTags().tagItem("vehicle, ferrari", itemId8);

                //Approve items
                approveItemToInventory.execute(itemId);
                approveItemToInventory.execute(itemId2);
                approveItemToInventory.execute(itemId3);
                approveItemToInventory.execute(itemId4);
                approveItemToInventory.execute(itemId5);
                approveItemToInventory.execute(itemId6);
                approveItemToInventory.execute(itemId7);

                //Add some to wishlist
                addToWishlist.execute(itemId2, userId2);
                addToWishlist.execute(itemId2, userId3);
                addToWishlist.execute(itemId4, userId1);
                addToWishlist.execute(itemId4, userId3);
                addToWishlist.execute(itemId6, userId1);
                addToWishlist.execute(itemId6, userId2);
                addToWishlist.execute(itemId8, userId2);

                //Build a first trade.
                this.initiateTransaction.execute(userId2, userId1, itemId2, itemId4, "twoWay",
                        "temporary", LocalDate.now().plusDays(3), "Bahen", "Robarts");

            } catch (Exception e) {
                e.printStackTrace();
            }

            redirectAttributes.addFlashAttribute("seedSuccess", true);

            this.seeded = true;

        }
        else {
            redirectAttributes.addFlashAttribute("alreadySeeded", true);
        }

        return "redirect:/";

    }

}
