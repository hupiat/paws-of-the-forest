package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    // No default initializer here as it is supposed to come from Bukkit getUniqueId()
    private UUID uuid;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "xp_perks", nullable = false)
    private double xpPerks;

    @Column(name = "coins", nullable = false)
    private long coins;

    @Enumerated(EnumType.STRING)
    @Column(name = "clan")
    private Clans clan;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_uuid", nullable = false)
    private SettingsEntity settings;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SkillBranchEntity> skillBranches = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] backpackData;

    public boolean hasAbility(Skills skill) {
        return getAbilityInternal(skill).isPresent();
    }

    public int getAbilityTier(Skills skill) {
        return skill.getCurrentTier(getAbilityPerk(skill));
    }

    public double getAbilityPerk(Skills skill) {
        return getAbilityInternal(skill).map(SkillEntity::getProgress).orElse(0d);
    }

    public SkillEntity getAbility(Skills skill) {
        return getAbilityInternal(skill).orElse(null);
    }

    public SkillBranchEntity getAbilityBranch(Skills skill) {
        for (SkillBranchEntity branche : skillBranches) {
            if (branche.getBranch() == skill.getBranch()) {
                return branche;
            }
        }
        throw new IllegalArgumentException("Could not find ability branch for skill : " + skill);
    }

    private Optional<SkillEntity> getAbilityInternal(Skills skill) {
        Optional<SkillEntity> skillEntity = Optional.empty();
        for (SkillBranchEntity branche : skillBranches) {
            skillEntity = branche.getSkills().stream()
                .filter(other -> other.getSkill() == skill)
                .findFirst();
            if (skillEntity.isPresent()) {
                return skillEntity;
            }
        }
        return skillEntity;
    }
}
