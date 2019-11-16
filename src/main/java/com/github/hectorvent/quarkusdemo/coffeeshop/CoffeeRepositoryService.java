package com.github.hectorvent.quarkusdemo.coffeeshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.jboss.logging.Logger;

/**
 *
 * @author Héctor Ventura <hectorvent@gmail.com>
 */
@ApplicationScoped
public class CoffeeRepositoryService {

    private static final Logger LOGGER = Logger.getLogger(CoffeeRepositoryService.class);

    private static final Map<Integer, Coffee> COFFEE_LIST = new HashMap<>();
    private static final Map<Integer, Integer> AVAILABILITY = new HashMap<>();
    private static final AtomicLong COUNTER = new AtomicLong(0);

    public CoffeeRepositoryService() {

        COFFEE_LIST.put(1, new Coffee(1, "Santo Domingo", "Rep. Dominicana", 23));
        COFFEE_LIST.put(2, new Coffee(2, "Don Valdez", "Colombia", 18));
        COFFEE_LIST.put(3, new Coffee(3, "Cafe Puro", "Rep. Dominicana", 25));
        COFFEE_LIST.put(4, new Coffee(4, "Illy", "Italia", 55));
        COFFEE_LIST.put(5, new Coffee(5, "LavAzza Qualita", "Italia", 25));

        AVAILABILITY.put(1, 20);
        AVAILABILITY.put(2, 7);
        AVAILABILITY.put(3, 40);
        AVAILABILITY.put(4, 15);
        AVAILABILITY.put(5, 19);
    }

    public List<Coffee> getAllCoffees() {
        return new ArrayList(COFFEE_LIST.values());
    }

    public Coffee getCoffeeById(Integer id) {
        return COFFEE_LIST.get(id);
    }

    public List<Coffee> getRecommendations(Integer id) {
        if (id == null) {
            return Collections.emptyList();
        }
        return COFFEE_LIST.values().stream()
                .filter(coffee -> !id.equals(coffee.getId()))
                .limit(2)
                .collect(Collectors.toList());
    }

    // 2 of las 4 request fail CB will be open for 5 secons
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000L)
    public Integer getAvailability(Coffee coffee) {
        maybeFail();
        return AVAILABILITY.get(coffee.getId());
    }

    private void maybeFail() {
        final Long invocationNumber = COUNTER.getAndIncrement();
        if (invocationNumber % 4 > 1) { // alternate 2 successful and 2 failing invocations
            LOGGER.errorf("Invocation #%d failing", invocationNumber);
            throw new RuntimeException("Service failed.");
        }
        LOGGER.infof("Invocation #%d OK", invocationNumber);
    }
}
