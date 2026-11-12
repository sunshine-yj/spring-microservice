package se.magnus.microservices.core.recommendation.messaging;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.event.Event;

@Configuration
public class MessageProcessorConfig {

    @Bean
    public Consumer<Event<Integer, Recommendation>> messageProcessor(RecommendationService service) {
        return event -> {
            switch (event.getEventType()) {
                case CREATE -> service.createRecommendation(event.getData()).block();
                case DELETE -> service.deleteRecommendations(event.getKey()).block();
            }
        };
    }
}
