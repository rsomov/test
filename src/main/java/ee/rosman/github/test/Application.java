package ee.rosman.github.test;

import ee.rosman.github.test.service.DbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final DbService dbService;
    private final ApplicationContext appContext;

    public Application(DbService dbService, ApplicationContext appContext) {
        this.dbService = dbService;
        this.appContext = appContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {
        dbService.runScripts(appContext.getResources("classpath:db/*.js"));
    }
}
