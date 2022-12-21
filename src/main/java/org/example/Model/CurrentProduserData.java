package org.example.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CurrentProduserData {

    private final double priceDown = 1;
    private double currentPrice;
    private double minPrice;

    @Setter
    private boolean auction = true;

    public void updateData(double minPrice) {
        this.minPrice = minPrice;
        this.currentPrice = 2 * this.minPrice;
        this.auction = true;
    }

    public void changePrice(double otherPrice) {
        if (otherPrice - priceDown > minPrice) {
            currentPrice = otherPrice - priceDown;
        } else {
            currentPrice = minPrice;
            auction = false;
        }
//        } else {
//            currentPrice = minPrice;
//            auction = false;
//        }
    }
}
