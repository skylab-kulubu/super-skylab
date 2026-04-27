package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.User.PromoteUserRequest;
import com.skylab.superapp.entities.DTOs.User.UpdateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Kullanıcı Yönetimi", description = "Kullanıcı profilleri, yetkilendirmeler ve LDAP entegrasyon işlemleri")
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('users.me', 'users.moderator')")
    @Operation(summary = "Aktif Kullanıcı Bilgilerini Getir", description = "Sisteme giriş yapmış kullanıcının profil verilerini döner.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri başarıyla getirildi."),
            @ApiResponse(responseCode = "401", description = "Yetkilendirme hatası.", content = @Content)
    })
    public ResponseEntity<DataResult<UserDto>> getAuthenticatedUser() {
     var result = userService.getAuthenticatedUser();
     return ResponseEntity.status(HttpStatus.OK)
             .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS,
                     HttpStatus.OK));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('users.me', 'users.moderator')")
    @Operation(summary = "Aktif Kullanıcı Profilini Güncelle", description = "Sisteme giriş yapmış kullanıcının kendi profil bilgilerini güncellemesini sağlar.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı profili güncellendi.")
    })
    public ResponseEntity<DataResult<UserDto>> updateAuthenticatedUser(@RequestBody UpdateUserRequest updateUserRequest) {
       var result =userService.updateAuthenticatedUser(updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS, HttpStatus.OK));
    }

    @PostMapping("/me/profile-picture")
    @PreAuthorize("hasAnyRole('users.me', 'users.moderator')")
    @Operation(summary = "Profil Fotoğrafı Yükle", description = "Kullanıcının profil fotoğrafını skycdn'e yükler ve kaydeder.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Result> updateProfilePicture(@RequestParam("image") MultipartFile image) {
        userService.uploadProfilePictureOfAuthenticatedUser(image);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResult(UserMessages.PROFILE_PICTURE_UPDATED_SUCCESS, HttpStatus.OK));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('users.list', 'users.moderator')")
    @Operation(summary = "Tüm Kullanıcıları Getir", description = "Sistemdeki tüm kullanıcıları listeler. E-posta ve rol bazlı filtreleme yapılabilir.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DataResult<List<UserDto>>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) List<String> roles) {

        var result = userService.getAllUsers(email, roles);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USERS_LISTED_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('users.get', 'users.moderator')")
    @Operation(summary = "Kullanıcı Detayını Getir", description = "Belirtilen UUID'ye sahip kullanıcının detaylarını döner.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı detayları getirildi."),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı.", content = @Content)
    })
    public ResponseEntity<DataResult<UserDto>> getUserById(@PathVariable UUID id) {
        var result = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_GET_SUCCESS, HttpStatus.OK));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('users.update', 'users.moderator')")
    @Operation(summary = "Kullanıcı Güncelle (Admin)", description = "Yetkili personelin belirtilen kullanıcının verilerini güncellemesini sağlar.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DataResult<UserDto>> updateUserById(@PathVariable UUID id,
                                                 @RequestBody UpdateUserRequest updateUserRequest) {
        var result = userService.updateUser(id, updateUserRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(result, UserMessages.USER_UPDATED_SUCCESS,
                        HttpStatus.OK));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('users.delete', 'users.moderator')")
    @Operation(summary = "Kullanıcı Sil", description = "Belirtilen kullanıcıyı sistemden siler. LDAP bağlantısı varsa LDAP üzerinden de silinir.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Result> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_DELETED_SUCCESS, HttpStatus.OK));
    }


    @PostMapping("/{id}/promote")
    @PreAuthorize("hasAnyRole('users.promote', 'users.moderator')")
    @Operation(summary = "Kullanıcıyı LDAP Üyesine Terfi Ettir", description = "Normal bir uygulamaya kayıtlı kullanıcıyı LDAP dizinine ekler.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı başarıyla LDAP üyesi yapıldı."),
            @ApiResponse(responseCode = "400", description = "Kullanıcı zaten LDAP üyesi.", content = @Content)
    })
    public ResponseEntity<Result> promoteUserToLdap(@PathVariable UUID id, @RequestBody PromoteUserRequest promoteUserRequest){

        userService.promoteUserToLdap(id, promoteUserRequest.getTargetRole(), promoteUserRequest.getInitialPassword());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(UserMessages.USER_PROMOTED_SUCCESS, HttpStatus.OK));


    }
}
