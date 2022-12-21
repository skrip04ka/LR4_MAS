package org.example.ProducerBehaviour;

import org.example.Model.CurrentProduserData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceiveAnswers extends Behaviour {

    private AID topic;

    private CurrentProduserData currentProduserData;

    private MessageTemplate mt;


    public ReceiveAnswers(AID topic, CurrentProduserData currentProduserData) {
        this.topic = topic;
        this.currentProduserData = currentProduserData;
        mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.and(
                        MessageTemplate.MatchTopic(topic),
                        MessageTemplate.MatchProtocol("price")));
    }

    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(mt);
        if (msg != null) {
            if (currentProduserData.isAuction()) {
                double otherPrice = Double.parseDouble(msg.getContent());
                if (otherPrice < currentProduserData.getCurrentPrice()) {
                    currentProduserData.changePrice(otherPrice);
                    if (!currentProduserData.isAuction()) {
                        log.info("{} stop down price in {}", myAgent.getLocalName(), topic.getLocalName());
                    }

                    ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                    m.setContent(String.valueOf(currentProduserData.getCurrentPrice()));
                    m.addReceiver(topic);
                    m.setProtocol("price");
                    myAgent.send(m);
                    log.debug("send price {} to {}", m.getContent(), topic.getLocalName());
                }
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
