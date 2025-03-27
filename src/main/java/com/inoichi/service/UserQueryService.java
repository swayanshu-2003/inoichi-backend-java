package com.inoichi.service;

import com.inoichi.db.model.User;
import com.inoichi.dto.UserQueryDTO;
import com.inoichi.db.model.UserQuery;
import com.inoichi.dto.UserQueryRequestDTO;
import com.inoichi.repository.UserQueryRepository;
import com.inoichi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserQueryService {

    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private UserRepository userRepository;
    public UserQueryService(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    public List<UserQueryDTO> getUserQueriesByUserId(UUID userId) {
        List<UserQuery> userQueries = userQueryRepository.findByUserId(userId);
        return userQueries.stream()
                .map(UserQueryDTO::new)
                .collect(Collectors.toList());
    }
    public UserQueryDTO saveUserQuery(UserQueryRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserQuery userQuery = new UserQuery();
        userQuery.setUser(user);
        userQuery.setQuery(request.getQuery());
        userQuery.setExtractedSymptoms(request.getExtractedSymptoms());

        userQueryRepository.save(userQuery);
        return new UserQueryDTO(userQuery);
    }
}
