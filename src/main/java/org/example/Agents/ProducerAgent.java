package org.example.Agents;

import org.example.DfHelper;
import org.example.AutorunnableAgent;
import org.example.Model.ProducerData;
import org.example.ProducerBehaviour.ReceiveTopicName;
import org.example.cfg.ProducerCFG;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@AutorunnableAgent(name = "P", starIndex = 1, count = 3)
@Slf4j
public class ProducerAgent extends Agent {

    @Override
    protected void setup() {
        DfHelper.registerAgent(this, "producer");
        log.info("agent {} is start", this.getLocalName());

        ProducerCFG cfg;
        try {
            JAXBContext context = JAXBContext.newInstance(ProducerCFG.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            cfg = (ProducerCFG) jaxbUnmarshaller.unmarshal(
                    new File("src/main/resources/LR4/Producer/"  + getLocalName() + ".xml"));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new ReceiveTopicName(new ProducerData(cfg)));

    }
}


