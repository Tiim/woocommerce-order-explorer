package ch.scbirs.shop.orderexplorer.model.local;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDataCleaner {

    private static final Logger LOGGER = LogUtil.get();


    public Data clean(Data data) {
        Set<Integer> allIds = findAllProductIds(data);

        data = cleanUserData(data, allIds);

        return data;
    }

    private Data cleanUserData(Data data, Set<Integer> allIds) {
        Map<Integer, ProductData> productData = data.getUserData().getProductData();
        Map<Integer, ProductData> clean = new HashMap<>();
        for (int i : allIds) {
            ProductData value = productData.get(i);
            if (value != null) {
                clean.put(i, value);
            }
        }

        LOGGER.info("Saved " + (clean.size() - productData.size()) + " product data entries");
        return data.withUserData(data.getUserData().withProductData(clean));
    }

    private Set<Integer> findAllProductIds(Data data) {
        return data.getOrders()
                .stream()
                .flatMap((o) -> o.getProducts().stream())
                .map(Product::getId)
                .collect(Collectors.toSet());
    }

}
