package org.warriorcats.pawsOfTheForest.skills;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "skill_branchs")
public class SkillBranchEntity {

    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    private SkillBranches branche;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SkillEntity> skills = new ArrayList<>();
}
