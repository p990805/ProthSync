package com.prothsync.prothsync;

import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.entity.user.UserRole;
import com.prothsync.prothsync.entity.user.UserType;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User 엔티티 테스트")
class UserTest {

    private static final String VALID_USERNAME = "testuser123";
    private static final String VALID_PASSWORD = "password123!";
    private static final String VALID_NICKNAME = "테스트닉네임";
    private static final LocalDate VALID_BIRTHDAY = LocalDate.of(1990, 1, 1);
    private static final String VALID_ADDRESS = "서울시 강남구 테헤란로 123";
    private static final String VALID_EMAIL = "test@example.com";
    private static final UserType VALID_USER_TYPE = UserType.DENTIST;

    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTest {

        @Test
        @DisplayName("유효한 정보로 사용자를 생성할 수 있다")
        void createUser_WithValidData_Success() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user).isNotNull();
            assertThat(user.getUserName()).isEqualTo(VALID_USERNAME);
            assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
            assertThat(user.getNickName()).isEqualTo(VALID_NICKNAME);
            assertThat(user.getBirthday()).isEqualTo(VALID_BIRTHDAY);
            assertThat(user.getAddress()).isEqualTo(VALID_ADDRESS);
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
            assertThat(user.getUserType()).isEqualTo(VALID_USER_TYPE);
            assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        }

        @Test
        @DisplayName("생성된 사용자의 기본 권한은 USER이다")
        void createUser_DefaultRole_IsUser() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
            assertThat(user.isAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("사용자 이름 검증 테스트")
    class UserNameValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("사용자 이름이 null이거나 공백이면 예외가 발생한다")
        void createUser_WithNullOrBlankUserName_ThrowsException(String userName) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                userName,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USERNAME_IS_NULL);
        }

        @Test
        @DisplayName("사용자 이름이 20자를 초과하면 예외가 발생한다")
        void createUser_WithTooLongUserName_ThrowsException() {
            // given
            String tooLongUserName = "a".repeat(21);

            // when & then
            assertThatThrownBy(() -> User.createUser(
                tooLongUserName,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USERNAME_TOO_LONG);
        }

        @Test
        @DisplayName("사용자 이름이 20자일 때 생성에 성공한다")
        void createUser_WithMaxLengthUserName_Success() {
            // given
            String maxLengthUserName = "a".repeat(20);

            // when
            User user = User.createUser(
                maxLengthUserName,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getUserName()).isEqualTo(maxLengthUserName);
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class PasswordValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("비밀번호가 null이거나 공백이면 예외가 발생한다")
        void createUser_WithNullOrBlankPassword_ThrowsException(String password) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                password,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.PASSWORD_IS_NULL);
        }
    }

    @Nested
    @DisplayName("닉네임 검증 테스트")
    class NickNameValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("닉네임이 null이거나 공백이면 예외가 발생한다")
        void createUser_WithNullOrBlankNickName_ThrowsException(String nickName) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                nickName,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NICKNAME_IS_NULL);
        }

        @Test
        @DisplayName("닉네임이 10자를 초과하면 예외가 발생한다")
        void createUser_WithTooLongNickName_ThrowsException() {
            // given
            String tooLongNickName = "닉".repeat(11);

            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                tooLongNickName,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NICKNAME_TOO_LONG);
        }

        @Test
        @DisplayName("닉네임이 10자일 때 생성에 성공한다")
        void createUser_WithMaxLengthNickName_Success() {
            // given
            String maxLengthNickName = "닉".repeat(10);

            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                maxLengthNickName,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getNickName()).isEqualTo(maxLengthNickName);
        }
    }

    @Nested
    @DisplayName("생년월일 검증 테스트")
    class BirthdayValidationTest {

        @Test
        @DisplayName("생년월일이 null이면 예외가 발생한다")
        void createUser_WithNullBirthday_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                null,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BIRTHDAY_IS_NULL);
        }

        @Test
        @DisplayName("생년월일이 미래 날짜면 예외가 발생한다")
        void createUser_WithFutureBirthday_ThrowsException() {
            // given
            LocalDate futureBirthday = LocalDate.now().plusDays(1);

            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                futureBirthday,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BIRTHDAY_IS_FUTURE);
        }

        @Test
        @DisplayName("생년월일이 150년 이전이면 예외가 발생한다")
        void createUser_WithTooOldBirthday_ThrowsException() {
            // given
            LocalDate tooOldBirthday = LocalDate.now().minusYears(151);

            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                tooOldBirthday,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.BIRTHDAY_TOO_OLD);
        }

        @Test
        @DisplayName("생년월일이 오늘 날짜일 때 생성에 성공한다")
        void createUser_WithTodayBirthday_Success() {
            // given
            LocalDate todayBirthday = LocalDate.now();

            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                todayBirthday,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getBirthday()).isEqualTo(todayBirthday);
        }

        @Test
        @DisplayName("생년월일이 150년 전일 때 생성에 성공한다")
        void createUser_With150YearsAgoBirthday_Success() {
            // given
            LocalDate oldBirthday = LocalDate.now().minusYears(150);

            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                oldBirthday,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getBirthday()).isEqualTo(oldBirthday);
        }
    }

    @Nested
    @DisplayName("주소 검증 테스트")
    class AddressValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("주소가 null이거나 공백이면 예외가 발생한다")
        void createUser_WithNullOrBlankAddress_ThrowsException(String address) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                address,
                VALID_EMAIL,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.ADDRESS_IS_NULL);
        }
    }

    @Nested
    @DisplayName("이메일 검증 테스트")
    class EmailValidationTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("이메일이 null이거나 공백이면 예외가 발생한다")
        void createUser_WithNullOrBlankEmail_ThrowsException(String email) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                email,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_IS_NULL);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@invalid.com",
            "invalid@.com",
            "invalid@com",
            "invalid..email@example.com",
            "invalid @example.com",
            "invalid@example .com"
        })
        @DisplayName("이메일 형식이 올바르지 않으면 예외가 발생한다")
        void createUser_WithInvalidEmailFormat_ThrowsException(String invalidEmail) {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                invalidEmail,
                VALID_USER_TYPE
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_INVALID_FORMAT);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.kr",
            "user_name@example-site.com",
            "123@example.com",
            "user@subdomain.example.com"
        })
        @DisplayName("올바른 이메일 형식으로 생성에 성공한다")
        void createUser_WithValidEmailFormat_Success(String validEmail) {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                validEmail,
                VALID_USER_TYPE
            );

            // then
            assertThat(user.getEmail()).isEqualTo(validEmail);
        }
    }

    @Nested
    @DisplayName("사용자 타입 검증 테스트")
    class UserTypeValidationTest {

        @Test
        @DisplayName("사용자 타입이 null이면 예외가 발생한다")
        void createUser_WithNullUserType_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                null
            ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_TYPE_IS_NULL);
        }

        @Test
        @DisplayName("치과의사 타입으로 생성에 성공한다")
        void createUser_WithDentistType_Success() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                UserType.DENTIST
            );

            // then
            assertThat(user.getUserType()).isEqualTo(UserType.DENTIST);
        }

        @Test
        @DisplayName("기공소장 타입으로 생성에 성공한다")
        void createUser_WithDentalLabOwnerType_Success() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                UserType.DENTAL_LAB_OWNER
            );

            // then
            assertThat(user.getUserType()).isEqualTo(UserType.DENTAL_LAB_OWNER);
        }

        @Test
        @DisplayName("기공사 타입으로 생성에 성공한다")
        void createUser_WithDentalTechnicianType_Success() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                UserType.DENTAL_TECHNICIAN
            );

            // then
            assertThat(user.getUserType()).isEqualTo(UserType.DENTAL_TECHNICIAN);
        }

        @Test
        @DisplayName("학생 타입으로 생성에 성공한다")
        void createUser_WithStudentType_Success() {
            // when
            User user = User.createUser(
                VALID_USERNAME,
                VALID_PASSWORD,
                VALID_NICKNAME,
                VALID_BIRTHDAY,
                VALID_ADDRESS,
                VALID_EMAIL,
                UserType.STUDENT
            );

            // then
            assertThat(user.getUserType()).isEqualTo(UserType.STUDENT);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class UpdatePasswordTest {

        @Test
        @DisplayName("유효한 비밀번호로 변경에 성공한다")
        void updatePassword_WithValidPassword_Success() {
            // given
            User user = createValidUser();
            String newPassword = "newPassword123!";

            // when
            user.updatePassword(newPassword);

            // then
            assertThat(user.getPassword()).isEqualTo(newPassword);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("null이거나 공백인 비밀번호로 변경 시 예외가 발생한다")
        void updatePassword_WithNullOrBlankPassword_ThrowsException(String newPassword) {
            // given
            User user = createValidUser();

            // when & then
            assertThatThrownBy(() -> user.updatePassword(newPassword))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.PASSWORD_IS_NULL);
        }
    }

    @Nested
    @DisplayName("닉네임 변경 테스트")
    class UpdateNickNameTest {

        @Test
        @DisplayName("유효한 닉네임으로 변경에 성공한다")
        void updateNickName_WithValidNickName_Success() {
            // given
            User user = createValidUser();
            String newNickName = "새닉네임";

            // when
            user.updateNickName(newNickName);

            // then
            assertThat(user.getNickName()).isEqualTo(newNickName);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("null이거나 공백인 닉네임으로 변경 시 예외가 발생한다")
        void updateNickName_WithNullOrBlankNickName_ThrowsException(String newNickName) {
            // given
            User user = createValidUser();

            // when & then
            assertThatThrownBy(() -> user.updateNickName(newNickName))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NICKNAME_IS_NULL);
        }

        @Test
        @DisplayName("10자를 초과하는 닉네임으로 변경 시 예외가 발생한다")
        void updateNickName_WithTooLongNickName_ThrowsException() {
            // given
            User user = createValidUser();
            String tooLongNickName = "닉".repeat(11);

            // when & then
            assertThatThrownBy(() -> user.updateNickName(tooLongNickName))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NICKNAME_TOO_LONG);
        }
    }

    @Nested
    @DisplayName("주소 변경 테스트")
    class UpdateAddressTest {

        @Test
        @DisplayName("유효한 주소로 변경에 성공한다")
        void updateAddress_WithValidAddress_Success() {
            // given
            User user = createValidUser();
            String newAddress = "부산시 해운대구 센텀로 123";

            // when
            user.updateAddress(newAddress);

            // then
            assertThat(user.getAddress()).isEqualTo(newAddress);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("null이거나 공백인 주소로 변경 시 예외가 발생한다")
        void updateAddress_WithNullOrBlankAddress_ThrowsException(String newAddress) {
            // given
            User user = createValidUser();

            // when & then
            assertThatThrownBy(() -> user.updateAddress(newAddress))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.ADDRESS_IS_NULL);
        }
    }

    @Nested
    @DisplayName("이메일 변경 테스트")
    class UpdateEmailTest {

        @Test
        @DisplayName("유효한 이메일로 변경에 성공한다")
        void updateEmail_WithValidEmail_Success() {
            // given
            User user = createValidUser();
            String newEmail = "newemail@example.com";

            // when
            user.updateEmail(newEmail);

            // then
            assertThat(user.getEmail()).isEqualTo(newEmail);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("null이거나 공백인 이메일로 변경 시 예외가 발생한다")
        void updateEmail_WithNullOrBlankEmail_ThrowsException(String newEmail) {
            // given
            User user = createValidUser();

            // when & then
            assertThatThrownBy(() -> user.updateEmail(newEmail))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_IS_NULL);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@invalid.com",
            "invalid@.com",
            "invalid@com"
        })
        @DisplayName("잘못된 형식의 이메일로 변경 시 예외가 발생한다")
        void updateEmail_WithInvalidEmailFormat_ThrowsException(String newEmail) {
            // given
            User user = createValidUser();

            // when & then
            assertThatThrownBy(() -> user.updateEmail(newEmail))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.EMAIL_INVALID_FORMAT);
        }
    }

    @Nested
    @DisplayName("권한 관리 테스트")
    class RoleManagementTest {

        @Test
        @DisplayName("사용자를 관리자로 승격할 수 있다")
        void promoteToAdmin_Success() {
            // given
            User user = createValidUser();
            assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
            assertThat(user.isAdmin()).isFalse();

            // when
            user.promoteToAdmin();

            // then
            assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("관리자를 일반 사용자로 강등할 수 있다")
        void demoteToUser_Success() {
            // given
            User user = createValidUser();
            user.promoteToAdmin();
            assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.isAdmin()).isTrue();

            // when
            user.demoteToUser();

            // then
            assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
            assertThat(user.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("isAdmin() 메서드는 ADMIN 권한일 때 true를 반환한다")
        void isAdmin_WithAdminRole_ReturnsTrue() {
            // given
            User user = createValidUser();
            user.promoteToAdmin();

            // when
            boolean isAdmin = user.isAdmin();

            // then
            assertThat(isAdmin).isTrue();
        }

        @Test
        @DisplayName("isAdmin() 메서드는 USER 권한일 때 false를 반환한다")
        void isAdmin_WithUserRole_ReturnsFalse() {
            // given
            User user = createValidUser();

            // when
            boolean isAdmin = user.isAdmin();

            // then
            assertThat(isAdmin).isFalse();
        }
    }

    // Helper method
    private User createValidUser() {
        return User.createUser(
            VALID_USERNAME,
            VALID_PASSWORD,
            VALID_NICKNAME,
            VALID_BIRTHDAY,
            VALID_ADDRESS,
            VALID_EMAIL,
            VALID_USER_TYPE
        );
    }
}
