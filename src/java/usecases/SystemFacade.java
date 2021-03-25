package usecases;

import usecases.alerts.AlertManager;
import usecases.config.ConfigManager;
import usecases.rules.RuleValidator;
import usecases.tags.TagManager;
import usecases.users.Authenticator;
import usecases.users.CreditManager;
import usecases.users.PermissionsManager;
import usecases.users.UserManager;

/**
 * A facade for user/system-related use case classes.
 */
public class SystemFacade {

    /**
     * Class dependencies
     */
    private final AlertManager alert;
    private final PermissionsManager permissionsManager;
    private final Authenticator auth;
    private final ConfigManager config;
    private final RuleValidator rules;
    private final UserManager users;
    private final CreditManager credit;
    private final TagManager tagManager;

    /**
     * Initializes this class.
     *
     * @param alert              AlertManager
     * @param permissionsManager PermissionsManager
     * @param auth               Authenticator
     * @param config             ConfigManager
     * @param rules              RuleValidator
     * @param users              UserManager
     * @param credit             CreditManager
     * @param tagManager         TagManager
     */
    public SystemFacade(AlertManager alert, PermissionsManager permissionsManager, Authenticator auth,
                        ConfigManager config, RuleValidator rules, UserManager users, CreditManager credit,
                        TagManager tagManager) {
        this.alert = alert;
        this.permissionsManager = permissionsManager;
        this.auth = auth;
        this.config = config;
        this.rules = rules;
        this.users = users;
        this.credit = credit;
        this.tagManager = tagManager;
    }

    /**
     * Returns an instance of AlertManager.
     *
     * @return Returns an instance of AlertManager.
     */
    public AlertManager alert() {
        return this.alert;
    }

    /**
     * Returns an instance of PermissionsManager.
     *
     * @return Returns an instance of PermissionsManager.
     */
    public PermissionsManager permissionsManager() {
        return this.permissionsManager;
    }

    /**
     * Returns an instance of Authenticator.
     *
     * @return Returns an instance of Authenticator.
     */
    public Authenticator auth() {
        return this.auth;
    }

    /**
     * Returns an instance of ConfigManager.
     *
     * @return Returns an instance of ConfigManager.
     */
    public ConfigManager config() {
        return this.config;
    }

    /**
     * Returns an instance of RuleValidator.
     *
     * @return Returns an instance of RuleValidator.
     */
    public RuleValidator rules() {
        return this.rules;
    }

    /**
     * Returns an instance of UserManager.
     *
     * @return Returns an instance of UserManager.
     */
    public UserManager users() {
        return this.users;
    }

    /**
     * Returns an instance of CreditManager.
     *
     * @return Returns an instance of CreditManager.
     */
    public CreditManager credit() {
        return this.credit;
    }


    /**
     * Returns an instance of TagManager
     *
     * @return Returns an instance of TagManager
     */
    public TagManager tagManager() {
        return this.tagManager;
    }


}


