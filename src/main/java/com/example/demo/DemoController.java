package com.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pvtyuan on 2020/6/17.
 */
@RestController
public class DemoController {

    // 需设置service_name和ip这两个label
    private final MeterRegistry meterRegistry;
    private Counter counter;
    private DistributionSummary distributionSummary;
    private AtomicInteger unsavedRequestCount = new AtomicInteger();
    private Random random;

    public DemoController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.counter = Counter.builder("requests").register(meterRegistry);
        Gauge.builder("unsaved_requests", unsavedRequestCount, AtomicInteger::get).register(meterRegistry);
        // 这里的70，80，90是bucket的分界线
        this.distributionSummary = DistributionSummary.builder("response_time").baseUnit("ms").serviceLevelObjectives(70, 80, 90).register(meterRegistry);
        this.random = new Random();
    }

    @GetMapping("/counter")
    public String counter() {
        this.counter.increment();
        return "ok";
    }

    @GetMapping("/gauge")
    public String gauge(@RequestParam("value") int value) {
        this.unsavedRequestCount.set(value);
        return "ok";
    }

    @GetMapping("/histogram")
    public String histogram() {
        this.distributionSummary.record(random.nextInt(100));
        return "ok";
    }
}
