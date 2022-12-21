package org.example.Model;

import org.example.cfg.ProducerCFG;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ProducerData {

    private final ProducerCFG dataCFG;

    private double power;
    private double minPrice;
    private String lockKey = new String();

    public ProducerData(ProducerCFG dataCFG) {
        this.dataCFG = dataCFG;
    }

    public void updateData(int time) {
        this.power = dataCFG.getPower(time);
        this.minPrice = 1000/power + 100;
    }

    public void changePower(double reservedPower) {
        power = power - reservedPower;
    }

    public boolean lock(String lockId) {
        if (lockKey.isEmpty() || lockKey.equals(lockId)) {
            lockKey = lockId;
            log.debug("locked, use lockId {}", lockId);
            return false;
        } else {
            log.debug("fail lock, use lockId {}, correct lockKey {}", lockId, lockKey);
            return true;
        }
    }

    public void unLock(String lockId) {
        if (lockKey.isEmpty() || lockKey.equals(lockId)) {
            log.debug("unlocked, use lockId {}", lockId);
            lockKey = new String();
        }
    }

}
