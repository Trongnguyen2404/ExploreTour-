package com.vivu.api.security.services;

import com.vivu.api.entities.User;
import com.vivu.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Chỉ đọc, tối ưu hơn
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Tìm user theo email hoặc username (bỏ qua soft delete)
        // Vì user có thể đã bị soft delete nhưng vẫn cần xác thực (ví dụ: để khôi phục)
        // Hoặc tìm theo email/username đang active tùy logic của bạn
        // Ở đây ví dụ tìm user đang active
        User user = userRepository.findByEmailOrUsername(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username or email: " + usernameOrEmail));

        // Xây dựng UserDetails từ User entity tìm được
        return UserDetailsImpl.build(user);
    }
}