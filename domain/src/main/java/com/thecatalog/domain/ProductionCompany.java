package com.thecatalog.domain;

import com.thecatalog.domain.id.ProductionCompanyId;

public class ProductionCompany {

    private ProductionCompanyId productionCompanyId;

    private String productionCompany;

    public ProductionCompany(ProductionCompanyId productionCompanyId, String productionCompany) {
        this.productionCompanyId = productionCompanyId;
        this.productionCompany = productionCompany;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionCompany that = (ProductionCompany) o;

        return productionCompanyId != null ? productionCompanyId.equals(that.productionCompanyId) : that.productionCompanyId == null;

    }

    @Override
    public int hashCode() {
        return productionCompanyId != null ? productionCompanyId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProductionCompany{" +
                "productionCompanyId=" + productionCompanyId +
                ", productionCompany='" + productionCompany + '\'' +
                '}';
    }
}
