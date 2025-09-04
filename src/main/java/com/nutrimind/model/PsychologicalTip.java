package com.nutrimind.model;

import java.time.LocalDateTime;

import com.nutrimind.model.enums.TipType;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "psychological_tips")
public class PsychologicalTip extends PanacheEntity {

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "content")
    public String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public TipType type;

    @Column(name = "trigger_condition")
    public String triggerCondition;

    @Column(name = "min_level")
    public Integer minLevel;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    // Reactive finder methods
    public static Uni<java.util.List<PsychologicalTip>> findByType(TipType type) {
        return find("type", type).list();
    }

    public static Uni<java.util.List<PsychologicalTip>> findByTriggerCondition(String triggerCondition) {
        return find("triggerCondition", triggerCondition).list();
    }

    public static Uni<PsychologicalTip> findByTitle(String title) {
        return find("title", title).firstResult();
    }

    // Reactive save method
    public Uni<PsychologicalTip> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (PsychologicalTip) entity);
    }
}