package org.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {

//        TimeClass.resetTime(true);
//
//      while (true) {
//          System.out.println(TimeClass.getRestOfTime() + " " + TimeClass.getRestOfTimeAuction());
//          Thread.sleep(100);
//      }

        String s = "topic:D3";
        System.out.println(s.split(":")[1]);

    }



}
