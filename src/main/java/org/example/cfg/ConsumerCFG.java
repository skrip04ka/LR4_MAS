package org.example.cfg;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")
@Getter
@Setter
public class ConsumerCFG {

    @XmlElement
    private List<Double> load = new ArrayList<>();
    @XmlElement
    private double Pnom;
    @XmlElement
    private double maxPrice;

    public double getPower(int time) {
        return load.get(time-1) * Pnom / 100;
    }

}
