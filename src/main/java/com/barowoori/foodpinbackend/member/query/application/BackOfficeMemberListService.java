package com.barowoori.foodpinbackend.member.query.application;

import com.barowoori.foodpinbackend.member.command.application.dto.ResponseBackOfficeMember;
import com.barowoori.foodpinbackend.member.command.application.dto.SearchBackOfficeMemberDto;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BackOfficeMemberListService {

    private final MemberRepository memberRepository;

    public Page<ResponseBackOfficeMember.GetBackOfficeMemberListDto> findBackOfficeMemberList(String search, Boolean isDeleted, Pageable pageable) {
        return memberRepository.findBackOfficeMemberList(search, isDeleted, pageable)
                .map(ResponseBackOfficeMember.GetBackOfficeMemberListDto::toDto);
    }

    public Page<SearchBackOfficeMemberDto> searchBackOfficeMembersForTruckManager(String search, Pageable pageable) {
        return memberRepository.searchBackOfficeMembersForTruckManager(search, pageable)
                .map(SearchBackOfficeMemberDto::toDto);
    }
}
