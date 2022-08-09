package com.igorgorbunov3333.timer.model.entity.pomodoro;

import com.igorgorbunov3333.timer.model.TemporalObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Pomodoro implements TemporalObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime startTime;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime endTime;

    @Column(nullable = false, updatable = false)
    private boolean savedAutomatically;

    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "pomodoroId")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PomodoroPause> pomodoroPauses;

    @Setter
    @ManyToMany
    @JoinTable(name = "pomodoro_pomodoro_tag", joinColumns = {@JoinColumn(name = "pomodoro_id")}, inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<PomodoroTag> tags;

    @Setter
    @ManyToOne
    @JoinColumn(name = "tag_group_id")
    private PomodoroTagGroup pomodoroTagGroup;

}
