package com.thecatalog.domain;

public class BrandId {

    private String brandId;

    public BrandId(String brandId) {
        this.brandId = brandId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrandId brandId1 = (BrandId) o;

        return brandId != null ? brandId.equals(brandId1.brandId) : brandId1.brandId == null;

    }

    public int hashCode() {
        return brandId != null ? brandId.hashCode() : 0;
    }

    public String toString() {
        return "BrandId{" +
                "brandId='" + brandId + '\'' +
                '}';
    }
}
