package com.felipegc.booking.repositories;

import com.felipegc.booking.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    @Query(value="select * from tb_user where email = :email", nativeQuery = true)
    Optional<UserModel> findByEmail(@Param("email") String email);

    @Query(value="select * from tb_user where email = :email and password = :password", nativeQuery = true)
    UserModel findUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query(value="select * from tb_user where token = :token", nativeQuery = true)
    UserModel findUserByToken(@Param("token") UUID token);
}
