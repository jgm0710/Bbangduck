package bbangduck.bd.bbangduck.domain.shop.entity.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShopPriceUnit {
    WON("원");

    private final String description;

    public String getDescription() {
        return this.description;
    }
}
