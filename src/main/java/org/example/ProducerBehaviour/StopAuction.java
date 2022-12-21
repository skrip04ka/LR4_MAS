package org.example.ProducerBehaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopAuction extends Behaviour {

    private boolean isEnd = false;

    private MessageTemplate mt;

    public StopAuction(AID topic) {
        mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                MessageTemplate.and(
                        MessageTemplate.MatchTopic(topic),
                        MessageTemplate.MatchProtocol("stop auction")));
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            log.debug("get msg {} from {}", msg.getContent(), msg.getSender().getLocalName());
            isEnd = true;

        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return isEnd;
    }
}
