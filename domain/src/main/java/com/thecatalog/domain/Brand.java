package com.thecatalog.domain;

public class Brand {

    private BrandId brandId;

    private String brandName;

    public Brand(BrandId brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brand brand = (Brand) o;

        return brandId != null ? brandId.equals(brand.brandId) : brand.brandId == null;

    }

    public int hashCode() {
        return brandId != null ? brandId.hashCode() : 0;
    }

    public String toString() {
        return "Brand{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
