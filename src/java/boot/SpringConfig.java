package boot;

import controller.AuthInterceptor;
import eventhandler.EventHandler;
import eventhandler.HandlesEvents;
import eventhandler.events.AppConfigChangedEvent;
import eventhandler.events.UserRegisteredEvent;
import eventhandler.listeners.ConfigNotifier;
import eventhandler.listeners.WishlistInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import persistence.PersistenceInterface;
import persistence.SerPersistenceGateway;
import persistence.relations.RelationMapper;
import presenter.*;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.alerts.AddInventoryAlert;
import usecases.alerts.AlertManager;
import usecases.alerts.FreezeUserAlert;
import usecases.alerts.UnfreezeUserAlert;
import usecases.command.CommandManager;
import usecases.command.action.Actionable;
import usecases.command.action.AddToWishlist;
import usecases.command.action.ApproveItemToInventory;
import usecases.command.action.InitiateTransaction;
import usecases.config.ConfigManager;
import usecases.items.ItemEditor;
import usecases.items.ItemFetcher;
import usecases.meeting.MeetingFactory;
import usecases.meeting.MeetingManager;
import usecases.rules.MaxIncompleteTransactionRule;
import usecases.rules.MaxTransactionPerWeekRule;
import usecases.rules.NoMoreBorrowThanLendRule;
import usecases.rules.RuleValidator;
import usecases.tags.TagManager;
import usecases.trade.TradeFactory;
import usecases.trade.TransactionFetcher;
import usecases.trade.TransactionManager;
import usecases.users.Authenticator;
import usecases.users.CreditManager;
import usecases.users.PermissionsManager;
import usecases.users.UserManager;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SpringConfig implements WebMvcConfigurer {

    /**
     * Adds interceptors to run prior to controller methods.
     * @param registry The registry of all interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(this.getSystemFacade()));
    }

    /**
     * instantiate SerPersistenceInterface
     * @return  new SerPersistenceGateway
     */
    @Bean
    public PersistenceInterface getPersistence() {
        return new SerPersistenceGateway();
    }

    /**
     * instantiate RelationMapper
     * @return new RelationMapper
     */
    @Bean
    public RelationMapper getRelation() {
        return new RelationMapper(this.getPersistence());
    }

    /**
     * instantiate HandlesEvents
     * @return new HandlesEvents
     */
    @Bean
    public HandlesEvents getEventHandler() {
        return new EventHandler();
    }

    /**
     * instantiate MeetingManager
     * @return new MeetingManager
     */
    @Bean
    public MeetingManager getMeetingManager() {
        return new MeetingManager(this.getPersistence(), this.getRelation());
    }

    /**
     * instantiate TransactionFetcher
     * @return new TransactionFetcher
     */
    @Bean
    public TransactionFetcher getTransactionFetcher() {
        return new TransactionFetcher(this.getPersistence(), this.getRelation());
    }

    /**
     * instantiate TransactionManager
     * @return new TransactionManager
     */
    @Bean
    public TransactionManager getTransactionManager() {
        return new TransactionManager(this.getPersistence(), this.getTransactionFetcher(), this.getRelation(), this.getTradeFactory(), this.getMeetingFactory());
    }

    /**
     * instantiate UserManager
     * @return new UserManager
     */
    @Bean
    public UserManager getUserManager() {
        return new UserManager(this.getPersistence());
    }


    /**
     * instantiate ItemEditor
     * @return new ItemEditor
     */
    @Bean
    public ItemEditor getItemEditor() {
        return new ItemEditor(this.getPersistence());
    }

    /**
     * instantiate ItemFetcher
     * @return new ItemFetcher
     */
    @Bean
    public ItemFetcher getItemFetcher() {
        return new ItemFetcher(this.getPersistence(), this.getRelation());
    }

    /**
     * instantiate TradeFactory
     * @return new TradeFactory
     */
    @Bean
    public TradeFactory getTradeFactory() {
        return new TradeFactory(this.getPersistence());
    }

    /**
     * instantiate MeetingFactory
     * @return new MeetingFactory
     */
    @Bean
    public MeetingFactory getMeetingFactory() {
        return new MeetingFactory(this.getPersistence());
    }

    /**
     * instantiate Authenticator
     * @return new Authenticator
     */
    @Bean
    public Authenticator getAuthenticator() {
        return new Authenticator(this.getPersistence(), this.getEventHandler());
    }

    /**
     * instantiate ConfigManager
     * @return new ConfigManage
     */
    @Bean
    public ConfigManager getConfigManager() {
        return new ConfigManager(this.getPersistence(), this.getEventHandler());
    }

    /**
     * instantiate CreditManager
     * @return new CreditManager
     */
    @Bean
    public CreditManager getCreditManager() {
        return new CreditManager(this.getPersistence(), this.getRelation());
    }


    /**
     * instantiate TagManager
     * @return new TagManager
     */
    @Bean
    public TagManager getTagManager() {
        return new TagManager(this.getPersistence(), this.getRelation());
    }


    /**
     * instantiate TradingFacade
     * @return new TradingFacade
     */
    @Bean
    public TradingFacade getTradingFacade() {
        return new TradingFacade(this.getItemEditor(),
                this.getItemFetcher(),
                this.getMeetingFactory(),
                this.getMeetingManager(),
                this.getTradeFactory(),
                this.getTransactionManager(),
                this.getTransactionFetcher(),
                this.getTagManager()
        );
    }

    /**
     * instantiate AddToWishlist
     * @return new AddToWishlist
     */
    @Bean
    public AddToWishlist getAddToWishlist() {
        return new AddToWishlist(this.getItemEditor(), this.getItemFetcher(), this.getPersistence());
    }

    /**
     * instantiate ApproveItemToInventory
     * @return new ApproveItemToInventory
     */
    @Bean
    public ApproveItemToInventory getApproveToInventory() {
        return new ApproveItemToInventory(this.getItemEditor(), this.getItemFetcher(), this.getTransactionFetcher(), this.getPersistence());
    }

    /**
     * instantiate InitiateTransaction
     * @return new InitiateTransaction
     */
    @Bean
    public InitiateTransaction getInitiateTransaction() {
        return new InitiateTransaction(this.getPersistence(), this.getTransactionManager(), this.getTransactionFetcher());
    }

    /**
     * instantiate CommandManager
     * @return new CommandManager
     */
    @Bean
    public CommandManager getCommandManager() {
        List<Actionable> actionables = new ArrayList<>();
        actionables.add(this.getAddToWishlist());
        actionables.add(this.getApproveToInventory());
        actionables.add(this.getInitiateTransaction());

        return new usecases.command.CommandManager(this.getPersistence(), actionables);

    }

    /**
     * instantiate MaxIncompleteTransactionRule
     * @return new MaxIncompleteTransactionRule
     */
    @Bean
    public MaxIncompleteTransactionRule getMaxIncompleteTransactionRule() {
        return new MaxIncompleteTransactionRule();
    }

    /**
     * instantiate MaxTransactionPerWeekRule
     * @return new MaxTransactionPerWeekRule
     */
    @Bean
    public MaxTransactionPerWeekRule getMaxTransactionPerWeekRule() {
        return new MaxTransactionPerWeekRule();
    }

    /**
     * instantiate NoMoreBorrowThanLendRule
     * @return new NoMoreBorrowThanLendRule
     */
    @Bean
    public NoMoreBorrowThanLendRule getNoMoreBorrowThanLendRule() {
        return new NoMoreBorrowThanLendRule();
    }

    /**
     * instantiate AlertManager
     * @return new AlertManager
     */
    @Bean
    public AlertManager getAlertManager() {
        return new AlertManager(this.getPersistence());
    }

    /**
     * instantiate AddInventoryAlert
     * @return new AddInventoryAlert
     */
    @Bean
    public AddInventoryAlert getAddInventoryAlert() {
        return new AddInventoryAlert(this.getPersistence());
    }

    /**
     * instantiate FreezeUserAlert
     * @return new FreezeUserAlert
     */
    @Bean
    public FreezeUserAlert getFreezeUserAlert() {
        return new FreezeUserAlert(this.getRuleValidator());
    }

    /**
     * instantiate UnfreezeUserAlert
     * @return new UnfreezeUserAlert
     */
    @Bean
    public UnfreezeUserAlert getUnfreezeUserAlert() {
        return new UnfreezeUserAlert(this.getPersistence());
    }

    /**
     * instantiate RuleValidator
     * @return new RuleValidator
     */
    @Bean
    public RuleValidator getRuleValidator() {
        return new RuleValidator(this.getTransactionFetcher());
    }

    /**
     * instantiate PermissionsManager
     * @return new PermissionsManager
     */
    @Bean
    public PermissionsManager getPermissionsManager() {
        return new PermissionsManager(this.getPersistence(), this.getRuleValidator());
    }

    /**
     * instantiate SystemFacade
     * @return new SystemFacade
     */
    @Bean
    public SystemFacade getSystemFacade() {
        return new SystemFacade(this.getAlertManager(), this.getPermissionsManager(), this.getAuthenticator(),
                this.getConfigManager(), this.getRuleValidator(), this.getUserManager(), this.getCreditManager(),
                this.getTagManager());
    }

    /**
     * instantiate WishlistInitializer
     * @return new WishlistInitializer
     */
    @Bean
    public WishlistInitializer getWishlistInitializer() {
        return new WishlistInitializer(this.getItemEditor());
    }


    /**
     * instantiate UserRegisteredEvent
     * @return new UserRegisteredEvent
     */
    @Bean
    public UserRegisteredEvent getUserRegisteredEvent() {
        return new UserRegisteredEvent(this.getWishlistInitializer());
    }

    /**
     * instantiate ConfigNotifier
     * @return new ConfigNotifier
     */
    @Bean
    public ConfigNotifier getConfigNotifier() {
        return new ConfigNotifier(this.getConfigManager());
    }

    /**
     * instantiate AppConfigChangedEvent
     * @return new AppConfigChangedEvent
     */
    @Bean
    public AppConfigChangedEvent getAppConfigChangedEvent() {
        return new AppConfigChangedEvent(this.getConfigNotifier());
    }

    /**
     * instantiate AdminPresenter
     * @return new AdminPresenter
     */
    @Bean
    public AdminPresenter getAdminPresenter() {
        return new AdminPresenter(getSystemFacade(), getTradingFacade(), getCommandManager());
    }

    /**
     * instantiate ItemPresenter
     * @return new ItemPresenter
     */
    @Bean
    public ItemPresenter getItemPresenter() {
        return new ItemPresenter(getTradingFacade(), getSystemFacade());
    }

    /**
     * instantiate MeetingPresenter
     * @return new MeetingPresenter
     */
    @Bean
    public MeetingPresenter getMeetingPresenter() {
        return new MeetingPresenter(getTradingFacade());
    }

    /**
     * instantiate UserPresenter
     * @return new UserPresenter
     */
    @Bean
    public UserPresenter getUserPresenter() {
        return new UserPresenter(getTradingFacade(), getSystemFacade());
    }

    /**
     * instantiate TransactionPresenter
     * @return new TransactionPresenter
     */
    @Bean
    public TransactionPresenter getTransactionPresenter() {
        return new TransactionPresenter(getTradingFacade(), getSystemFacade());
    }
}
