package com.igorgorbunov3333.timer.model.entity.pomodoro;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PomodoroTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Setter
    @ManyToOne
    private PomodoroTag parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PomodoroTag> children;

    @Setter
    private boolean removed;

    public void addChildTag(PomodoroTag childTag) {
        if (CollectionUtils.isEmpty(this.children)) {
            this.children = new ArrayList<>(List.of(childTag));
        } else {
            this.children.add(childTag);
        }
        childTag.setParent(this);
    }

    public void removeChild(PomodoroTag childTag) {
        if (CollectionUtils.isEmpty(this.children)) {
            return;
        }
        childTag.setParent(null);
        this.children.remove(childTag);
    }

}
