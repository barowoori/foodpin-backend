package com.barowoori.foodpinbackend.member.command.domain.model;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.infra.domain.ImageDirectory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "members")
@Getter
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "types")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<MemberType> types = new HashSet<>();

    @Embedded
    private SocialLoginInfo socialLoginInfo;

    @Column(name = "image")
    private String image;

    @Column(name = "refresh_token")
    private String refreshToken;

    protected Member() {
    }

    @Builder
    public Member(String phone, String email, String image, String nickname, SocialLoginInfo socialLoginInfo, String refreshToken) {
        setPhone(phone);
        this.email = email;
        setNickname(nickname);
        this.image = image;
        this.types.add(MemberType.NORMAL);
        this.socialLoginInfo = socialLoginInfo;
        this.refreshToken = refreshToken;
    }

    public void updateProfile(ImageManager imageManager, String nickname, String originImageUrl, MultipartFile image) {
        setNickname(nickname);
        setImage(imageManager, originImageUrl, image);
    }

    public void updatePhone(String phone) {
        setPhone(phone);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean matchRefreshToken(String refreshToken) {
        if (this.refreshToken == null) {
            throw new CustomException(MemberErrorCode.MEMBER_ORIGIN_REFRESH_TOKEN_EMPTY);
        }
        return this.refreshToken.equals(refreshToken);
    }

    private void setNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_EMPTY);
        }
        this.nickname = nickname;
    }

    private void setPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new CustomException(MemberErrorCode.MEMBER_PHONE_EMPTY);
        }
        this.phone = phone;
    }

    private void setImage(ImageManager imageManager, String originImageUrl, MultipartFile image) {
        if (image == null) {
            this.image = originImageUrl;
            return;
        }
        if (this.image != null) {
            imageManager.deleteFile(this.image);
        }
        this.image = imageManager.updateFile(image, this.image, ImageDirectory.PROFILE);
    }

    //Spring Security에서 쓰이는 getAuthorities는 ROLE_ 접두사를 사용하므로 type 앞에 붙여서 넣어줌
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return types.stream()
                .map(type -> new SimpleGrantedAuthority("ROLE_" + type.name()))
                .collect(Collectors.toList());
    }

    //sso 기반이니 비밀번호는 미사용
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * spring security에서 사용하는 principal 값(String)
     * @return String 타입 id
     */
    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }
}
