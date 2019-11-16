package com.github.hectorvent.quarkusdemo.coffeeshop;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 *
 * @author Héctor Ventura <hectorvent@gmail.com>
 */
@Path("/coffee")
@Produces(MediaType.APPLICATION_JSON)
public class CoffeeResource {

    private static final Logger LOGGER = Logger.getLogger(CoffeeResource.class);

    @Inject
    CoffeeRepositoryService coffeeRepository;

    private static final AtomicLong COUNTER = new AtomicLong(0);
    private static final Float FAIL_RATIO = 0.5f;

    @GET
    @Retry(maxRetries = 4, retryOn = RuntimeException.class)
    public List<Coffee> coffees() {
        final Long invocationNumber = COUNTER.getAndIncrement();

        maybeFail(String.format("CoffeeResource#coffees() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#coffees() invocation #%d returning successfully", invocationNumber);
        return coffeeRepository.getAllCoffees();
    }

    @GET
    @Path("/{id}")
    public Response coffeeDetail(@PathParam int id) {
        final Long invocationNumber = COUNTER.getAndIncrement();

        maybeFail(String.format("CoffeeResource#coffees() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#coffees() invocation #%d returning successfully", invocationNumber);
        Coffee coffee = coffeeRepository.getCoffeeById(id);

        // if coffee with given id not found, return 404
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(coffee).build();
    }

    @GET
    @Path("/{id}/availability")
    public Response availability(@PathParam int id) {
        final Long invocationNumber = COUNTER.getAndIncrement();

        Coffee coffee = coffeeRepository.getCoffeeById(id);

        // check that coffee with given id exists, return 404 if not
        if (coffee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Integer availability = coffeeRepository.getAvailability(coffee);
            LOGGER.infof("CoffeeResource#availability() invocation #%d returning successfully", invocationNumber);
            return Response.ok(availability).build();
        } catch (RuntimeException e) {
            String message = e.getClass().getSimpleName() + ": " + e.getMessage();
            LOGGER.errorf("CoffeeResource#availability() invocation #%d failed: %s", invocationNumber, message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(message)
                    .build();
        }
    }

    @GET
    @Path("/{id}/recommendations")
    @Timeout(250)
    @Fallback(fallbackMethod = "fallbackRecommendations")
    public List<Coffee> recommendations(@PathParam int id) {
        long started = System.currentTimeMillis();
        final long invocationNumber = COUNTER.getAndIncrement();

        try {
            randomDelay();
            LOGGER.infof("CoffeeResource#recommendations() invocation #%d returning successfully", invocationNumber);
            return coffeeRepository.getRecommendations(id);
        } catch (InterruptedException e) {
            LOGGER.errorf("CoffeeResource#recommendations() invocation #%d timed out after %d ms",
                    invocationNumber, System.currentTimeMillis() - started);
            return null;
        }
    }

    /**
     * A fallback method for recommendations.
     *
     * @param id
     * @return List<Coffee>
     */
    public List<Coffee> fallbackRecommendations(int id) {
        LOGGER.info("Falling back to RecommendationResource#fallbackRecommendations()");
        return Collections.singletonList(coffeeRepository.getCoffeeById(1));
    }

    private void maybeFail(String failureLogMessage) {
        if (new Random().nextFloat() < FAIL_RATIO) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }

    private void randomDelay() throws InterruptedException {
        Thread.sleep(new Random().nextInt(500));
    }
}
