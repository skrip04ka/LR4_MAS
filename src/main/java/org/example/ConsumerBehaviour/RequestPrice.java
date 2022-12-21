package org.example.ConsumerBehaviour;

import org.example.DfHelper;
import org.example.JsonParser;
import org.example.Model.WinnerProducerData;
import org.example.cfg.ConsumerCFG;
import org.example.time.TimeClass;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RequestPrice extends Behaviour {

    private ConsumerCFG cfg;
    private WinnerProducerData winner;

    private boolean isEnd = false;

    private MessageTemplate mt = MessageTemplate.and(MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.REFUSE)),
                MessageTemplate.MatchProtocol("result price"));

    private int time;

    public RequestPrice(ConsumerCFG cfg, int time) {
        this.cfg = cfg;
        this.time = time;
    }

    @Override
    public void onStart() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("request for a result");
        msg.setProtocol("result price");

        List<AID> distributors = DfHelper.findAgents(myAgent, "distributor:" + myAgent.getLocalName());
        if (distributors.isEmpty()) {
            throw new RuntimeException("no distributor");
        }

        for (AID distributor : distributors) {
            msg.addReceiver(distributor);
        }

        myAgent.send(msg);
        log.debug("send {} to {} distributors", msg.getContent(), distributors.size());

    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {

            if (msg.getPerformative() == ACLMessage.AGREE) {
                log.debug("get winner msg {}", msg.getContent());
                winner = JsonParser.parseData(msg.getContent(), WinnerProducerData.class);
            }

            myAgent.addBehaviour(new WakerBehaviour(myAgent, TimeClass.getRestOfTime() - 100) {
                @Override
                protected void onWake() {
                    if(winner == null) {
                        log.info("winner time: {}, no winner producer, req power: {}",
                                time, cfg.getPower(time));
                    } else {
                        double totalPrice = 0;
                        double totalPower = 0;
                        String prod = "";
                        String price = "";
                        String power = "";
                        for (String winProdName: winner.getWinner().keySet()) {
                            AID winProd = new AID(winProdName, true);
                            prod = prod + winProd.getLocalName() + " ";
                            price = price +  String.format("%.3f", winner.getWinner().get(winProdName).get(0)) + " ";
                            power = power + String.format("%.3f", winner.getWinner().get(winProdName).get(1)) + " ";

                            totalPrice = totalPrice + winner.getWinner().get(winProdName).get(0) * winner.getWinner().get(winProdName).get(1);
                            totalPower = totalPower + winner.getWinner().get(winProdName).get(1);
                        }
                        log.info("winner time: {}, prod: {}; price: {} // {}; power: {} // {}",
                                time, prod, price,
                                String.format("%.3f", totalPrice), power, String.format("%.3f", totalPower ));
                    }
                }
            });

            myAgent.addBehaviour(new WakerBehaviour(myAgent, TimeClass.getRestOfTime() + 100) {
                @Override
                protected void onWake() {
                    myAgent.addBehaviour(new SendRequest(cfg));
                    log.debug("change time");
                }
            });


            isEnd = true;
        } else {
            block();
        }

    }

    @Override
    public int onEnd() {

        if (winner == null) {
            return 0;
        } else {
            return winner.getWinner().size();
        }
    }

    @Override
    public boolean done() {
        return isEnd;
    }



}
