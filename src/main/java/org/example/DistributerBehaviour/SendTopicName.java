package org.example.DistributerBehaviour;

import org.example.DfHelper;
import org.example.Model.TopicData;
import org.example.TopicHelper;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class SendTopicName extends OneShotBehaviour {

    private TopicData data;
    private List<AID> producers;

    public SendTopicName(TopicData data) {
        this.data = data;
    }

    @Override
    public void onStart() {
        producers = DfHelper.findAgents(myAgent, "producer");
    }

    @Override
    public void action() {
        AID topic = TopicHelper.createTopic(myAgent, "topic:"+myAgent.getLocalName());
        data.setTopic(topic);

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("topicName");
        for (AID producer : producers) {
            msg.addReceiver(producer);
        }
        msg.setContent(topic.getLocalName());
        myAgent.send(msg);
    }
}
