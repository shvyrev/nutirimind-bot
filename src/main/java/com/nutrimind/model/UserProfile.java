package com.nutrimind.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.nutrimind.model.enums.ActivityLevel;
import com.nutrimind.model.enums.Gender;
import com.nutrimind.model.enums.HealthGoal;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "user_profiles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserProfile extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(name = "age")
    public Integer age;

    @Column(name = "height")
    public Double height;

    @Column(name = "weight")
    public Double weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    public Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    public ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_goal")
    public HealthGoal healthGoal;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_dietary_restrictions", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "restriction")
    public List<String> dietaryRestrictions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_food_preferences", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "preference")
    public List<String> foodPreferences;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "allergy")
    public List<String> allergies;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_disliked_foods", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "disliked_food")
    public List<String> dislikedFoods;

    @Column(name = "meals_per_day")
    public Integer mealsPerDay;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_typical_meal_times", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day_of_week")
    @Column(name = "meal_time")
    public Map<DayOfWeek, LocalTime> typicalMealTimes;

    @Column(name = "eats_out_frequency")
    public Boolean eatsOutFrequency;

    // Reactive finder methods
    public static Uni<UserProfile> findByUserId(Long userId) {
        return find("user.id", userId).firstResult();
    }

    // Reactive save method
    public Uni<UserProfile> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (UserProfile) entity);
    }
}