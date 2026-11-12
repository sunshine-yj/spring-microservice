package se.magnus.microservices.core.product.messaging;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.event.Event;

@Configuration
public class MessageProcessorConfig {

    @Bean
    public Consumer<Event<Integer, Product>> messageProcessor(ProductService service) {
        return event -> {
            switch (event.getEventType()) {
                case CREATE -> service.createProduct(event.getData()).block();
                case DELETE -> service.deleteProduct(event.getKey()).block();
            }
        };
    }
}
