package org.example.Model;


import jade.core.AID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerProducerData {

    private Map<String, List<Double>> winner = new HashMap<>();

    public void addWinner(AID producer, double price, double power) {
        List<Double> param = new ArrayList<>();
        param.add(price);
        param.add(power);
        winner.put(producer.getName(), param);
    }

    public void changePower(AID producer, double power) {
        String producerName = producer.getName();
        winner.get(producerName).set(1, power);
    }

    public void removeAll() {
        winner = new HashMap<>();
    }

    public double powerByAid(AID producer) {
        String producerName = producer.getName();
        return winner.get(producerName).get(1);
    }

    public boolean checkWinner(AID producer) {
        if (winner.isEmpty()) {
            return false;
        }
        String producerName = producer.getName();
        return winner.containsKey(producerName);
    }

    @JsonIgnore
    public double getResultPrice() {
        if (winner.isEmpty()) {
            throw new NullPointerException("winner is empty");
        }
        double resultPrice = 0;
        for (String prod: winner.keySet()) {
            resultPrice = resultPrice + winner.get(prod).get(0) * winner.get(prod).get(1);
        }

        return resultPrice;
    }

}
