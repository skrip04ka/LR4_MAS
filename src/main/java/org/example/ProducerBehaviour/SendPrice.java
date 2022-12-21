package org.example.ProducerBehaviour;

import org.example.JsonParser;
import org.example.Model.CurrentProduserData;
import org.example.Model.DistributorData;
import org.example.Model.ProducerData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendPrice extends Behaviour {

    private boolean isEnd = false;
    private ProducerData producerData;
    private CurrentProduserData currentProduserData;
    private AID topic;

    private MessageTemplate mt;

    public SendPrice(AID topic, ProducerData producerData, CurrentProduserData currentProduserData) {
        this.topic = topic;
        this.producerData = producerData;
        this.currentProduserData = currentProduserData;
        log.debug("reg topic");
        mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                MessageTemplate.and(
                    MessageTemplate.MatchTopic(topic),
                    MessageTemplate.MatchProtocol("time, power")));

    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            log.debug("get msg {} from {}", msg.getContent(), msg.getSender().getLocalName());
            DistributorData distributorData = JsonParser.parseData(msg.getContent(), DistributorData.class);
            producerData.updateData(distributorData.getTime());

            if (producerData.getPower() >= 0.001) {

                currentProduserData.updateData(producerData.getMinPrice());
                log.info("min price: {}; start price: {}", currentProduserData.getMinPrice(),
                        currentProduserData.getCurrentPrice());

                ACLMessage m = new ACLMessage(ACLMessage.INFORM);

                m.setContent(String.valueOf(currentProduserData.getCurrentPrice()));
                m.addReceiver(topic);
                m.setProtocol("price");
                myAgent.send(m);
                log.info("{} participates in the auction in topic {}", myAgent.getLocalName(), topic.getLocalName());
                log.debug("send msg {} to {}", m.getContent(), topic.getLocalName());
            } else {
                currentProduserData.setAuction(false);
                log.info("{} does not participate in the auction in topic {}", myAgent.getLocalName(), topic.getLocalName());
            }
            isEnd = true;

        } else {
            block();
        }

    }

    @Override
    public int onEnd() {
        return 1;
    }

    @Override
    public boolean done() {
        return isEnd;
    }

}
