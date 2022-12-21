package org.example.cfg;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")
@Getter
@Setter
public class ProducerCFG {

    @XmlElement
    private String type;
    @XmlElement
    private double A;
    @XmlElement
    private double B1;
    @XmlElement
    private double B2;
    @XmlElement
    private double C0;
    @XmlElement
    private double C1;
    @XmlElement
    private double C2;
    @XmlElement
    private double C3;

    public double getPower(int time){
        double power = 0;
        switch (type) {
            case "SPS" -> {
                if (time <= 5 || time >= 19) {
                    power = 0;
                } else {
                    power = C0 + C1 * time + C2 * Math.pow(time, 2) + C3 * Math.pow(time, 3);
                }
            }
            case "TPS" -> {
                power = A;
            }
            case "WPS" -> {
                for (int i = (time - 1) * 1000; i < time * 1000; i++) {
                    power = power + 1/(B2 * Math.sqrt(2 * 3.14)) *
                            Math.exp(- Math.pow(i/1000 - B1, 2) / (2 * Math.pow(B2, 2))) / 3;
                }
            }
        }

        if (power < 0) {
            return 0;
        }

        return power;
    }

}
