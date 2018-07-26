package ch.scbirs.shop.orderexplorer.web.products;

import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.SteppedTask;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ProductFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();

    private final SimpleProductFetcher simpleProductFetcher;
    private final VariableProductFetcher variableProductFetcher;


    public ProductFetcher(UserSettings settings) {
        simpleProductFetcher = new SimpleProductFetcher(settings);
        variableProductFetcher = new VariableProductFetcher(settings);
    }

    @Override
    public void doStep() throws Exception {
        if (!simpleProductFetcher.isDone()) {
            LOGGER.info("loading next simple product");
            simpleProductFetcher.doStep();
            if (simpleProductFetcher.isDone()) {
                LOGGER.info("all simple products loaded");
                variableProductFetcher.setInputProducts(simpleProductFetcher.getProducts());
            }
        } else {
            LOGGER.info("load next variable product");
            variableProductFetcher.doStep();
        }
    }

    @Override
    public boolean isDone() {
        return simpleProductFetcher.isDone() & variableProductFetcher.isDone();
    }

    @Override
    public int currentProgress() {
        return simpleProductFetcher.currentProgress() + variableProductFetcher.currentProgress();
    }

    @Override
    public int maxProgress() {
        return simpleProductFetcher.maxProgress() + variableProductFetcher.maxProgress();
    }

    public List<ProductVariation> getProducts() {
        return variableProductFetcher.getProducts();
    }
}
