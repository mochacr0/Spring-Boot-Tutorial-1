package com.example.tutorial.service;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.User;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.validator.DataValidator;
import com.example.tutorial.common.validator.UserDataValidator;
import com.example.tutorial.model.UserEntity;
import com.example.tutorial.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends BaseService<User, UserEntity> implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataValidator<User> dataValidator;
    @Override
    public JpaRepository<UserEntity, UUID> getRepository() {
        return this.userRepository;
    }

    @Override
    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Override
//    public Class<User> getDataClass() {
//        return User.class;
//    }

    @Override
    public PageData<User> findUsers(PageParameter pageParameter) {
        log.info("Performing service findUsers");
        validatePageParameter(pageParameter);
        return DaoUtils.toPageData(userRepository.findAll(DaoUtils.toPageable(pageParameter)));
    }

    @Override
    public User saveUser(User user) {
        log.info("Performing service saveUser");
        dataValidator.validate(user);
        if (!StringUtils.isEmpty(user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        return save(user);
    }

    @Override
    public User findUserByName(String name) {
        log.info("Performing service findUserByName");
        return DaoUtils.toData(userRepository.findByName(name));
    }

    @Override
    public User findUserById(UUID id) {
        log.info("Performing service findUserById");
        return DaoUtils.toData(userRepository.findById(id));
    }

    @Override
    public User findUserByEmail(String email) {
        log.info("Performing service findUserByEmail");
        return DaoUtils.toData(userRepository.findByEmail(email));
    }
}
