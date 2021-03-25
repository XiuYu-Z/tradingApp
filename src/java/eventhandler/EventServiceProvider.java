package eventhandler;

import eventhandler.events.AbstractEvent;
import eventhandler.events.AppConfigChangedEvent;
import eventhandler.events.UserRegisteredEvent;
import eventhandler.listeners.ConfigNotifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import usecases.config.ListensForConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EventServiceProvider {

    /**
     * Loads the event handler initially on boot.
     *
     * @param event A Spring event which indicates the application context has been refreshed.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();

        Map<String, ? extends ListensForConfig> configListeners = applicationContext.getBeansOfType(ListensForConfig.class);

        for (Map.Entry<String, ? extends ListensForConfig> entry : configListeners.entrySet()) {
            applicationContext.getBean(ConfigNotifier.class).addListener(entry.getValue());
        }

        //We bind our events to the event handler
        List<AbstractEvent> events = new ArrayList<>();
        events.add(applicationContext.getBean(UserRegisteredEvent.class));
        events.add(applicationContext.getBean(AppConfigChangedEvent.class));
        applicationContext.getBean(EventHandler.class).register(events);

    }


}
