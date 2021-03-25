package boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


//Go to 127.0.0.1:5000 to view the app.
@SpringBootApplication
@ComponentScan({"boot", "controller", "eventhandler", "usecases"})
public class TradingApplication {

    /**
     * Starts the application
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(TradingApplication.class, args);
    }


}
