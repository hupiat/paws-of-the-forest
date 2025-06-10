package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.SkillEntity;

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

    @Column(name = "xp", nullable = false)
    private double xp;

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
    private List<SkillBranchEntity> skillBranchs;

    public boolean hasAbility(String skillName) {
        return skillBranchs.stream().anyMatch(branche ->
                branche.getSkills().stream().anyMatch(skill ->
                        skill.getName().trim().equalsIgnoreCase(skillName.trim())));
    }

    public double getAbilityPerk(String skillName) {
        for (SkillBranchEntity branche : skillBranchs) {
            Optional<SkillEntity> skillEntity = branche.getSkills().stream()
                    .filter(skill -> skill.getName().trim().equalsIgnoreCase(skillName.trim()))
                    .findFirst();
            if (skillEntity.isPresent()) {
                return skillEntity.get().getProgress();
            }
        }
        return 0;
    }
}
