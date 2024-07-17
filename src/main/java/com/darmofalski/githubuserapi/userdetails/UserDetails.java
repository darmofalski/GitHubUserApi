package com.darmofalski.githubuserapi.userdetails;

import java.time.LocalDateTime;

public record UserDetails(Long id,
                          String login,
                          String name,
                          String type,
                          String avatarUrl,
                          LocalDateTime createdAt,
                          Double calculations) {
}
