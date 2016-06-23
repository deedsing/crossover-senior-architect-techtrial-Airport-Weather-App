package com.crossover.trial.weather.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <h1>DataPoint</h1>
 * <p>This object is used to hold weathe statistics that corresponds to weather type {@ link DataPointType}</p>
 * <p>
 * In Math statistics a concept called quantiles is used to take different readings of some
 * quantity over a course of time. The entire data set is  divided into four equal portions and 
 * the readings which mark the boundary are recorded.
 * Middle number is called the mean/median.
 * The first quartile (Q1) is defined as the middle number between the smallest number and the median of the data set. 
 * The second quartile (Q2) is the median of the data. 
 * The third quartile (Q3) is the middle value between the median and the highest value of the data set.
 * Count denotes a set of readings.
 * </p>
 * 
 * <p>
 * Example:
 *Data Set : 3, 4, 4, 5, 6, 8, 8
 *Median/Mean : 5
 *First (Q1): 4
 *Second (Q2): 5 
 *Third (Q3): 8
 *Count : 7 
 * </p>
 * 
 *
 * @author deedsing
 */
public final class DataPoint {

    public double mean = 0.0;

    public int first = 0;

    public int second = 0;

    public int third = 0;

    public int count = 0;

    /** private constructor, use the builder to create this object */
    public DataPoint() { }

    public DataPoint(int first, int mean, int median, int third, int count) {
        this.setFirst(first);
        this.setMean(mean);
        this.setSecond(median);
        this.setThird(third);
        this.setCount(count);
    
    }

   

	/** the mean of the observations */
    public double getMean() {
        return mean;
    }

    public void setMean(double mean) { this.mean = mean; }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    protected void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    protected void setSecond(int second) {
        this.second = second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    protected void setThird(int third) {
        this.third = third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    protected void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object that) {
        return this.toString().equals(that.toString());
    }

    static public class Builder {
        int first;
        int mean;
        int median;
        int last;
        int count;

        public Builder() { }

        public Builder withFirst(int first) {
            this.first= first;
            return this;
        }

        public Builder withMean(int mean) {
            this.mean = mean;
            return this;
        }

        public Builder withMedian(int median) {
            this.median = median;
            return this;
        }

        public Builder withCount(int count) {
            this.count = count;
            return this;
        }

        public Builder withLast(int last) {
            this.last = last;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(this.first, this.mean, this.median, this.last, this.count);
        }
    }
}
