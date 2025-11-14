package com.weather.monitoring.health;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * System-level health check for JVM and OS resources
 */
@Slf4j
public class SystemHealthCheck implements HealthCheck {

    private static final double MEMORY_THRESHOLD = 0.90; // 90% memory usage threshold
    private static final double DISK_THRESHOLD = 0.95;   // 95% disk usage threshold

    @Override
    public HealthStatus check() {
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> details = new HashMap<>();
            boolean healthy = true;
            StringBuilder issues = new StringBuilder();

            // Check memory usage
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            double memoryUsage = (double) usedMemory / maxMemory;

            details.put("memory_used_bytes", usedMemory);
            details.put("memory_max_bytes", maxMemory);
            details.put("memory_usage_ratio", memoryUsage);

            if (memoryUsage > MEMORY_THRESHOLD) {
                healthy = false;
                issues.append("High memory usage: ")
                      .append(String.format("%.1f%%", memoryUsage * 100))
                      .append("; ");
            }

            // Check disk space
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            long totalSpace = tempDir.getTotalSpace();
            long freeSpace = tempDir.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            double diskUsage = (double) usedSpace / totalSpace;

            details.put("disk_total_bytes", totalSpace);
            details.put("disk_free_bytes", freeSpace);
            details.put("disk_usage_ratio", diskUsage);

            if (diskUsage > DISK_THRESHOLD) {
                healthy = false;
                issues.append("High disk usage: ")
                      .append(String.format("%.1f%%", diskUsage * 100))
                      .append("; ");
            }

            // Check thread count
            int threadCount = Thread.activeCount();
            details.put("thread_count", threadCount);

            // Warn if too many threads (possible leak)
            if (threadCount > 1000) {
                issues.append("High thread count: ").append(threadCount).append("; ");
                if (threadCount > 2000) {
                    healthy = false;
                }
            }

            // Check system load (if available)
            try {
                double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
                if (systemLoad >= 0) { // -1 means not available
                    details.put("system_load", systemLoad);

                    int processors = Runtime.getRuntime().availableProcessors();
                    details.put("available_processors", processors);

                    if (systemLoad > processors * 2) {
                        issues.append("High system load: ").append(systemLoad).append("; ");
                    }
                }
            } catch (Exception e) {
                log.debug("Could not get system load", e);
            }

            long responseTime = System.currentTimeMillis() - startTime;
            details.put("response_time_ms", responseTime);

            HealthStatus status;
            if (healthy) {
                status = HealthStatus.up("system");
            } else {
                status = HealthStatus.down("system", issues.toString().trim());
            }

            status.setResponseTimeMs(responseTime);
            status.setDetails(details);

            return status;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Object> details = new HashMap<>();
            details.put("error", e.getMessage());
            details.put("response_time_ms", responseTime);

            HealthStatus status = HealthStatus.down("system", "System check failed: " + e.getMessage());
            status.setResponseTimeMs(responseTime);
            status.setDetails(details);

            log.error("System health check failed", e);
            return status;
        }
    }

    @Override
    public String getName() {
        return "system";
    }
}
