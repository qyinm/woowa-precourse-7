package store.service;

import static store.exception.store.StoreErrorCode.EXCEED_PRODUCT_QUANTITY;
import static store.exception.store.StoreErrorCode.NOT_FOUND_PRODUCT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import store.domain.Item;
import store.domain.Product;
import store.domain.Promotion;
import store.dtos.ReceiptDto;
import store.exception.StoreException;
import store.repository.StoreRepository;

public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public boolean hasActivePromotion(String productName) {
        Optional<Product> promotionProduct = storeRepository.findPromotionProductByName(productName);

        return promotionProduct.map(product -> product.getPromotion().get().isActive()).orElse(false);
    }

    public boolean isSufficientQuantityGeneralProduct(String productName, BigDecimal quantity) {
        Optional<Product> generalProduct = storeRepository.findGeneralProductByName(productName);

        return generalProduct.map(isGreaterQuantityThan(quantity)).orElse(false);
    }

    private Function<Product, Boolean> isGreaterQuantityThan(BigDecimal quantity) {
        return product -> product.getQuantity().compareTo(quantity) >= 0;
    }

    public boolean isSufficientQuantityPromotionProduct(String productName, BigDecimal quantity) {
        Optional<Product> generalProduct = storeRepository.findPromotionProductByName(productName);

        return generalProduct.map(isGreaterQuantityThan(quantity)).orElse(false);
    }

    public boolean isSufficientQuantityToGetBonusProduct(String productName, BigDecimal quantity) {
        Optional<Product> promotionProduct = storeRepository.findPromotionProductByName(productName);
        Promotion promotion = promotionProduct.get().getPromotion().get();

        Boolean isSufficientQuantity = promotionProduct.map(isGreaterQuantityThan(quantity)).orElse(false);
        return promotion.isAvailableToGetBonus(quantity) && isSufficientQuantity;
    }

    public Product getGeneralProduct(String productName) {
        validateHasProductWithName(productName);

        Product product = storeRepository.findGeneralProductByName(productName).get();
        return product;
    }

    public Product purchaseGeneralProduct(String productName, BigDecimal quantity) {
        Product generalProduct = getGeneralProduct(productName);

        storeRepository.updateGeneralProduct(productName, generalProduct.getQuantity().subtract(quantity));
        return generalProduct;
    }

    private void validateHasProductWithName(String productName) {
        storeRepository.findPromotionProductByName(productName)
                .or(() -> storeRepository.findGeneralProductByName(productName))
                .orElseThrow(() -> new StoreException(NOT_FOUND_PRODUCT));
    }

    public Product getPromotionProduct(String productName) {
        validateHasProductWithName(productName);

        Product product = storeRepository.findPromotionProductByName(productName).get();
        return product;
    }

    public Product purchasePromotionProduct(String productName, BigDecimal quantity) {
        Product promotionProduct = getPromotionProduct(productName);

        storeRepository.updatePromotionProduct(productName, promotionProduct.getQuantity().subtract(quantity));
        return promotionProduct;
    }

    public void validatePurchasableProduct(String productName, BigDecimal quantity) {
        BigDecimal promotionQuantity = storeRepository.findPromotionProductByName(productName).map(Product::getQuantity)
                .orElse(BigDecimal.ZERO);
        BigDecimal generalQuantity = storeRepository.findGeneralProductByName(productName).map(Product::getQuantity)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalQuantity = promotionQuantity.add(generalQuantity);
        if (quantity.compareTo(totalQuantity) > 0) {
            throw new StoreException(EXCEED_PRODUCT_QUANTITY);
        }
    }

    public Set<Product> getAllProducts() {
        return storeRepository.getAllProducts();
    }

    public List<Item> getMixedItems(String itemName, BigDecimal quantity, Product promotionProduct) {
        BigDecimal willBuyPromotionProductQuantity = promotionProduct.getQuantity();
        BigDecimal willBuyGeneralProductQuantity = quantity.subtract(willBuyPromotionProductQuantity);

        return List.of(new Item(purchasePromotionProduct(itemName, willBuyPromotionProductQuantity),
                        willBuyPromotionProductQuantity),
                new Item(purchaseGeneralProduct(itemName, willBuyGeneralProductQuantity),
                        willBuyGeneralProductQuantity)
        );
    }

    private BigDecimal calculateMembershipDiscountAmount(List<Item> cart, List<Item> promotionBonusItems) {
        return cart.stream().filter(item -> promotionBonusItems.stream()
                        .noneMatch(bonusItem -> bonusItem.product().getName().equals(item.product().getName())))
                .map(item -> item.getTotalQuantity().multiply(BigDecimal.valueOf(0.3)))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.FLOOR)
                .min(BigDecimal.valueOf(8000));
    }

    private List<Item> getPromotionBonusItems(List<Item> cart) {
        return cart.stream().filter(item -> item.product().getPromotion().isPresent()).map(item -> {
            Promotion promotion = item.product().getPromotion().get();
            BigDecimal bonusQuantity = item.quantity()
                    .divide(promotion.getBuyAmount().add(promotion.getGetAmount()), 0, RoundingMode.FLOOR);
            return new Item(item.product(), bonusQuantity);
        }).toList();
    }

    public ReceiptDto calculateUserPurchase(List<Item> cart, boolean applyMembershipDiscount) {
        BigDecimal totalPay = calculateAllItemsTotalPrice(cart);
        List<Item> promotionBonusItems = getPromotionBonusItems(cart);
        BigDecimal promotionDiscountPay = calculateAllItemsTotalPrice(promotionBonusItems);
        BigDecimal membershipDiscountPay = calculateMembershipDiscountPrice(cart, applyMembershipDiscount,
                promotionBonusItems);
        BigDecimal willPayAmounts = totalPay.subtract(promotionDiscountPay).subtract(membershipDiscountPay);

        return new ReceiptDto(promotionBonusItems, totalPay, promotionDiscountPay, membershipDiscountPay,
                willPayAmounts);
    }

    private BigDecimal calculateMembershipDiscountPrice(List<Item> cart, boolean applyMembershipDiscount,
                                                        List<Item> promotionBonusItems) {
        if (!applyMembershipDiscount) {
            return BigDecimal.ZERO;
        }
        return calculateMembershipDiscountAmount(cart, promotionBonusItems);
    }

    private BigDecimal calculateAllItemsTotalPrice(List<Item> promotionBonusItems) {
        return promotionBonusItems.stream().map(Item::getTotalQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
