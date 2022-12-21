package org.example.DistributerBehaviour;

import org.example.Model.TopicData;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcceptRequestPrice extends Behaviour {
    private boolean isEnd = false;

    private MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.MatchProtocol("result price"));

    private TopicData data;

    public AcceptRequestPrice(TopicData data) {
        this.data = data;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            log.debug("get msg {} from {}", msg.getContent(), msg.getSender().getLocalName());

            ACLMessage m = new ACLMessage(ACLMessage.AGREE);
            m.setContent("stop auction");
            m.addReceiver(data.getTopic());
            m.setProtocol("stop auction");
            myAgent.send(m);
            log.info("{} in {}", m.getContent(), data.getTopic().getLocalName());


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
