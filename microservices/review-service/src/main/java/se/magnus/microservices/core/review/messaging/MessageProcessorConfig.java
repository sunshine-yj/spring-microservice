package se.magnus.microservices.core.review.messaging;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.event.Event;

@Configuration
public class MessageProcessorConfig {

    @Bean
    public Consumer<Event<Integer, Review>> messageProcessor(ReviewService service) {
        return event -> {
            switch (event.getEventType()) {
                case CREATE -> service.createReview(event.getData()).block();
                case DELETE -> service.deleteReviews(event.getKey()).block();
            }
        };
    }
}
