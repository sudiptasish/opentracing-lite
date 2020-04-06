/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class VertxMain {
    
    private static final Logger logger = LoggerFactory.getLogger(VertxMain.class);
    
    private static final Integer EVENT_LOOP_SIZE = 4;
    private static final Integer WORKER_POOL_SIZE = 4;
    private static final Integer BLOCKING_POOL_SIZE = 2;
    private static final Long BLOCK_THREAD_CHECK_INNTERVAL = 2 * 60 * 1000L;
    
    private static final String APP_EMP = "EMP";
    private static final String APP_DEPT = "DEPT";

    private Vertx vertx;
    private Handler deployHandler;

    public static void main(String[] args) {
        if (Boolean.getBoolean("pause")) {
            pause(Long.parseLong(System.getProperty("pauseTime", "15000")));
        }
        VertxMain main = new VertxMain();
        main.createVertx();
        
        if (APP_EMP.equals(System.getProperty("app-name"))) {
            main.deploy(APP_EMP, EmployeeVerticle.class.getName());
        }
        else if (APP_DEPT.equals(System.getProperty("app-name"))) {
            main.deploy(APP_DEPT, DeptVerticle.class.getName());
            main.deploy("prc", ProcessorVerticle.class.getName());
        }
        else {
            logger.error("Invalid system property app-name: " + System.getProperty("app-name") + ". Exiting ...");
            main.stop();
        }
    }
    
    protected void createVertx() {
        MetricsOptions metricsOptions = new DropwizardMetricsOptions()
                .setEnabled(Boolean.TRUE)
                .setJmxEnabled(Boolean.TRUE);

        VertxOptions options = new VertxOptions()
                .setMetricsOptions(metricsOptions)
                .setEventBusOptions(new EventBusOptions().setLogActivity(Boolean.TRUE))
                .setEventLoopPoolSize(EVENT_LOOP_SIZE)
                .setWorkerPoolSize(WORKER_POOL_SIZE)
                .setInternalBlockingPoolSize(BLOCKING_POOL_SIZE)
                .setBlockedThreadCheckInterval(BLOCK_THREAD_CHECK_INNTERVAL)
                .setBlockedThreadCheckIntervalUnit(TimeUnit.MILLISECONDS);

        vertx = Vertx.vertx(options);
        deployHandler = new DeployHandler(vertx);

        logger.info("Created Vert.x Instance. Classloader: {} Event Loop size: {}. "
                + "Worker Pool size: {}. Internal Blocking Pool Size: {}"
                , getClass().getClassLoader()
                , EVENT_LOOP_SIZE
                , WORKER_POOL_SIZE
                , BLOCKING_POOL_SIZE);
    }
    
    protected void stop() {
        vertx.close();
    }
    
    protected void deploy(String verticleName, String verticleClass) {
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(future -> {
            JsonObject result = future.result();
            JsonObject verticleConfig = result.getJsonObject(verticleName.toLowerCase());
            
            DeploymentOptions deployOptions = new DeploymentOptions();
            // Deployment options being set.
            // 1. Not a worker verticle.
            // 2. Not in HA (High Availability) Mode.
            // 3. Number of instances will be 1.
            deployOptions.setConfig(verticleConfig)
                    .setWorker(verticleConfig.getBoolean("worker.mode", Boolean.FALSE))
                    .setHa(verticleConfig.getBoolean("ha.mode", Boolean.FALSE))
                    .setInstances(verticleConfig.getInteger("instance.count", 1));

            vertx.deployVerticle(verticleClass, deployOptions, deployHandler);
        });
    }
    
    private static void pause(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException e) {}
    }
}
