package org.example.ProducerBehaviour;

import org.example.Model.CurrentProduserData;
import org.example.Model.ProducerData;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;

public class ProducerFSM extends FSMBehaviour {
    private static final String SEND_PRICE="send_price", OPEN_AUCTION="open_auction", CONFIRM = "confirm";

    public ProducerFSM(AID topic, ProducerData producerData) {
        CurrentProduserData currentProduserData = new CurrentProduserData();

        registerFirstState(new SendPrice(topic, producerData, currentProduserData), SEND_PRICE);
        registerState(new Auction(topic, currentProduserData), OPEN_AUCTION);
        registerLastState(new ResultAuction(topic, producerData, currentProduserData), CONFIRM);

        registerDefaultTransition(SEND_PRICE, OPEN_AUCTION);
        registerDefaultTransition(OPEN_AUCTION, CONFIRM);

    }

}
