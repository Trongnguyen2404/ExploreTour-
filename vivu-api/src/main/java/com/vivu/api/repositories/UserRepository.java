package com.vivu.api.repositories;

import com.vivu.api.entities.User;
import org.springframework.data.domain.Page; // Thêm import này
import org.springframework.data.domain.Pageable; // Thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm user đang active theo email (tự động áp dụng @Where(clause = "is_active = true"))
    Optional<User> findByEmail(String email);

    // Tìm user đang active theo username (tự động áp dụng @Where)
    Optional<User> findByUsername(String username);

    // Tìm user đang active theo email hoặc username (tự động áp dụng @Where)
    Optional<User> findByEmailOrUsername(String email, String username);

    // Kiểm tra tồn tại user active theo email
    boolean existsByEmail(String email);

    // Kiểm tra tồn tại user active theo username
    boolean existsByUsername(String username);

    // --- Các phương thức bỏ qua soft delete (@Where) ---

    // Lấy tất cả user, bỏ qua @Where clause (cho Admin)
    @Query(value = "SELECT u FROM User u",
            countQuery = "SELECT count(u) FROM User u") // Cần countQuery tương ứng
    Page<User> findAllIgnoringStatus(Pageable pageable);

    // Tìm user theo ID, bỏ qua @Where clause (cho Admin)
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdIgnoringStatus(@Param("userId") Integer userId);


    // Tìm user theo email, bất kể trạng thái active
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailIgnoringStatus(@Param("email") String email);

    // Tìm user theo username, bất kể trạng thái active
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameIgnoringStatus(@Param("username") String username);

    // Kiểm tra tồn tại user theo email, bất kể trạng thái active
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmailIgnoringStatus(@Param("email") String email);

    // Kiểm tra tồn tại user theo username, bất kể trạng thái active
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsernameIgnoringStatus(@Param("username") String username);
}