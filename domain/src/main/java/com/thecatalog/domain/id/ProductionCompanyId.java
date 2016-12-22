package com.thecatalog.domain.id;

public class ProductionCompanyId {

    private int productionCompanyId;

    public ProductionCompanyId(int productionCompanyId) {
        this.productionCompanyId = productionCompanyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionCompanyId that = (ProductionCompanyId) o;

        return productionCompanyId == that.productionCompanyId;

    }

    @Override
    public int hashCode() {
        return productionCompanyId;
    }

    @Override
    public String toString() {
        return "ProductionCompanyId{" +
                "productionCompanyId=" + productionCompanyId +
                '}';
    }
}
