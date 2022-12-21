package org.example.DistributerBehaviour;

import org.example.Model.ConsumerData;
import org.example.Model.TopicData;
import org.example.Model.WinnerProducerData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfirmPrice extends Behaviour {

    private TopicData topicData;
    private ConsumerData consumerData;
    private WinnerProducerData winner;
    private boolean isEnd = false;
    private List<AID> sortProducer;

    private MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.or(
                            MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                            MessageTemplate.or(
                                    MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
                                    MessageTemplate.MatchPerformative(ACLMessage.INFORM)))),
            MessageTemplate.MatchProtocol("confirm power"));


    public ConfirmPrice(TopicData topicData, ConsumerData consumerData,
                        WinnerProducerData winner) {
        this.topicData = topicData;
        this.consumerData = consumerData;
        this.winner = winner;
    }

    @Override
    public void onStart() {
        sortProducer = sort(topicData.getBitsData());

        if (!topicData.getBitsData().isEmpty() && topicData.getBitsData().get(sortProducer.get(0)) * consumerData.getPower()
                <= consumerData.getMaxPrice()) {

            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
            m.setProtocol("confirm power");
            m.addReceiver(sortProducer.get(0));
            m.setContent(consumerData.getPower() + "");
            myAgent.send(m);
            log.info("send confirm to {}", sortProducer.get(0).getLocalName());
            winner.addWinner(sortProducer.get(0),
                    topicData.getBitsData().get(sortProducer.get(0)),
                    consumerData.getPower() );
        } else {
            if (topicData.getBitsData().isEmpty()) {
                log.warn("no producer");
                myAgent.addBehaviour(new SendAgreePrice(winner));
                isEnd = true;
            } else {
                winner.removeAll();
                log.info("no money: min price {} // {} > max price {}",
                        topicData.getBitsData().get(sortProducer.get(0)),
                        topicData.getBitsData().get(sortProducer.get(0)) * consumerData.getPower(),
                        consumerData.getMaxPrice());

                ACLMessage looseMessage = new ACLMessage(ACLMessage.CANCEL);
                looseMessage.setProtocol("confirm power");
                looseMessage.setContent("loose, no money");

                for (AID producer : topicData.getBitsData().keySet()) {
                    looseMessage.addReceiver(producer);
                }
                myAgent.send(looseMessage);
                isEnd = true;
                myAgent.addBehaviour(new SendAgreePrice(winner));
            }
        }
    }

    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(mt);
        if (msg != null) {
            log.debug("get {} from {}", msg.getContent(), msg.getSender().getLocalName());
            switch (msg.getPerformative()) {
                case ACLMessage.AGREE -> {
                    log.info("prod {} accept", msg.getSender().getLocalName());
                    ACLMessage winnerMessage = new ACLMessage(ACLMessage.CONFIRM);
                    winnerMessage.setProtocol("confirm power");
                    winnerMessage.addReceiver(msg.getSender());
                    winnerMessage.setContent(consumerData.getPower() + "");
                    myAgent.send(winnerMessage);

                    winner.addWinner(msg.getSender(),
                            topicData.getBitsData().get(msg.getSender()),
                            consumerData.getPower());

                    ACLMessage looseMessage = new ACLMessage(ACLMessage.CANCEL);
                    looseMessage.setProtocol("confirm power");
                    looseMessage.setContent("loose, you not winner");

                    for (AID producer: topicData.getBitsData().keySet()) {
                        if(!producer.equals(msg.getSender())) {
                            looseMessage.clearAllReceiver();
                            looseMessage.addReceiver(producer);
                            myAgent.send(looseMessage);
                        }
                    }

                    isEnd = true;
                    myAgent.addBehaviour(new SendAgreePrice(winner));
                }

                case ACLMessage.INFORM -> {
                    log.info("dist wait {}", msg.getSender().getLocalName());
                    myAgent.addBehaviour(new WakerBehaviour(myAgent, 100) {
                        @Override
                        protected void onWake() {
                            myAgent.addBehaviour(new ConfirmPrice(topicData, consumerData, winner));
                        }
                    });
                    isEnd = true;
                }

                case ACLMessage.CANCEL -> {
                    log.info("prod {} no power", msg.getSender().getLocalName());
                    sortProducer.remove(msg.getSender());
                    topicData.getBitsData().remove(msg.getSender());
                    winner.removeAll();

                    ACLMessage looseMessage = msg.createReply();
                    looseMessage.setPerformative(ACLMessage.CANCEL);
                    looseMessage.setContent("loose, no power");
                    myAgent.send(looseMessage);

                    myAgent.addBehaviour(new ConfirmPrice(topicData, consumerData, winner));
                    isEnd = true;
                }

                case ACLMessage.REFUSE -> {
                    log.info("prod {} need split", msg.getSender().getLocalName());
                    winner.changePower(msg.getSender(), Double.parseDouble(msg.getContent()));
                    myAgent.addBehaviour(new SplitBehaviour(topicData,
                            consumerData, winner, sortProducer));
                    isEnd = true;
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
    public boolean done() {
        return isEnd;
    }

    private List<AID> sort(Map<AID, Double> producers) {
        List<AID> sortList = new ArrayList<>();

        for (AID producer: producers.keySet()) {
            if(sortList.isEmpty()) {
                sortList.add(producer);
            } else {
                int i = 0;
                while (i < sortList.size() && producers.get(sortList.get(i)) < producers.get(producer)) {
                    i++;
                }
                sortList.add(i, producer);

            }
        }

        return sortList;
    }

}
