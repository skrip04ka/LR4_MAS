package org.example.Agents;

import org.example.DfHelper;
import org.example.AutorunnableAgent;
import org.example.ConsumerBehaviour.SendRequest;
import org.example.cfg.ConsumerCFG;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@AutorunnableAgent(name = "L", starIndex = 1, count = 3)
@Slf4j
public class ConsumerAgent extends Agent {
    @Override
    protected void setup() {
        DfHelper.registerAgent(this, "consumer:" + getLocalName());
        log.info("agent {} is start", getLocalName());

        ConsumerCFG cfg;
        try {
            JAXBContext context = JAXBContext.newInstance(ConsumerCFG.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            cfg = (ConsumerCFG) jaxbUnmarshaller.unmarshal(
                    new File("src/main/resources/LR4/Consumer/"  + getLocalName() + ".xml"));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }


        addBehaviour(new SendRequest(cfg));
    }
}
