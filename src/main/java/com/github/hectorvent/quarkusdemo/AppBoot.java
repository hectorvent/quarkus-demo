package com.github.hectorvent.quarkusdemo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 *
 * @author Héctor Ventura <hectorvent@gmail.com>
 */
@ApplicationScoped
public class AppBoot {

    private static final Logger LOGGER = LoggerFactory.getLogger("AppBoot");

    public void onStart(@Observes StartupEvent event) {
        LOGGER.info("Started up .......");
    }

    public void onStop(@Observes ShutdownEvent event) {
        LOGGER.info("Stoped .......");
    }

}
