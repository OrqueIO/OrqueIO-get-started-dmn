package io.orqueio.bpm.getstarted.dmn;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.orqueio.bpm.dmn.engine.DmnDecisionTableResult;
import io.orqueio.bpm.engine.DecisionService;
import io.orqueio.bpm.engine.variable.VariableMap;
import io.orqueio.bpm.engine.variable.Variables;
import io.orqueio.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import io.orqueio.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class Application {

    protected final static Logger LOGGER = Logger.getLogger(Application.class.getName());

    @Autowired
    private DecisionService decisionService;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    public void processPostDeploy(PostDeployEvent event) {
        VariableMap variables = Variables.createVariables()
                .putValue("paysDestination", "France")
                .putValue("poidsColis", 6.0)
                .putValue("typeLivraison", "Standard");

        DmnDecisionTableResult carrierDecisionResult = decisionService.evaluateDecisionTableByKey("carrier", variables);
        String transporteur = carrierDecisionResult.getSingleEntry();

        LOGGER.log(Level.INFO, "\n\nSelected carrier: {0}\n\n", transporteur);
    }
}