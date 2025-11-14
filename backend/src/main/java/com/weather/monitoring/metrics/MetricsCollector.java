package com.weather.monitoring.metrics;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Simple metrics collector compatible with Prometheus format
 */
@Slf4j
public class MetricsCollector {

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
    private final Map<String, Histogram> histograms = new ConcurrentHashMap<>();

    /**
     * Increments a counter metric
     */
    public void incrementCounter(String name, String... labels) {
        String key = buildKey(name, labels);
        counters.computeIfAbsent(key, k -> new Counter(name, labels)).increment();
    }

    /**
     * Increments a counter by a specific value
     */
    public void incrementCounter(String name, double value, String... labels) {
        String key = buildKey(name, labels);
        counters.computeIfAbsent(key, k -> new Counter(name, labels)).increment(value);
    }

    /**
     * Sets a gauge value
     */
    public void setGauge(String name, double value, String... labels) {
        String key = buildKey(name, labels);
        gauges.computeIfAbsent(key, k -> new Gauge(name, labels)).set(value);
    }

    /**
     * Records a value in a histogram
     */
    public void recordHistogram(String name, double value, String... labels) {
        String key = buildKey(name, labels);
        histograms.computeIfAbsent(key, k -> new Histogram(name, labels)).record(value);
    }

    /**
     * Records execution time
     */
    public void recordTimer(String name, long durationMs, String... labels) {
        recordHistogram(name + "_duration_ms", durationMs, labels);
        incrementCounter(name + "_total", labels);
    }

    /**
     * Gets all metrics in Prometheus format
     */
    public String getPrometheusMetrics() {
        StringBuilder sb = new StringBuilder();

        // Export counters
        for (Counter counter : counters.values()) {
            sb.append(counter.toPrometheusFormat()).append("\n");
        }

        // Export gauges
        for (Gauge gauge : gauges.values()) {
            sb.append(gauge.toPrometheusFormat()).append("\n");
        }

        // Export histograms
        for (Histogram histogram : histograms.values()) {
            sb.append(histogram.toPrometheusFormat()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Clears all metrics
     */
    public void clear() {
        counters.clear();
        gauges.clear();
        histograms.clear();
    }

    private String buildKey(String name, String... labels) {
        if (labels.length == 0) {
            return name;
        }

        StringBuilder sb = new StringBuilder(name);
        for (int i = 0; i < labels.length; i += 2) {
            if (i + 1 < labels.length) {
                sb.append("{").append(labels[i]).append("=").append(labels[i + 1]).append("}");
            }
        }
        return sb.toString();
    }

    /**
     * Counter metric implementation
     */
    private static class Counter {
        private final String name;
        private final String[] labels;
        private final DoubleAdder value = new DoubleAdder();

        Counter(String name, String... labels) {
            this.name = name;
            this.labels = labels;
        }

        void increment() {
            value.add(1.0);
        }

        void increment(double amount) {
            value.add(amount);
        }

        String toPrometheusFormat() {
            StringBuilder sb = new StringBuilder();
            sb.append("# TYPE ").append(name).append(" counter\n");
            sb.append(name);

            if (labels.length > 0) {
                sb.append("{");
                for (int i = 0; i < labels.length; i += 2) {
                    if (i > 0) sb.append(",");
                    if (i + 1 < labels.length) {
                        sb.append(labels[i]).append("=\"").append(labels[i + 1]).append("\"");
                    }
                }
                sb.append("}");
            }

            sb.append(" ").append(value.doubleValue());
            return sb.toString();
        }
    }

    /**
     * Gauge metric implementation
     */
    private static class Gauge {
        private final String name;
        private final String[] labels;
        private volatile double value = 0.0;

        Gauge(String name, String... labels) {
            this.name = name;
            this.labels = labels;
        }

        void set(double value) {
            this.value = value;
        }

        String toPrometheusFormat() {
            StringBuilder sb = new StringBuilder();
            sb.append("# TYPE ").append(name).append(" gauge\n");
            sb.append(name);

            if (labels.length > 0) {
                sb.append("{");
                for (int i = 0; i < labels.length; i += 2) {
                    if (i > 0) sb.append(",");
                    if (i + 1 < labels.length) {
                        sb.append(labels[i]).append("=\"").append(labels[i + 1]).append("\"");
                    }
                }
                sb.append("}");
            }

            sb.append(" ").append(value);
            return sb.toString();
        }
    }

    /**
     * Simple histogram implementation
     */
    private static class Histogram {
        private final String name;
        private final String[] labels;
        private final DoubleAdder sum = new DoubleAdder();
        private final AtomicLong count = new AtomicLong(0);
        private volatile double min = Double.MAX_VALUE;
        private volatile double max = Double.MIN_VALUE;

        Histogram(String name, String... labels) {
            this.name = name;
            this.labels = labels;
        }

        void record(double value) {
            sum.add(value);
            count.incrementAndGet();

            synchronized (this) {
                if (value < min) min = value;
                if (value > max) max = value;
            }
        }

        String toPrometheusFormat() {
            StringBuilder sb = new StringBuilder();
            long currentCount = count.get();
            double currentSum = sum.doubleValue();

            String labelStr = "";
            if (labels.length > 0) {
                StringBuilder labelSb = new StringBuilder("{");
                for (int i = 0; i < labels.length; i += 2) {
                    if (i > 0) labelSb.append(",");
                    if (i + 1 < labels.length) {
                        labelSb.append(labels[i]).append("=\"").append(labels[i + 1]).append("\"");
                    }
                }
                labelSb.append("}");
                labelStr = labelSb.toString();
            }

            sb.append("# TYPE ").append(name).append(" histogram\n");
            sb.append(name).append("_count").append(labelStr).append(" ").append(currentCount).append("\n");
            sb.append(name).append("_sum").append(labelStr).append(" ").append(currentSum).append("\n");

            if (currentCount > 0) {
                sb.append(name).append("_min").append(labelStr).append(" ").append(min).append("\n");
                sb.append(name).append("_max").append(labelStr).append(" ").append(max).append("\n");
                sb.append(name).append("_avg").append(labelStr).append(" ").append(currentSum / currentCount);
            }

            return sb.toString();
        }
    }
}
