package com.yashmerino.ecommerce.services;

import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.services.interfaces.AlgoliaService;
import com.yashmerino.ecommerce.repositories.ProductRepository;
import com.yashmerino.ecommerce.utils.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs on application startup and ensures products are indexed in Algolia.
 */
@Component
@Slf4j
public class ProductIndexingRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final ProductRepository productRepository;
    private final AlgoliaService algoliaService;
    private final ApplicationProperties applicationProperties;

    public ProductIndexingRunner(ProductRepository productRepository,
                                 AlgoliaService algoliaService,
                                 ApplicationProperties applicationProperties) {
        this.productRepository = productRepository;
        this.algoliaService = algoliaService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!applicationProperties.isAlgoliaUsed) {
            log.info("Algolia usage is disabled (algolia.usage=false). Skipping products reindex.");
            return;
        }

        try {
            List<Product> products = productRepository.findAll();
            log.info("Found {} products. Starting Algolia reindex.", products.size());
            algoliaService.populateIndex(products);
            log.info("Algolia reindex finished successfully.");
        } catch (Exception ex) {
            log.error("Failed to reindex products to Algolia on startup", ex);
        }
    }
}
