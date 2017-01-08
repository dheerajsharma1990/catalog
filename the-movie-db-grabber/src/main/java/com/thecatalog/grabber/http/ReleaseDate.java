package com.thecatalog.grabber.http;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReleaseDate {

    private LocalDate startDate;

    private LocalDate endDate;

    public ReleaseDate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isStartDateBeforeEndDate() {
        return startDate.isBefore(endDate);
    }

    public boolean isStartDateEqualToEndDate() {
        return startDate.isEqual(endDate);
    }

    public ReleaseDate getFirstHalf() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate mid = startDate.plusDays(days / 2);
        return new ReleaseDate(startDate, mid);
    }

    public ReleaseDate getSecondHalf() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate mid = startDate.plusDays(days / 2);
        return new ReleaseDate(mid.plusDays(1), endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReleaseDate that = (ReleaseDate) o;

        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;

    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReleaseDate{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
