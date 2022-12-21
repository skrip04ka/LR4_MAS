package LR4;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockAgent extends Agent {

    @Override
    protected void setup() {
        log.info("Mock agent {} starts {} behvaiours", this.getLocalName(), getArguments().length);
        for (Object arg : getArguments()) {
            if (!(arg instanceof Behaviour)){
                throw new RuntimeException("WRONG USING MOCK AGENT!!!!!");
            }
            Behaviour beh = (Behaviour) arg;
            this.addBehaviour(beh);
        }
    }
}
