package org.example.ProducerBehaviour;

import org.example.Model.ProducerData;
import org.example.TopicHelper;
import org.example.time.TimeClass;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ReceiveTopicName extends Behaviour {
    private ProducerData producerData;
    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchProtocol("topicName"));

    public ReceiveTopicName(ProducerData data) {
        this.producerData = data;
    }

    @Override
    public void onStart() {
        myAgent.addBehaviour(new TickerBehaviour(myAgent, TimeClass.getTime()) {
            @Override
            protected void onTick() {
                log.info("free power {}", producerData.getPower());
            }
        });
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            log.debug("get topic name <{}>", msg.getContent());
            AID topic = TopicHelper.createTopic(myAgent, msg.getContent());
            myAgent.addBehaviour(new ProducerFSM(topic, producerData));
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
