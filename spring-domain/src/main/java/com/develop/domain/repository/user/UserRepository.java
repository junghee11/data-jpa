package com.develop.domain.repository.user;

import com.develop.domain.dto.user.UserDto;
import com.develop.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    UserDto findByUserId(String id);

    Optional<User> findOptionalByUserId(String id);

    User findByNickname(String nick);

    User findByPhone(String phone);

    User findByUserIdOrNicknameOrPhone(String id, String nick, String phone);

    Optional<User> findByNameAndPhone(String name, String phone);

    Optional<User> findByUserIdAndNameAndPhone(String id, String name, String phone);

    List<User> findByUserIdIn(Set<String> ids);

    List<UserDto> findByUserIdNotAndRoleIn(String id, String[] roles);

}
