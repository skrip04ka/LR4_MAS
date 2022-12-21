package org.example.Model;

import jade.core.AID;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TopicData {
    private AID topic;
    private Map<AID, Double> bitsData = new HashMap<>();

}
