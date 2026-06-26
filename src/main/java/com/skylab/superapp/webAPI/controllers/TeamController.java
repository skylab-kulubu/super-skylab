package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.team.LocalizedText;
import com.skylab.superapp.entities.DTOs.team.TeamMemberDto;
import com.skylab.superapp.entities.DTOs.team.TeamMembersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Takim (Keycloak grup) uyeligi listeleme.
 *
 * GUVENLIK: Yalnizca Keycloak'ta 'public_listing=true' attribute'una sahip takimlar listelenir.
 * Allowlist app config'de DEGIL, dogrudan Keycloak'ta yonetilir (tek dogruluk kaynagi).
 * Yapisal/yetkili gruplar (UYELER, ADMIN, YK...) flag tasimadigi icin 404 alir.
 * Donen alanlar PII icermez (email/skyNumber yok).
 */
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Takım Yönetimi", description = "public_listing işaretli takımların üyelerini/liderlerini listeleme (PII'siz).")
public class TeamController {

    private final UserService userService;

    @GetMapping("/{team}/members")
    @Operation(
            summary = "Takım Üyelerini Getir",
            description = "Takımın tüm üyelerini (alt gruplar dahil) listeler; her üyede 'leader' bayrağı vardır. " +
                    "Yalnızca güvenli alanlar döner. İşaretsiz/yapısal gruplar için 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Üyeler listelendi."),
            @ApiResponse(responseCode = "404", description = "Takım bulunamadı veya listelenemez.")
    })
    public ResponseEntity<DataResult<TeamMembersResponse>> getTeamMembers(
            @Parameter(description = "Takım adı", example = "WEBLAB") @PathVariable String team) {

        List<UserDto> users = userService.getPublicTeamMembers(team);
        Set<UUID> leaderIds = userService.getTeamLeaderIds(team);

        TeamMembersResponse response = buildResponse(team, users, leaderIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(response, UserMessages.USERS_LISTED_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{team}/leaders")
    @Operation(
            summary = "Takım Liderlerini Getir",
            description = "Yalnızca takımın lider alt grubundaki (LIDERLER/KOORDINATORLER) üyeleri listeler. " +
                    "İşaretsiz/yapısal gruplar için 404."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liderler listelendi."),
            @ApiResponse(responseCode = "404", description = "Takım bulunamadı veya listelenemez.")
    })
    public ResponseEntity<DataResult<TeamMembersResponse>> getTeamLeaders(
            @Parameter(description = "Takım adı", example = "WEBLAB") @PathVariable String team) {

        List<UserDto> leaders = userService.getPublicTeamLeaders(team);
        Set<UUID> leaderIds = userService.getTeamLeaderIds(team);

        TeamMembersResponse response = buildResponse(team, leaders, leaderIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(response, UserMessages.USERS_LISTED_SUCCESS, HttpStatus.OK));
    }

    // ---- ortak: attribute'lardan çok dilli etiket + güvenli üye DTO'su kurar ----
    private TeamMembersResponse buildResponse(String team, List<UserDto> users, Set<UUID> leaderIds) {
        Map<String, String> attrs = userService.getTeamAttributes(team);

        LocalizedText displayName = new LocalizedText(
                attrs.getOrDefault("display_name_tr", team),
                attrs.get("display_name_en"));
        LocalizedText description = new LocalizedText(
                attrs.get("description_tr"),
                attrs.get("description_en"));

        List<TeamMemberDto> members = users.stream()
                .map(u -> new TeamMemberDto(
                        u.getFirstName(),
                        u.getLastName(),
                        u.getProfilePictureUrl(),
                        u.getLinkedin(),
                        u.getUniversity(),
                        u.getFaculty(),
                        u.getDepartment(),
                        leaderIds.contains(u.getId())))
                .toList();

        return new TeamMembersResponse(team, displayName, description, members.size(), members);
    }
}
