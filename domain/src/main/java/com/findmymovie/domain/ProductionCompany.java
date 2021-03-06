package com.findmymovie.domain;

import com.findmymovie.domain.id.ProductionCompanyId;

public class ProductionCompany {

    private ProductionCompanyId productionCompanyId;

    private String productionCompany;

    public ProductionCompany(ProductionCompanyId productionCompanyId, String productionCompany) {
        this.productionCompanyId = productionCompanyId;
        this.productionCompany = productionCompany;
    }


    public ProductionCompanyId getProductionCompanyId() {
        return productionCompanyId;
    }

    public String getProductionCompany() {
        return productionCompany;
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
