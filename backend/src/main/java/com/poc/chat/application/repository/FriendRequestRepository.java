package com.poc.chat.application.repository;


import com.poc.chat.application.domain.entity.FriendRequestEntity;
import com.poc.chat.application.domain.entity.FriendRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, FriendRequestId> {
}
