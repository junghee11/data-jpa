package com.develop.datajpa.repository;

import com.develop.datajpa.dto.user.UserDto;
import com.develop.datajpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    UserDto findByUserId(String id);

    Optional<User> findOptionalByUserId(String id);

    User findByNickname(String nick);

    User findByPhone(String phone);

    User findByUserIdOrNicknameOrPhone(String id, String nick, String phone);

    User findByNameAndPhone(String name, String phone);

    User findByUserIdAndNameAndPhone(String id, String name, String phone);

    User findByUserIdAndPw(String id, String pw);

}
