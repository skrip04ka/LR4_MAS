package org.example.time;

import lombok.Getter;

import java.util.Date;

public class TimeClass {
    static {
        startDate = new Date();
    }

    @Getter
    private static int time = 8000;
    private static int timeBeforeAuctionEnd = 5000;
    private static Date startDate;

    private TimeClass() {};

    public static int getCurrentTime() {
        int currentTime = (int) ((new Date().getTime() - startDate.getTime()) / time + 1);
        if (currentTime > 24) {
            startDate = new Date();
            currentTime = 1;
        }
        return currentTime;


    }

    public static long getRestOfTime() {
        return time - (new Date().getTime() - startDate.getTime() - (long) (getCurrentTime() - 1) * time);

    }

    public static long  getRestOfTimeAuction() {
        long restOfTimeAuction = getRestOfTime() - timeBeforeAuctionEnd;
        if (restOfTimeAuction > 0) {
            return restOfTimeAuction;
        } else {
            return 0;
        }
    }


    public static void resetTime() {
        startDate = new Date();
    }

}
