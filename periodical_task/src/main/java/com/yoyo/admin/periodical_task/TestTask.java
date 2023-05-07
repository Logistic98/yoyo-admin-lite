package com.yoyo.admin.periodical_task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 定时任务示例
 */
@Slf4j
@Component
@Async
@EnableScheduling
@ConditionalOnProperty(prefix = "settings.task", name = "enabled", havingValue = "true") // 配置文件读取是否开启定时任务（与havingValue值相同才会开启）
public class TestTask {

    /**
     * 单线程测试定时任务
     */
    @Scheduled(cron = "* * * * * ?")
    public void testTask(){
        log.info("{}， 测试定时任务执行时间为{}", Thread.currentThread().getName(), new Date());
    }

    /**
     * 多线程测试定时任务
     */
    @Scheduled(fixedDelay = 99999999)
    public void testMultithreadTask()  {

        // 开始时间
        Long start = System.currentTimeMillis();
        // 任务提交顺序
        List<String> list = new ArrayList<>();
        // 定长20线程池
        ExecutorService exs = Executors.newFixedThreadPool(20);
        // 待执行的任务列表
        final List<Integer> taskList = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
        try {
            // 全流式处理转换成CompletableFuture[]+组装成一个无返回值CompletableFuture，join等待执行完毕。返回结果whenComplete获取
            CompletableFuture[] cfs = taskList.stream().map(object-> CompletableFuture.supplyAsync(()->calc(object), exs)
                    .thenApply(h->Integer.toString(h))
                    //如需获取任务完成先后顺序，修改此处代码即可
                    .whenComplete((v, e) -> {
                        log.info("{}， 测试多线程定时任务执行时间为{}， 正在执行第{}个子任务", Thread.currentThread().getName(), new Date(), v);
                        log.info("任务"+v+"完成!result="+v+"，异常 e="+e+","+new Date());
                        list.add(v);
                    })).toArray(CompletableFuture[]::new);
            // 等待总任务完成，但是封装后无返回值，必须自己whenComplete()获取
            CompletableFuture.allOf(cfs).join();
            log.info("任务完成先后顺序为" + list + ",总耗时="+(System.currentTimeMillis()-start));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            exs.shutdown();
        }
    }

    /**
     * 组合多个CompletableFuture为一个CompletableFuture,所有子任务全部完成，组合后的任务才会完成。带返回值，可直接get.
     * @param futures List
     * @return
     */
    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        //1.构造一个空CompletableFuture，子任务数为入参任务list size
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        //2.流式（总任务完成后，每个子任务join取结果，后转换为list）
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /**
     * 线程执行耗时及异常统计
     * @param i
     * @return
     */
    public static Integer calc(Integer i){
        try {
            Thread.sleep(2000);
            log.info("Task线程："+Thread.currentThread().getName()+"任务i="+i+",完成！+"+new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

}
