package org.example.DistributerBehaviour;

import org.example.Model.TopicData;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectBitsParallel extends ParallelBehaviour {

    private TopicData topicData;

    public CollectBitsParallel(TopicData topicData) {
        super(ParallelBehaviour.WHEN_ANY);
        this.topicData = topicData;
    }

    @Override
    public void onStart() {
        addSubBehaviour(new CollectBits(topicData));
        addSubBehaviour(new AcceptRequestPrice(topicData));

    }

    @Override
    public int onEnd() {
        for (AID producer: topicData.getBitsData().keySet()) {
            log.info("prod {}, price {}", producer.getLocalName(), topicData.getBitsData().get(producer));
        }

        return 1;
    }
}
