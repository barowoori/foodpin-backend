package com.barowoori.foodpinbackend.member.command.domain.model;

import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.infra.domain.ImageDirectory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private MemberType type;

    @Embedded
    private SocialLoginInfo socialLoginInfo;

    @Column(name = "image")
    private String image;

    protected Member(){}

    @Builder
    public Member(GenerateNicknameService nicknameGenerator, String phone, String email, String nickname, SocialLoginInfo socialLoginInfo) {
        setPhone(phone);
        this.email = email;
        this.nickname = makeNickname(nicknameGenerator, nickname);
        this.type = MemberType.NORMAL;
        this.socialLoginInfo = socialLoginInfo;
    }
    public void updateProfile(ImageManager imageManager, String nickname, MultipartFile image) {
        setNickname(nickname);
        setImage(imageManager, image);
    }

    public void updatePhone(String phone){
        setPhone(phone);
    }

    private String makeNickname(GenerateNicknameService nicknameGenerator, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return nicknameGenerator.generationNickname();
        }
        return nickname;
    }

    private void setNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("NICKNAME EMPTY");
        }
        this.nickname = nickname;
    }

    private void setPhone(String phone){
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("PHONE EMPTY");
        }
        this.phone = phone;
    }

    private void setImage(ImageManager imageManager, MultipartFile image) {
        if (image == null) {
            return;
        }
        if (this.image != null) {
            imageManager.deleteFile(this.image);
        }
        this.image = imageManager.updateFile(image, this.image, ImageDirectory.PROFILE);
    }
}
