package com.comviva.mfs.promotion.modules.common.tokens.repository;

import com.comviva.mfs.promotion.modules.common.tokens.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TOKEN_INFO repository.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, String>{
    Optional<Token> findByTokenUniqueReference(String token_unique_reference);
}
