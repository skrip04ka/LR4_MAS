package org.example.ProducerBehaviour;

import org.example.Model.CurrentProduserData;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;

public class Auction extends ParallelBehaviour {

    private AID topic;

    private CurrentProduserData currentProduserData;

    public Auction(AID topic, CurrentProduserData currentProduserData) {
        super(ParallelBehaviour.WHEN_ANY);
        this.topic = topic;
        this.currentProduserData = currentProduserData;
    }

    public void onStart() {
        addSubBehaviour(new ReceiveAnswers(topic, currentProduserData));
        addSubBehaviour(new StopAuction(topic));
    }
}
