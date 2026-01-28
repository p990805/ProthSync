package com.prothsync.prothsync.service;

import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.exception.BusinessException;
import com.prothsync.prothsync.exception.UserErrorCode;
import com.prothsync.prothsync.repository.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(SignupRequestDTO signupRequest){

        validateDuplicateUserName(signupRequest.userName());
        validateDuplicateNickName(signupRequest.nickName());
        validateDuplicateEmail(signupRequest.email());

        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        User user = User.createUser(
            signupRequest.userName(),
            encodedPassword,
            signupRequest.nickName(),
            signupRequest.birthday(),
            signupRequest.address(),
            signupRequest.email(),
            signupRequest.userType()
        );

        return userRepository.save(user);
    }

    private void validateDuplicateUserName(String userName) {
        if (userRepository.existsByUserName(userName)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateNickName(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

}
