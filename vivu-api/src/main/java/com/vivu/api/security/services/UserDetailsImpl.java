package com.vivu.api.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vivu.api.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter // Lombok getter
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Integer id;
    private final String username; // Sử dụng username của User entity
    private final String email;

    @JsonIgnore // Không trả về password trong response
    private final String password;

    private final String fullName;
    private final LocalDate dateOfBirth;
    private final String phoneNumber;
    private final String profilePictureUrl;
    private final boolean isActive; // Lấy trạng thái active từ User entity


    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor chính
    public UserDetailsImpl(Integer id, String username, String email, String password, String fullName,
                           LocalDate dateOfBirth, String phoneNumber, String profilePictureUrl,
                           boolean isActive, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username; // Gán username từ User entity
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    // Phương thức build để tạo UserDetailsImpl từ User entity
    public static UserDetailsImpl build(User user) {
        // Role được lấy từ user.getRole() và chuyển thành GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name()); // Prefix ROLE_ là convention

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(), // Dùng username từ entity
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getProfilePictureUrl(),
                user.isActive(), // Lấy trạng thái active
                Collections.singletonList(authority) // Tạo list chỉ chứa 1 role
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // Trả về email để xác thực (thường dùng email làm username trong hệ thống)
        // Hoặc bạn có thể trả về username nếu muốn đăng nhập bằng username
        return email;
        // return username; // Nếu muốn đăng nhập bằng username
    }

    // Các phương thức khác của UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true; // Logic hết hạn tài khoản nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive; // Tài khoản bị khóa nếu is_active = false
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Logic hết hạn mật khẩu nếu cần
    }

    @Override
    public boolean isEnabled() {
        return isActive; // Tài khoản được bật nếu is_active = true
    }

    // Override equals và hashCode để so sánh UserDetails
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id); // So sánh dựa trên ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}