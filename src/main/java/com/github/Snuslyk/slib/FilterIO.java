package com.github.Snuslyk.slib;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface FilterIO {
    <T> void getPredicates(Root<T> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates);

    public static class Range{
        public final double min;
        public final double max;
        public final boolean canEqualMin;
        public final boolean canEqualMax;

        public Range(double min, double max, boolean canEqualMin, boolean canEqualMax){
            this.min = min;
            this.max = max;

            this.canEqualMin = canEqualMin;
            this.canEqualMax = canEqualMax;
        }

        public static Range from(double min){
            return new Range(min, Double.POSITIVE_INFINITY, false, false);
        }

        public static Range to(double max){
            return new Range(Double.NEGATIVE_INFINITY, max, false, false);
        }

        public static Range from(double min, boolean canEqual){
            return new Range(min, Double.POSITIVE_INFINITY, canEqual, false);
        }

        public static Range to(double max, boolean canEqual){
            return new Range(Double.NEGATIVE_INFINITY, max, false, canEqual);
        }
    }
}
