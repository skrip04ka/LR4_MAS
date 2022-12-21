package org.example.DistributerBehaviour;

import org.example.Model.TopicData;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectBits extends Behaviour {

    private TopicData topicData;

    private MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchProtocol("price"));

    public CollectBits(TopicData topicData) {
        this.topicData = topicData;
    }

    @Override
    public void action() {
        ACLMessage receive = getAgent().receive(mt);
        if (receive != null){
            log.debug("get msg {} from {}", receive.getContent(), receive.getSender().getLocalName());
            topicData.getBitsData().put(receive.getSender(), Double.parseDouble(receive.getContent()));

        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
