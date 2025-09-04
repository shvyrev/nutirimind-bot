package com.nutrimind.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "psychological_tips")
public class PsychologicalTip extends PanacheEntity {

    @Column(name = "category")
    public String category;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "content")
    public String content;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    // Reactive finder methods
    public static Uni<java.util.List<PsychologicalTip>> findByCategory(String category) {
        return find("category", category).list();
    }

    public static Uni<PsychologicalTip> findByTitle(String title) {
        return find("title", title).firstResult();
    }

    // Reactive save method
    public Uni<PsychologicalTip> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (PsychologicalTip) entity);
    }
}