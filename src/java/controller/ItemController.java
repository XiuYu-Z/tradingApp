package controller;

import controller.forms.ItemAddForm;
import controller.forms.TagFilterForm;
import controller.forms.WishlistAddForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import presenter.ItemPresenter;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.command.action.AddToWishlist;
import usecases.items.ItemQueryBuilder;
import usecases.users.PermissionsManager;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class ItemController extends AbstractBaseController {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final AddToWishlist addWishlist;
    private final ItemPresenter itemPresenter;


    /**
     * Create a new item controller
     *
     * @param tradingFacade A facade that contains trading related functionality.
     * @param systemFacade  A facade that contains system related functionality.
     * @param addToWishlist Action to add to wishlist
     * @param itemPresenter The item presenter
     */
    public ItemController(TradingFacade tradingFacade, SystemFacade systemFacade,
                          AddToWishlist addToWishlist, ItemPresenter itemPresenter) {
        super(systemFacade);
        this.tradingFacade = tradingFacade;
        this.addWishlist = addToWishlist;
        this.itemPresenter = itemPresenter;
    }


/********************************************************************************************************
 *
 * Wishlist
 *
 *********************************************************************************************************/

    /**
     * Displays the wishlist of the user.
     *
     * @param model   Holds data to send to the view.
     * @param request An object holding the HTTP request.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/wishlist/view")
    public String viewWishlist(Model model, HttpServletRequest request) throws IOException {
        int userId = this.getLoggedInUserId(request);
        return itemPresenter.viewWishlistPresenter(model, userId);
    }


    /**
     * Handles adding an item to a wishlist.
     *
     * @param request         An object holding the HTTP request.
     * @param wishlistAddForm The form holding the front-end input.
     * @return A url to which we redirect to.
     * @throws IOException An IOException
     */
    @PostMapping("/wishlist/add")
    public String handleAddToWishlist(HttpServletRequest request,
                                      @ModelAttribute WishlistAddForm wishlistAddForm) throws IOException {

        if (this.isDemo(request)) return "redirect:/browse";

        int userId = this.getLoggedInUserId(request);
        this.addWishlist.execute(wishlistAddForm.getItemId(), userId);

        return "redirect:/browse?addSuccess=true";
    }


/********************************************************************************************************
 *
 * Inventory
 *
 *********************************************************************************************************/


    /**
     * Displays all items ot the logged in user.
     *
     * @param model   Holds data to send to the view.
     * @param request An object holding the HTTP request.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/items/all")
    public String myItems(Model model, HttpServletRequest request) throws IOException {

        int userId = (Integer) request.getSession().getAttribute("userId");
        return itemPresenter.myItemsPresenter(model, userId);
    }


    /**
     * Shows the form for adding an item.
     *
     * @param model      Holds data to send to the view.
     * @param addSuccess An optional parameter indicating whether the adding succeeded.
     * @return A string indicating which view to display.
     */
    @GetMapping("/items/add")
    public String addItem(Model model, @RequestParam(required = false) String addSuccess) {

        if (addSuccess != null && addSuccess.equals("true")) {
            itemPresenter.addSuccess(model);
        }
        return itemPresenter.addItemPresenter(model);
    }


    /**
     * Handles adding an item to inventory.
     *
     * @param form    The form holding the front-end input.
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect to.
     * @throws IOException An IOException
     */
    @PostMapping("/items/add")
    public String handleAddItem(@Valid @ModelAttribute ItemAddForm form,
                                Errors error, HttpServletRequest request) throws IOException {
        if (error.hasErrors()) {
            return "items/addItem";
        }

        if (this.isDemo(request)) return "redirect:/items/add";

        int userId = this.getLoggedInUserId(request);

        String itemName = form.getItemName();
        String itemDescription = form.getItemDescription();
        int price = form.getPrice();
        boolean forSale = form.getForSale();

        int itemId = tradingFacade.editItems().addItemToInventory(itemName, itemDescription, userId, price, forSale);

        tradingFacade.manageTags().tagItem(form.getTags(), itemId);

        return "redirect:/items/add?addSuccess=true";

    }


/********************************************************************************************************
 *
 * Browse
 *
 *********************************************************************************************************/


    /**
     * Browse all items on the platform that this user can interact with.
     *
     * @param model         Holds data to send to the view.
     * @param request       An object holding the HTTP request.
     * @param addSuccess    An optional parameter indicating whether the adding succeeded.
     * @param tagFilterForm The form holding the front-end input.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/browse")
    public String browse(Model model, HttpServletRequest request, @RequestParam(required = false) String addSuccess,
                         @ModelAttribute TagFilterForm tagFilterForm) throws IOException {

        int userId = this.getLoggedInUserId(request);

        if (addSuccess != null && addSuccess.equals("true")) {
            itemPresenter.addSuccess(model);
        }

        ItemQueryBuilder query = tradingFacade.fetchItems().browsableItems();

        //Run tag filters
        query.isTaggedWithAll(tagFilterForm.getTag());

        //If the user is logged in, then we add an additional filter of not retrieving the logged in user's items.
        if (this.isLoggedIn(request)) {
            //Remove the user's own items and only show items in the user's home city
            query.exceptOwnedBy(userId).findByHomeCity(userId);
        }
        List<Integer> myWishlist = tradingFacade.fetchItems().query().inWishlistOf(userId).getIds();
        Boolean canTrade = this.canTrade(userId);

        return itemPresenter.browsePresenter(model, query, myWishlist, canTrade);
    }

    /**
     * Determines whether a user can trade.
     *
     * @param userId the unique id of a user
     * @return Whether the user can trade.
     * @throws IOException An IOException.
     */
    protected boolean canTrade(int userId) throws IOException {

        PermissionsManager permissionsManager = this.systemFacade.permissionsManager();

        boolean canTrade = (permissionsManager.canLend(userId) || permissionsManager.canBorrow(userId));
        boolean notFrozen = !permissionsManager.isFrozen(userId);
        boolean notPaused = !permissionsManager.isFrozen(userId);

        return canTrade && notFrozen && notPaused;

    }


}
