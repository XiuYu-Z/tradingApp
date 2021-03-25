package usecases.alerts;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import usecases.rules.SystemRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The system to handle context refresh event
 */
@Component
public class AlertServiceProvider {

    /**
     * Loads the alerts on boot.
     *
     * @param event A Spring event which indicates the application context has been refreshed.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();

        Map<String, ? extends SystemAlert> alerts = applicationContext.getBeansOfType(SystemAlert.class);
        List<SystemAlert> alertList = new ArrayList<>(alerts.values());

        AlertManager alertManager = applicationContext.getBean(AlertManager.class);
        alertManager.setAlerts(alertList);


        Map<String, ? extends SystemRule> rules = applicationContext.getBeansOfType(SystemRule.class);
        List<SystemRule> ruleList = new ArrayList<>(rules.values());
        FreezeUserAlert freezeUserAlert = applicationContext.getBean(FreezeUserAlert.class);
        freezeUserAlert.setRules(ruleList);

    }


}
