package com.benorim.ridepally;

import org.springframework.boot.SpringApplication;

public class TestRidepallyApplication {

    public static void main(String[] args) {
        SpringApplication.from(RidepallyApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
