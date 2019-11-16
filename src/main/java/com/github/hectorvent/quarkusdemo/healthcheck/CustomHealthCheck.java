package com.github.hectorvent.quarkusdemo.healthcheck;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 *
 * @author Héctor Ventura <hectorvent@gmail.com>
 */
@Readiness
@ApplicationScoped
public class CustomHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("With custome data")
                .up()
                .withData("connectedClients", 3678)
                .build();
    }
}
