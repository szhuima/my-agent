package dev.szhuima.agent.domain.support.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class DynamicTaskService {

    private ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setThreadNamePrefix("dynamic-task-");
        scheduler.initialize();
    }


    // 启动任务
    public void startTask(String taskId, String cronExpression, Runnable task) {
        if (tasks.containsKey(taskId)) {
            log.info("任务 {} 已存在，先停止再启动。", taskId);
            stopTask(taskId);
        }

        ScheduledFuture<?> future = scheduler.schedule(task, new CronTrigger(cronExpression));

        tasks.put(taskId, future);
        System.out.println("任务 " + taskId + " 已启动，表达式：" + cronExpression);
    }

    // 停止任务
    public void stopTask(String taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
            System.out.println("任务 " + taskId + " 已停止。");
        }
    }

    // 修改任务
    public void updateTask(String taskId, String newCron, Runnable task) {
        stopTask(taskId);
        startTask(taskId, newCron, task);
        System.out.println("任务 " + taskId + " 已更新为新表达式：" + newCron);
    }

    // 停止所有任务
    public void stopAll() {
        tasks.forEach((id, f) -> f.cancel(true));
        tasks.clear();
        System.out.println("所有任务已停止。");
    }
}
