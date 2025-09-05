package com.nutrimind.model;

import com.nutrimind.model.enums.AchievementType;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Achievement extends PanacheEntity {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public AchievementType type;

    @Column(name = "icon")
    public String icon;

    @Column(name = "points_value")
    public Integer pointsValue;

    // Reactive finder methods
    public static Uni<java.util.List<Achievement>> findByType(AchievementType type) {
        return find("type", type).list();
    }

    public static Uni<Achievement> findByName(String name) {
        return find("name", name).firstResult();
    }

    // Reactive save method
    public Uni<Achievement> store() {
        return persist().chain(() -> flush()).onItem().transform(voidValue -> this);
    }
}