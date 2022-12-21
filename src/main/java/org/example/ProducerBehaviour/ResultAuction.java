package org.example.ProducerBehaviour;

import org.example.Model.CurrentProduserData;
import org.example.Model.ProducerData;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultAuction extends Behaviour {


    private boolean isEnd = false;

    private ProducerData producerData;

    private String lockId;

    private MessageTemplate mt;

    public ResultAuction(AID topic, ProducerData producerData, CurrentProduserData currentProduserData) {
        this.producerData = producerData;
        mt = MessageTemplate.and(
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                                MessageTemplate.MatchPerformative(ACLMessage.CANCEL))),
                MessageTemplate.and(
                        MessageTemplate.MatchProtocol("confirm power"),
                        MessageTemplate.MatchSender(new AID(
                                topic.getLocalName().split(":")[1], false))));
        this.lockId = topic.getName();
        if (!currentProduserData.isAuction()) {
            isEnd = true;
            log.info("no auction");
        }

    }


    @Override
    public void action() {
        ACLMessage msg = getAgent().receive(mt);
        if (msg != null) {
            log.debug("get {} from {}", msg.getContent(), msg.getSender().getLocalName());

            switch (msg.getPerformative()) {
                case ACLMessage.REQUEST -> {
                    if (producerData.lock(lockId)) {
                        ACLMessage m = msg.createReply();
                        m.setPerformative(ACLMessage.INFORM);
                        m.setContent("wait");
                        myAgent.send(m);
                        log.info("send wait to {}", msg.getSender().getLocalName());
                    } else if (producerData.getPower() < 0.001) {
                        ACLMessage m = msg.createReply();
                        m.setPerformative(ACLMessage.CANCEL);
                        m.setContent("no power");
                        myAgent.send(m);
                        log.info("send no power to {}", msg.getSender().getLocalName());
                    } else if (producerData.getPower() < Double.parseDouble(msg.getContent())) {
                        ACLMessage m = msg.createReply();
                        m.setPerformative(ACLMessage.REFUSE);
                        m.setContent(producerData.getPower() + "");
                        myAgent.send(m);
                        log.info("send need split to {}, max power {}, req power {}", msg.getSender().getLocalName(),
                                producerData.getPower(), msg.getContent());
                    } else {
                        ACLMessage m = msg.createReply();
                        m.setPerformative(ACLMessage.AGREE);
                        m.setContent("ok");
                        myAgent.send(m);
                        log.info("send ok to {}", msg.getSender().getLocalName());
                    }
                }

                case ACLMessage.CONFIRM -> {
                    producerData.changePower(Double.parseDouble(msg.getContent()));
                    isEnd = true;
                    log.info("winner auction in topic:{}", msg.getSender().getLocalName());
                }

                case ACLMessage.CANCEL -> {
                    isEnd = true;
                    log.info("leaving auction in topic:{}", msg.getSender().getLocalName());
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
        log.info("left {} power", producerData.getPower());
        producerData.unLock(lockId);

        return 1;
    }

    @Override
    public boolean done() {
        return isEnd;
    }
}
