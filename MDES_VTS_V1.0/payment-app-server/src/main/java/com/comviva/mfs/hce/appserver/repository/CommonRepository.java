package com.comviva.mfs.hce.appserver.repository;


import com.comviva.mfs.hce.appserver.model.SysMessage;
import com.comviva.mfs.hce.appserver.model.SysMessagePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by shadab.ali on 23-08-2017.
 */
@Repository
public interface CommonRepository extends JpaRepository<SysMessage, String> {
    @Query("Select distinct sm from SysMessage sm where sm.id.messageCode =:messageCode and sm.id.languageCode=:languageCode")
    List<SysMessage> find(@Param("messageCode")String messageCode, @Param("languageCode") String languageCode);
}
