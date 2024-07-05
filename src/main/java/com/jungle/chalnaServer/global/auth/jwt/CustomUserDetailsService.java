package com.jungle.chalnaServer.global.auth.jwt;

import com.jungle.chalnaServer.domain.auth.domain.entity.AuthInfo;
import com.jungle.chalnaServer.domain.auth.repository.AuthInfoRepository;
import com.jungle.chalnaServer.global.auth.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthInfoRepository authInfoRepository;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        AuthInfo authInfo = authInfoRepository.findById(Long.valueOf(id));
        if (authInfo == null)
            new UsernameNotFoundException("400");
        return new CustomUserDetails(authInfo);
    }
}
