package com.jungle.chalnaServer.domain.chatRoom.domain.entity;

import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import com.jungle.chalnaServer.domain.File.domain.entity.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    @Column(nullable = false)
    private Integer memberCount;

    private LocalDateTime removedAt;

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatRoomMember> members = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;



    public enum ChatRoomType{
        MATCH,
        FRIEND,
        LOCAL,
        CLOSED,  // 채팅방 닫힘 - 채팅 불가
        WAITING  // 채팅방 열린지 5분 지난 후. 친구 수락/거절 대기 상태
    }

    public ChatRoom(ChatRoomType type, Integer memberCount) {
        this.type = type;
        this.memberCount = memberCount;
    }

    public void updateType(ChatRoomType newType){
        this.type = newType;
    }

    public void updateMemberCount(Integer newCount) {
        this.memberCount = newCount;
    }

    public void updateRemovedAt() {
        this.removedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

}
