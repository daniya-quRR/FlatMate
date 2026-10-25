package com.kz.flatmate.repository;

import com.kz.flatmate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Сигнатура метода для поиска пользователя по его электронной почте
    User findByEmail(String email);
}
