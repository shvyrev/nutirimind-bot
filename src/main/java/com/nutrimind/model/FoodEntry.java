package com.nutrimind.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;


@Entity
@Table(name = "food_entries")
public class FoodEntry extends PanacheEntity {

    @ManyToOne
    public User user;

    @Column(name = "food_name", nullable = false)
    public String foodName;

    @Column(name = "calories")
    public Integer calories;

    @Column(name = "protein")
    public Float protein;

    @Column(name = "carbs")
    public Float carbs;

    @Column(name = "fat")
    public Float fat;

    @Column(name = "quantity")
    public Float quantity;

    @Column(name = "entry_date")
    public LocalDateTime entryDate;

    // Reactive finder methods
    public static Uni<java.util.List<FoodEntry>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<FoodEntry>> findByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return find("user.id = ?1 and entryDate between ?2 and ?3", userId, start, end).list();
    }

    // Reactive save method
    public Uni<FoodEntry> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (FoodEntry) entity);
    }
}