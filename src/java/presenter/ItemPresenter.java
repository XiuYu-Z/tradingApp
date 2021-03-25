package presenter;

import controller.forms.ItemAddForm;
import controller.forms.TagFilterForm;
import controller.forms.WishlistAddForm;
import entities.Item;
import org.springframework.ui.Model;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.items.ItemQueryBuilder;

import java.io.IOException;
import java.util.List;

public class ItemPresenter {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;

    /**
     * Instantiates this presenter.
     *
     * @param tradingFacade The facade holding trading related functionality.
     * @param systemFacade  The facade holding system related functionality.
     */
    public ItemPresenter(TradingFacade tradingFacade, SystemFacade systemFacade) {
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
    }

    /**
     * Shows the wishlist page.
     *
     * @param model  A model that holds dynamic data.
     * @param userId The user id
     * @return The view
     * @throws IOException An IOException
     */
    public String viewWishlistPresenter(Model model, int userId) throws IOException {
        List<Item> items = tradingFacade.fetchItems().viewMyWishlist(userId).getObjects();
        model.addAttribute("myWishlist", items);
        model.addAttribute("itemTagMap", systemFacade.tagManager().getItemTagMap());

        return "items/myWishlist";
    }

    /**
     * Shows the user's items.
     *
     * @param model  A model that holds dynamic data.
     * @param userId The user id
     * @return The view
     * @throws IOException An IOException
     */
    public String myItemsPresenter(Model model, int userId) throws IOException {
        List<Item> items = tradingFacade.fetchItems().allMyItems(userId).getObjects();
        model.addAttribute("myItems", items);
        model.addAttribute("itemTagMap", systemFacade.tagManager().getItemTagMap());

        return "items/myItems";

    }

    /**
     * Shows the add item page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String addItemPresenter(Model model) {
        model.addAttribute("itemAddForm", new ItemAddForm());
        return "items/addItem";
    }

    /**
     * Shows the browse item page.
     *
     * @param model      A model that holds dynamic data.
     * @param query      The Item builder query.
     * @param myWishlist My wishlist
     * @param canTrade   Whether the user can trade
     * @return The view
     * @throws IOException An IOException
     */
    public String browsePresenter(Model model, ItemQueryBuilder query, List<Integer> myWishlist, Boolean canTrade)
            throws IOException {

        model.addAttribute("tagFilterForm", new TagFilterForm());
        model.addAttribute("wishlistAddForm", new WishlistAddForm());
        model.addAttribute("allTags", systemFacade.tagManager().all());
        model.addAttribute("availableItems", query.getObjects());
        model.addAttribute("myWishlist", myWishlist);
        model.addAttribute("canTrade", canTrade);
        model.addAttribute("itemTagMap", systemFacade.tagManager().getItemTagMap());

        return "items/browse";
    }

    /**
     * Whether adding an item was successful.
     *
     * @param model A model that holds dynamic data.
     */
    public void addSuccess(Model model) {
        model.addAttribute("addSuccess", true);
    }


}
