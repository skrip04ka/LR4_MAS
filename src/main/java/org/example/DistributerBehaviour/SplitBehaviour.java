package org.example.DistributerBehaviour;

import org.example.Model.ConsumerData;
import org.example.Model.TopicData;
import org.example.Model.WinnerProducerData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class SplitBehaviour extends Behaviour {


    private TopicData topicData;
    private ConsumerData consumerData;
    private WinnerProducerData winner;
    private List<AID> sortProducer;

    private int count = 0;

    private MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.REFUSE)),
            MessageTemplate.MatchProtocol("confirm power"));

    public SplitBehaviour(TopicData topicData, ConsumerData consumerData,
                          WinnerProducerData winner, List<AID> sortProducer) {
        this.topicData = topicData;
        this.consumerData = consumerData;
        this.winner = winner;
        this.sortProducer = sortProducer;
    }

    @Override
    public void onStart() {
        if(topicData.getBitsData().size() >= 2) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(sortProducer.get(0));
            msg.setProtocol("confirm power");
            double power = winner.powerByAid(sortProducer.get(0));
            msg.setContent(power + "");
            myAgent.send(msg);

            log.info("send split contract to {}", sortProducer.get(0).getLocalName());

            msg.clearAllReceiver();
            msg.addReceiver(sortProducer.get(1));
            msg.setContent((consumerData.getPower() - power) + "");
            myAgent.send(msg);

            winner.addWinner(sortProducer.get(1), topicData.getBitsData().get(sortProducer.get(1)),
                    consumerData.getPower() - power);
            count = 0;
            log.info("send split contract to {}", sortProducer.get(1).getLocalName());

        } else {
            winner.removeAll();
            count = winner.getWinner().size() + 1;
            log.info("no split");
        }
    }

    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(mt);
        if (msg != null) {

            switch (msg.getPerformative()) {
                case ACLMessage.AGREE -> {
                    log.info("{} accept", msg.getSender().getLocalName());
                    count++;
                }

                case ACLMessage.REFUSE -> {
                    if (topicData.getBitsData().size() >= winner.getWinner().size() + 1) {
                        log.info("try next split");
                        double dPower = winner.powerByAid(msg.getSender()) - Double.parseDouble(msg.getContent());
                        winner.changePower(msg.getSender(), Double.parseDouble(msg.getContent()));

                        ACLMessage splitContractMessage = new ACLMessage(ACLMessage.REQUEST);
                        splitContractMessage.addReceiver(msg.getSender());
                        splitContractMessage.setProtocol("confirm power");
                        splitContractMessage.setContent(winner.powerByAid(msg.getSender()) + "");
                        myAgent.send(splitContractMessage);

                        splitContractMessage.clearAllReceiver();
                        AID threeProd = sortProducer.get(sortProducer.indexOf(msg.getSender()) + 1);
                        splitContractMessage.addReceiver(threeProd);
                        splitContractMessage.setContent(dPower + "");
                        myAgent.send(splitContractMessage);
                        winner.addWinner(threeProd, topicData.getBitsData().get(threeProd), dPower);
                    } else {
                        log.info("error split");
                        winner.removeAll();
                        count = winner.getWinner().size() + 1;
                    }
                }

                default -> {
                    throw new RuntimeException("error");
                }
            }
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        if (!winner.getWinner().isEmpty()) {
            if(winner.getResultPrice() > consumerData.getMaxPrice()) {
                log.info("no money: result price {} > max price {}",
                        winner.getResultPrice(), consumerData.getMaxPrice());
                winner.removeAll();
            } else {
                ACLMessage confirmMessage = new ACLMessage(ACLMessage.CONFIRM);
                confirmMessage.setProtocol("confirm power");

                for (String win : winner.getWinner().keySet()) {
                    confirmMessage.clearAllReceiver();
                    confirmMessage.addReceiver(new AID(win, true));
                    confirmMessage.setContent(winner.getWinner().get(win).get(1) + "");
                    myAgent.send(confirmMessage);
                }
            }
        }

        ACLMessage looseMessage = new ACLMessage(ACLMessage.CANCEL);
        looseMessage.setProtocol("confirm power");
        looseMessage.setContent("loose, error split");

        for (AID producer: topicData.getBitsData().keySet()) {
            if(!winner.checkWinner(producer)) {
                looseMessage.clearAllReceiver();
                looseMessage.addReceiver(producer);
                myAgent.send(looseMessage);
                log.debug("send {} to {}", looseMessage.getContent(), producer.getLocalName());
            }
        }

        myAgent.addBehaviour(new SendAgreePrice(winner));
        log.info("winner size {}", winner.getWinner().size());

        return 1;
    }

    @Override
    public boolean done() {
        return count >= winner.getWinner().size();
    }


}
