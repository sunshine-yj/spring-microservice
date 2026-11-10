package se.magnus.microservices.composite.product.services;

import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.product.*;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration integration;

    public ProductCompositeServiceImpl(
            ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }


    @Override
    public void createProduct(ProductAggregate body) {
        int productId = body.getProductId();
        integration.createProduct(new Product(productId, body.getName(), body.getWeight(), null));

        if (body.getRecommendations() != null) {
            for (RecommendationSummary recommendation : body.getRecommendations()) {
                integration.createRecommendation(new Recommendation(
                        productId, recommendation.getRecommendationId(), recommendation.getAuthor(),
                        recommendation.getRate(), recommendation.getContent(), null));
            }
        }

        if (body.getReviews() != null) {
            for (ReviewSummary review : body.getReviews()) {
                integration.createReview(new Review(
                        productId, review.getReviewId(), review.getAuthor(),
                        review.getSubject(), review.getContent(), null));
            }
        }
    }

    @Override
    public void deleteProduct(int productId) {
        integration.deleteRecommendations(productId);
        integration.deleteReviews(productId);
        integration.deleteProduct(productId);
    }

    @Override
    public ProductAggregate getProduct(int productId) {

        Product product = integration.getProduct(productId);
        if (product == null) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        List<Recommendation> recommendations = integration.getRecommendations(productId);

        List<Review> reviews = integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {

        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        List<RecommendationSummary> recommendationSummaries =
                (recommendations == null) ? null : recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        List<ReviewSummary> reviewSummaries =
                (reviews == null) ? null : reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);

    }
}
