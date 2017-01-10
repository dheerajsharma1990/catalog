package com.findmymovie.grabber.http;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReleaseDateRange {

    private LocalDate startDate;

    private LocalDate endDate;

    public ReleaseDateRange(LocalDate startDate, LocalDate endDate) {
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

    public ReleaseDateRange getFirstHalf() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate mid = startDate.plusDays(days / 2);
        return new ReleaseDateRange(startDate, mid);
    }

    public ReleaseDateRange getSecondHalf() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate mid = startDate.plusDays(days / 2);
        return new ReleaseDateRange(mid.plusDays(1), endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReleaseDateRange that = (ReleaseDateRange) o;

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
        return "ReleaseDateRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
