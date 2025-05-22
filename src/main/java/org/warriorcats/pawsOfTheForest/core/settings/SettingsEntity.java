package org.warriorcats.pawsOfTheForest.core.settings;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;

@Data
@Entity
@Table(name = "player_settings")
public class SettingsEntity {

    @Column(name = "show_roleplay")
    private boolean showRoleplay;

    @Enumerated(EnumType.STRING)
    @Column(name = "toggled_chat")
    private ChatChannel toggledChat;
}
