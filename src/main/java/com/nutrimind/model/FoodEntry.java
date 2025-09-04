package com.nutrimind.model;

import java.time.LocalDateTime;

import com.nutrimind.model.enums.MealType;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "food_entries")
public class FoodEntry extends PanacheEntity {

    @ManyToOne
    public User user;

    @Column(name = "timestamp")
    public LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    public MealType mealType;

    @Column(name = "description")
    public String description;

    @Column(name = "photo_id")
    public String photoId;

    @Column(name = "calories")
    public Double calories;

    @Column(name = "protein")
    public Double protein;

    @Column(name = "fat")
    public Double fat;

    @Column(name = "carbs")
    public Double carbs;

    @Column(name = "mood_before")
    public String moodBefore;

    @Column(name = "mood_after")
    public String moodAfter;

    @Column(name = "was_hungry")
    public Boolean wasHungry;

    @Column(name = "eating_context")
    public String eatingContext;

    // Reactive finder methods
    public static Uni<java.util.List<FoodEntry>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<FoodEntry>> findByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return find("user.id = ?1 and timestamp between ?2 and ?3", userId, start, end).list();
    }

    public static Uni<java.util.List<FoodEntry>> findByMealType(Long userId, MealType mealType) {
        return find("user.id = ?1 and mealType = ?2", userId, mealType).list();
    }

    // Reactive save method
    public Uni<FoodEntry> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (FoodEntry) entity);
    }
}