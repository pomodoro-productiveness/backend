package com.igorgorbunov3333.timer.model.entity.pomodoro;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroTagBunch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Fetch(FetchMode.JOIN)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pomodoro_tag_bunch_tag", joinColumns = {@JoinColumn(name = "pomodoro_tag_bunch_id")}, inverseJoinColumns = {@JoinColumn(name = "pomodoro_tag_id")})
    private List<PomodoroTag> pomodoroTags;

}
