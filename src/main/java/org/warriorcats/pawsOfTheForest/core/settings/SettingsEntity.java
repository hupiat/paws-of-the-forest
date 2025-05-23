package org.warriorcats.pawsOfTheForest.core.settings;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;

@Data
@Entity
@Table(name = "player_settings")
public class SettingsEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @Column(name = "show_roleplay")
    private boolean showRoleplay = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "toggled_chat")
    private ChatChannel toggledChat = ChatChannel.DEFAULT_TOGGLED;
}
