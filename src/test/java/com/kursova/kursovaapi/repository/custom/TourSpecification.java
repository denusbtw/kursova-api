package com.kursova.kursovaapi.repository.custom;

import com.kursova.kursovaapi.entity.TourEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TourSpecificationTest {

    private Root<TourEntity> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;

    @BeforeEach
    void setup() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
    }

    @Test
    void nameContains_null_returnsNullPredicate() {
        var spec = TourSpecification.nameContains(null);
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void hasType_blank_returnsNullPredicate() {
        var spec = TourSpecification.hasType("  ");
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void hasMealOption_valid_returnsPredicate() {
        Path<String> path = mock(Path.class);
        when(root.get("mealOption")).thenReturn((Path) path);
        when(cb.lower(path)).thenReturn(path);
        when(cb.equal(eq(path), anyString())).thenReturn(mock(Predicate.class));

        var spec = TourSpecification.hasMealOption("Full Board");
        assertThat(spec.toPredicate(root, query, cb)).isNotNull();
    }

    @Test
    void minDays_null_returnsNullPredicate() {
        var spec = TourSpecification.minDays(null);
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void maxDays_valid_returnsPredicate() {
        Path<Integer> path = mock(Path.class);
        when(root.get("numberOfDays")).thenReturn((Path) path);
        when(cb.lessThanOrEqualTo(eq(path), eq(7))).thenReturn(mock(Predicate.class));

        var spec = TourSpecification.maxDays(7);
        assertThat(spec.toPredicate(root, query, cb)).isNotNull();
    }

    @Test
    void minPrice_valid_returnsPredicate() {
        Path<Integer> path = mock(Path.class);
        when(root.get("price")).thenReturn((Path) path);
        when(cb.greaterThanOrEqualTo(eq(path), eq(100))).thenReturn(mock(Predicate.class));

        var spec = TourSpecification.minPrice(100);
        assertThat(spec.toPredicate(root, query, cb)).isNotNull();
    }

    @Test
    void maxPrice_null_returnsNullPredicate() {
        var spec = TourSpecification.maxPrice(null);
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void minRating_valid_returnsPredicate() {
        Path<Double> path = mock(Path.class);
        when(root.get("rating")).thenReturn((Path) path);
        when(cb.greaterThanOrEqualTo(eq(path), eq(4.5))).thenReturn(mock(Predicate.class));

        var spec = TourSpecification.minRating(4.5);
        assertThat(spec.toPredicate(root, query, cb)).isNotNull();
    }

    @Test
    void maxRating_null_returnsNullPredicate() {
        var spec = TourSpecification.maxRating(null);
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void hasTransportName_blank_returnsNullPredicate() {
        var spec = TourSpecification.hasTransportName("   ");
        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    void hasTransportName_valid_returnsPredicate() {
        Join<Object, Object> join = mock(Join.class);
        Path<String> namePath = mock(Path.class);

        when(root.join("transport")).thenReturn(join);
        when(join.get("name")).thenReturn((Path) namePath);
        when(cb.lower(namePath)).thenReturn(namePath);
        when(cb.equal(eq(namePath), eq("bus"))).thenReturn(mock(Predicate.class));

        var spec = TourSpecification.hasTransportName("Bus");
        assertThat(spec.toPredicate(root, query, cb)).isNotNull();
    }
}
