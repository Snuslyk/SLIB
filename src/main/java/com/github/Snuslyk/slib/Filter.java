package com.github.Snuslyk.slib;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class Filter {

    public final String filteredValue;
    public final Object exceptedValue;
    public Type type;
    public final TypeIO typeIO;

    public Filter(String filteredValue, Object exceptedValue, Type type){
        this.filteredValue = filteredValue;
        this.exceptedValue = exceptedValue;
        this.type = type;
        this.typeIO = type.typeIO;
    }

    public Filter(String filteredValue, Object exceptedValue, TypeIO typeIO){
        this.filteredValue = filteredValue;
        this.exceptedValue = exceptedValue;
        this.typeIO = typeIO;
    }

    public enum Type {
        EQUAL(new TypeIO() {
            @Override
            public <T> void getPredicates(Root<T> root, Filter filter, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
                predicates.add(criteriaBuilder.equal(root.get(filter.filteredValue), filter.exceptedValue));
            }
        }),
        RANGE(new TypeIO() {
            @Override
            public <T> void getPredicates(Root<T> root, Filter filter, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
                Range range = (Range) filter.exceptedValue;

                if (range.min != Double.NEGATIVE_INFINITY) {
                    Predicate minPredicate = range.canEqualMin ? criteriaBuilder.greaterThanOrEqualTo(root.get(filter.filteredValue), range.min) : criteriaBuilder.greaterThan(root.get(filter.filteredValue), range.min);
                    predicates.add(minPredicate);
                }
                if (range.max != Double.POSITIVE_INFINITY) {
                    Predicate maxPredicate = range.canEqualMax ? criteriaBuilder.lessThanOrEqualTo(root.get(filter.filteredValue), range.max) : criteriaBuilder.lessThan(root.get(filter.filteredValue), range.max);
                    predicates.add(maxPredicate);
                }
            }
        });

        final TypeIO typeIO;

        Type(TypeIO typeIO){
            this.typeIO = typeIO;
        }

        public TypeIO getTypeIO() {
            return typeIO;
        }
    }

    public interface TypeIO {
        <T> void getPredicates(Root<T> root, Filter filter, CriteriaBuilder criteriaBuilder, List<Predicate> predicates);
    }

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
