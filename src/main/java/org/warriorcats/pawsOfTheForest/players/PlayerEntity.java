package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannel;

import java.util.UUID;

@Data
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_toggled", nullable = false)
    private ChatChannel chatToggled = ChatChannel.GLOBAL;
}
