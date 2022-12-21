package org.example.DistributerBehaviour;

import org.example.Model.ConsumerData;
import org.example.Model.TopicData;
import org.example.Model.WinnerProducerData;
import jade.core.behaviours.FSMBehaviour;

public class DistributerFSM extends FSMBehaviour {
    private static final String SEND_TOPIC="send_topic",
            SEND_DATA="send_data",
            COLLECT="collect",
            ACCEPT="accept",
            CONFIRM_PRICE = "confirm_price";

    public DistributerFSM(ConsumerData consumerData) {
        TopicData topicData = new TopicData();
        WinnerProducerData winner = new WinnerProducerData();

        registerFirstState(new SendTopicName(topicData), SEND_TOPIC);
        registerState(new SendFirstData(myAgent, 500, topicData, consumerData), SEND_DATA);
        registerState(new CollectBitsParallel(topicData), COLLECT);
        registerLastState(new ConfirmPrice(topicData, consumerData, winner), CONFIRM_PRICE);
//        registerLastState(new SendAgreePrice(topicData, winner), ACCEPT);

        registerDefaultTransition(SEND_TOPIC, SEND_DATA);
        registerDefaultTransition(SEND_DATA, COLLECT);
        registerDefaultTransition(COLLECT, CONFIRM_PRICE);
//        registerDefaultTransition(CONFIRM_PRICE, ACCEPT);
    }
}
