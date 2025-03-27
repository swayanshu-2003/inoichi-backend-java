package com.inoichi.service;

import com.inoichi.db.model.User;
import com.inoichi.dto.UserQueryDTO;
import com.inoichi.db.model.UserQuery;
import com.inoichi.dto.UserQueryRequestDTO;
import com.inoichi.dto.UserQueryResponseDTO;
import com.inoichi.repository.UserQueryRepository;
import com.inoichi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public UserQueryResponseDTO saveUserQuery(UUID userId, UserQueryRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserQuery query = new UserQuery();
        query.setUser(user);
        query.setQuery(requestDTO.getQuery());
        query.setExtractedSymptoms(requestDTO.getExtractedSymptoms());
        query.setCreatedAt(LocalDateTime.now());

        userQueryRepository.save(query);

        return new UserQueryResponseDTO(query);
    }
    public List<UserQueryResponseDTO> getAllQueriesForUser(UUID userId) {
        List<UserQuery> queries = userQueryRepository.findByUserId(userId);
        return queries.stream().map(UserQueryResponseDTO::new).collect(Collectors.toList());
    }


}
