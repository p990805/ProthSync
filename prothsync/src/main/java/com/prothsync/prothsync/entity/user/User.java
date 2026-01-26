package com.prothsync.prothsync.entity.user;

import com.prothsync.prothsync.common.BaseEntity;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 20)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 10)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private User(String userName,
        String password,
        String nickName,
        LocalDate birthday,
        String address,
        String email,
        UserType userType) {
        this.userName = userName;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.address = address;
        this.email = email;
        this.userType = userType;
        this.userRole = UserRole.USER;
    }

    public static User createUser(
        String userName,
        String password,
        String nickName,
        LocalDate birthday,
        String address,
        String email,
        UserType userType
    ) {
        validateUserName(userName);
        validatePassword(password);
        validateNickName(nickName);
        validateBirthday(birthday);
        validateAddress(address);
        validateEmail(email);
        validateUserType(userType);

        return new User(userName, password, nickName, birthday, address, email, userType);
    }

    private static void validateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new BusinessException(UserErrorCode.USERNAME_IS_NULL);
        }
        if (userName.length() > 20) {
            throw new BusinessException(UserErrorCode.USERNAME_TOO_LONG);
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException(UserErrorCode.PASSWORD_IS_NULL);
        }
    }

    private static void validateNickName(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            throw new BusinessException(UserErrorCode.NICKNAME_IS_NULL);
        }
        if (nickName.length() > 10) {
            throw new BusinessException(UserErrorCode.NICKNAME_TOO_LONG);
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new BusinessException(UserErrorCode.BIRTHDAY_IS_NULL);
        }

        if (birthday.isAfter(LocalDate.now())) {
            throw new BusinessException(UserErrorCode.BIRTHDAY_IS_FUTURE);
        }

        if (birthday.isBefore(LocalDate.now().minusYears(150))) {
            throw new BusinessException(UserErrorCode.BIRTHDAY_TOO_OLD);
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new BusinessException(UserErrorCode.ADDRESS_IS_NULL);
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(UserErrorCode.EMAIL_IS_NULL);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(UserErrorCode.EMAIL_INVALID_FORMAT);
        }
    }

    private static void validateUserType(UserType userType) {
        if (userType == null) {
            throw new BusinessException(UserErrorCode.USER_TYPE_IS_NULL);
        }
    }

    public void updatePassword(String newPassword) {
        validatePassword(newPassword);
        this.password = newPassword;
    }

    public void updateNickName(String newNickName) {
        validateNickName(newNickName);
        this.nickName = newNickName;
    }

    public void updateAddress(String newAddress) {
        validateAddress(newAddress);
        this.address = newAddress;
    }

    public void updateEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
    }

    public void promoteToAdmin() {
        this.userRole = UserRole.ADMIN;
    }

    public void demoteToUser() {
        this.userRole = UserRole.USER;
    }

    public boolean isAdmin() {
        return this.userRole == UserRole.ADMIN;
    }
}