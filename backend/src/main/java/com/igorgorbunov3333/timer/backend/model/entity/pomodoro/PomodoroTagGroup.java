package com.igorgorbunov3333.timer.backend.model.entity.pomodoro;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PomodoroTagGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Fetch(FetchMode.JOIN)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pomodoro_tag_group_tag", joinColumns = {@JoinColumn(name = "pomodoro_tag_group_id")}, inverseJoinColumns = {@JoinColumn(name = "pomodoro_tag_id")})
    private Set<PomodoroTag> pomodoroTags;

    @Setter
    @Column(nullable = false)
    private Long orderNumber;

}