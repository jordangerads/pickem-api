package com.gci.pickem.service.security;

import com.gci.pickem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PickemUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    PickemUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.gci.pickem.data.User dbUser =
            userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("could not find user with email %s",username)));

        List<GrantedAuthority> authorities = new ArrayList<>();
        dbUser.getUserRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRole())));

        return new User(dbUser.getEmail(), dbUser.getPassword(), authorities);
    }
}
