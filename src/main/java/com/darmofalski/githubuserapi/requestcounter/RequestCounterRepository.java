package com.darmofalski.githubuserapi.requestcounter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestCounterRepository extends JpaRepository<RequestCounter, String> {
}
