package com.nutrimind.model;

import java.util.List;
import java.util.Map;

import com.nutrimind.model.enums.MealType;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "nutrition_plans")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NutritionPlan extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(name = "daily_calories")
    public Double dailyCalories;

    @Column(name = "protein_target")
    public Double proteinTarget;

    @Column(name = "fat_target")
    public Double fatTarget;

    @Column(name = "carbs_target")
    public Double carbsTarget;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nutrition_plan_meals", joinColumns = @JoinColumn(name = "plan_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "meal_type")
    @Column(name = "meal_plan")
    public Map<MealType, String> mealPlans;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nutrition_plan_recommendations", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "recommendation")
    public List<String> foodRecommendations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nutrition_plan_seasonal_foods", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "seasonal_food")
    public List<String> seasonalFoods;

    // Reactive finder methods
    public static Uni<NutritionPlan> findByUserId(Long userId) {
        return find("user.id", userId).firstResult();
    }

    // Reactive save method
    public Uni<NutritionPlan> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (NutritionPlan) entity);
    }
}