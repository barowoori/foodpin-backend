package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.exception.EventErrorCode;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.*;
import com.barowoori.foodpinbackend.member.command.domain.repository.EventLikeRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.InterestEventCategoryRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.InterestEventRegionRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.InterestEventRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.region.command.domain.query.application.RegionSearchProcessor;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGu;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionGun;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.dto.RegionInfo;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.application.service.TruckService;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GenerateNicknameService generateNicknameService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageManager imageManager;
    private final TruckLikeRepository truckLikeRepository;
    private final TruckRepository truckRepository;
    private final FileRepository fileRepository;
    private final EventRepository eventRepository;
    private final EventLikeRepository eventLikeRepository;
    private final InterestEventRepository interestEventRepository;
    private final InterestEventRegionRepository interestEventRegionRepository;
    private final InterestEventCategoryRepository interestEventCategoryRepository;
    private final TruckManagerRepository truckManagerRepository;
    private final TruckService truckService;
    private final EventService eventService;
    private final SocialTokenVerifier socialTokenVerifier;
    private final AuthCodeExchanger authCodeExchanger;
    private final OAuthRevokeService oauthRevokeService;
    private final RegionDoRepository regionDoRepository;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;
    private final CategoryRepository categoryRepository;

    private Member getMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    @Transactional
    public void registerMember(RequestMember.RegisterMemberDto registerMemberDto) {

        Member member = memberRepository.findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(registerMemberDto.getSocialInfoDto().getType(), registerMemberDto.getSocialInfoDto().getId());

        if (member != null) {
            throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_LOGIN_INFO_EXISTS);
        }

        member = memberRepository.findByPhone(registerMemberDto.getPhone());
        if (member != null) {
            throw new CustomException(MemberErrorCode.MEMBER_PHONE_EXISTS);
        }

        member = memberRepository.findByNickname(registerMemberDto.getNickname());
        if (member != null) {
            throw new CustomException(MemberErrorCode.MEMBER_NICKNAME_EXISTS);
        }

        member = registerMemberDto.toEntity();
        memberRepository.save(member);
    }

    @Transactional
    public void registerTemporary(RequestMember.RegisterTemporaryDto registerTemporaryDto) {

        Member member = memberRepository.findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(registerTemporaryDto.getSocialInfoDto().getType(), registerTemporaryDto.getSocialInfoDto().getId());
        if (member != null) {
            throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_LOGIN_INFO_EXISTS);
        }
        if (!registerTemporaryDto.getSocialInfoDto().getType().equals(SocialLoginType.UNREGISTERED)) {
            throw new CustomException(MemberErrorCode.ONLY_UNREGISTERED_ALLOWED);
        }

        member = registerTemporaryDto.toEntity();
        member.getTypes().remove(MemberType.NORMAL);
        member.getTypes().add(MemberType.UNREGISTERED);
        memberRepository.save(member);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto loginTemporary(RequestMember.LoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(loginMemberRqDto.getSocialInfoDto().getType(), loginMemberRqDto.getSocialInfoDto().getId());
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
        return ResponseMember.LoginMemberRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto backOfficeLoginMember(RequestMember.BackOfficeLoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(loginMemberRqDto.getSocialInfoDto().getType(), loginMemberRqDto.getSocialInfoDto().getId());
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);

        String verifiedSocialId = socialTokenVerifier.verify(loginMemberRqDto.getSocialInfoDto().getType(), loginMemberRqDto.getIdentityToken());
        if (!Objects.equals(verifiedSocialId, loginMemberRqDto.getSocialInfoDto().getId())) {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }

        if (!member.getTypes().contains(MemberType.ADMIN)) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_ADMIN);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
        return ResponseMember.LoginMemberRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto v2loginMember(RequestMember.V2LoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo_TypeAndSocialLoginInfo_Id(loginMemberRqDto.getSocialInfoDto().getType(), loginMemberRqDto.getSocialInfoDto().getId());
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);

        String verifiedSocialId = socialTokenVerifier.verify(loginMemberRqDto.getSocialInfoDto().getType(), loginMemberRqDto.getIdentityToken());
        if (!Objects.equals(verifiedSocialId, loginMemberRqDto.getSocialInfoDto().getId())) {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }

        if (loginMemberRqDto.getSocialInfoDto().getType().equals(SocialLoginType.APPLE) && Objects.equals(loginMemberRqDto.getPlatform(), RequestMember.PlatformType.IOS)) {
            if (Objects.equals(loginMemberRqDto.getAuthorizationCode(), null) || loginMemberRqDto.getAuthorizationCode().isEmpty()) {
                throw new CustomException(MemberErrorCode.AUTH_CODE_EMPTY);
            }
            String oauthRefreshToken = authCodeExchanger.exchangeToken(loginMemberRqDto.getAuthorizationCode());
            SocialLoginInfo socialLoginInfo = member.getSocialLoginInfo();
            socialLoginInfo.setAppleRefreshToken(oauthRefreshToken);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
        return ResponseMember.LoginMemberRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseMember.ReissueTokenDto reissueToken(String refreshToken) {
        Member member = getMember();
        String accessToken;
        if (member.matchRefreshToken(refreshToken)) {
            accessToken = jwtTokenProvider.createAccessToken(member.getId());
            if (jwtTokenProvider.checkExpiry(refreshToken)) {
                refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
                member.updateRefreshToken(refreshToken);
                memberRepository.save(member);
            }
            return ResponseMember.ReissueTokenDto.toDto(accessToken, refreshToken);
        } else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public ResponseMember.GetInfoDto getInfo() {
        Member member = getMember();
        return ResponseMember.GetInfoDto.toDto(member, imageManager);
    }

    @Transactional
    public ResponseMember.GenerateNicknameDto generateNickname() {
        String nickname = generateNicknameService.generationNickname();
        return ResponseMember.GenerateNicknameDto.toDto(nickname);
    }

    @Transactional
    public ResponseMember.CheckNicknameDto checkNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);
        boolean isNicknameUsable = false;
        if (member == null) {
            isNicknameUsable = true;
        }
        return ResponseMember.CheckNicknameDto.toDto(isNicknameUsable);
    }

    @Transactional
    public ResponseMember.CheckPhoneDto checkPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        if (member != null) {
            return ResponseMember.CheckPhoneDto.toDto(member.getSocialLoginInfo());
        }
        return ResponseMember.CheckPhoneDto.toDto(new SocialLoginInfo(SocialLoginType.valueOf("NONE"), "null"));
    }

    @Transactional
    public void updatePhone(String phone) {
        Member member = getMember();
        if (phone != null && !phone.isEmpty()) {
            member.updatePhone(phone);
        } else throw new CustomException(MemberErrorCode.MEMBER_PHONE_EMPTY);
        memberRepository.save(member);
    }

    @Transactional
    public void updateProfile(RequestMember.UpdateProfileRqDto updateProfileRqDto) {
        Member member = getMember();
        if (updateProfileRqDto.getImage() == null) {
            member.updateProfile(updateProfileRqDto.getNickname(), null);
        } else {
            File file = fileRepository.findById(updateProfileRqDto.getImage())
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_PROFILE_NOT_FOUND));
            member.updateProfile(updateProfileRqDto.getNickname(), file);
        }
        memberRepository.save(member);
    }

    @Transactional
    public void logoutMember(String refreshToken) {
        Member member = getMember();
        if (member.matchRefreshToken(refreshToken)) {
            member.updateRefreshToken(null);
            member.updateFcmToken(null);
            memberRepository.save(member);
        } else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public void deleteMember(String refreshToken) {
        Member member = getMember();
        if (!member.matchRefreshToken(refreshToken)) {
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
        }
        List<TruckLike> truckLikeList = truckLikeRepository.findAllByMemberId(member.getId());
        if (truckLikeList != null) {
            truckLikeList.forEach(truckLikeRepository::delete);
        }
        List<EventLike> eventLikeList = eventLikeRepository.findAllByMemberId(member.getId());
        if (eventLikeList != null) {
            eventLikeList.forEach(eventLikeRepository::delete);
        }
        List<TruckManager> truckManagerList = truckManagerRepository.findAllByMember(member);
        if (truckManagerList != null) {
            truckManagerList.forEach(truckManager -> {
                if (truckManager.getRole().equals(TruckManagerRole.OWNER)) {
                    truckService.deleteTruck(truckManager.getTruck().getId(), true);
                }
                truckManagerRepository.delete(truckManager);
            });
        }
        List<Event> eventList = eventRepository.findAllByCreatedBy(member.getId());
        if (eventList != null) {
            eventList.forEach(event -> eventService.deleteEvent(event.getId()));
        }
        member.delete();
        memberRepository.save(member);
    }

    //todo 회원탈퇴 시 유저 프로필 사진 삭제 처리 필요
    //todo 이미지 삭제 시에 우리 db에서만 미는 게 아니라 s3에 등록되어있는 파일도 삭제 처리 되는 건지 확인 필요
    @Transactional
    public void v2deleteMember(String refreshToken) {
        Member member = getMember();
        if (!member.matchRefreshToken(refreshToken)) {
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
        }

        List<TruckManager> truckManagerList = truckManagerRepository.findAllByMember(member);
        if (truckManagerList != null) {
            truckManagerList.forEach(truckManager -> {
                if (truckManager.getRole().equals(TruckManagerRole.OWNER)) {
                    truckService.deleteTruck(truckManager.getTruck().getId(), true);
                }
                truckManagerRepository.delete(truckManager);
            });
        }

        List<Event> eventList = eventRepository.findAllByCreatedBy(member.getId());
        if (eventList != null) {
            eventList.forEach(event -> eventService.deleteEvent(event.getId()));
        }

        List<TruckLike> truckLikeList = truckLikeRepository.findAllByMemberId(member.getId());
        if (truckLikeList != null) {
            truckLikeList.forEach(truckLikeRepository::delete);
        }
        List<EventLike> eventLikeList = eventLikeRepository.findAllByMemberId(member.getId());
        if (eventLikeList != null) {
            eventLikeList.forEach(eventLikeRepository::delete);
        }

        InterestEvent interestEvent = interestEventRepository.findByMember(member);
        if (interestEvent != null) {
            interestEventRegionRepository.deleteAllByInterestEvent(interestEvent);
            interestEventCategoryRepository.deleteAllByInterestEvent(interestEvent);
            interestEventRepository.delete(interestEvent);
        }

        try {
            SocialLoginInfo socialLoginInfo = member.getSocialLoginInfo();
            switch (socialLoginInfo.getType()) {
                case APPLE -> oauthRevokeService.revokeAppleAccess(socialLoginInfo.getAppleRefreshToken());
                case KAKAO -> oauthRevokeService.unlinkKakaoAccess(socialLoginInfo.getId());
            }
        } catch (Exception e) {
            System.err.println("[WITHDRAW] revoke failed: " + member.getSocialLoginInfo().getType() + " / " + e.getMessage());
        }

        member.delete();
        memberRepository.save(member);
    }

    @Transactional
    public void setFcmToken(String fcmToken) {
        Member member = getMember();
        member.updateFcmToken(fcmToken);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public ResponseMember.InterestEventDto getInterestEvent() {
        Member member = getMember();
        InterestEvent interestEvent = interestEventRepository.findByMember(member);
        if (interestEvent == null) {
            return ResponseMember.InterestEventDto.toDto(Set.of(), Set.of());
        }

        Set<String> regionCodeSet = new HashSet<>();
        interestEvent.getRegions().forEach(region ->
                regionCodeSet.add(region.getRegionType().makeCode(region.getRegionId())));

        Set<String> categoryCodeSet = new HashSet<>();
        interestEvent.getCategories().forEach(category ->
                categoryCodeSet.add(category.getCategory().getCode()));

        return ResponseMember.InterestEventDto.toDto(regionCodeSet, categoryCodeSet);
    }

    @Transactional
    public void setInterestEvent(RequestMember.SetInterestEventDto setInterestEventDto) {
        Member member = getMember();
        InterestEvent interestEvent = interestEventRepository.findByMember(member);
        if (interestEvent == null) {
            interestEvent = InterestEvent.builder()
                    .member(member)
                    .build();
            interestEvent = interestEventRepository.save(interestEvent);
        } else {
            interestEventRegionRepository.deleteAllByInterestEvent(interestEvent);
            interestEventCategoryRepository.deleteAllByInterestEvent(interestEvent);
        }

        InterestEvent savedInterestEvent = interestEvent;

        if (!Objects.equals(setInterestEventDto.getRegionCodeSet(), null)) {
            List<RegionDo> regionDos = regionDoRepository.findAll();
            List<RegionSi> regionSis = regionSiRepository.findAll();
            List<RegionGu> regionGus = regionGuRepository.findAll();
            List<RegionGun> regionGuns = regionGunRepository.findAll();
            RegionSearchProcessor regionSearchProcessor = new RegionSearchProcessor(regionDos, regionSis, regionGus, regionGuns);

            List<InterestEventRegion> regions = setInterestEventDto.getRegionCodeSet().stream()
                    .map(regionCode -> {
                        RegionInfo regionInfo = regionSearchProcessor.findByCode(regionCode);
                        return InterestEventRegion.builder()
                                .regionType(regionInfo.getRegionType())
                                .regionId(regionInfo.getRegionId())
                                .interestEvent(savedInterestEvent)
                                .build();
                    })
                    .toList();
            interestEventRegionRepository.saveAll(regions);
        }

        if (!Objects.equals(setInterestEventDto.getCategoryCodeSet(), null)) {
            List<InterestEventCategory> categories = setInterestEventDto.getCategoryCodeSet().stream()
                    .map(categoryCode -> {
                        Category category = categoryRepository.findByCode(categoryCode);
                        if (category == null) {
                            throw new CustomException(EventErrorCode.EVENT_CATEGORY_NOT_FOUND);
                        }
                        return InterestEventCategory.builder()
                                .interestEvent(savedInterestEvent)
                                .category(category)
                                .build();
                    })
                    .toList();
            interestEventCategoryRepository.saveAll(categories);
        }
    }

    @Transactional
    public void likeTruck(String truckId) {
        Member member = getMember();
        Truck truck = truckRepository.findById(truckId).orElseThrow(() -> new CustomException(TruckErrorCode.NOT_FOUND_TRUCK));
        TruckLike truckLike = truckLikeRepository.findByMemberIdAndTruckId(member.getId(), truckId);
        if (truckLike == null) {
            truckLike = TruckLike.builder()
                    .truck(truck)
                    .member(member)
                    .build();
            truckLikeRepository.save(truckLike);
        } else {
            truckLikeRepository.delete(truckLike);
        }
    }

    @Transactional
    public void likeEvent(String eventId) {
        Member member = getMember();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new CustomException(EventErrorCode.NOT_FOUND_EVENT));
        EventLike eventLike = eventLikeRepository.findByMemberIdAndEventId(member.getId(), eventId);
        if (eventLike == null) {
            eventLike = EventLike.builder()
                    .event(event)
                    .member(member)
                    .build();
            eventLikeRepository.save(eventLike);
        } else {
            eventLikeRepository.delete(eventLike);
        }
    }

    @Transactional
    public void setServiceType(RequestMember.SetServiceTypeDto setServiceTypeDto) {
        Member member = getMember();
        member.updateServiceType(setServiceTypeDto.getServiceType());
    }

    @Transactional(readOnly = true)
    public ResponseMember.ServiceTypeDto getServiceType(){
        Member member = getMember();
        return ResponseMember.ServiceTypeDto.toDto(member.getServiceType());


    }
}
