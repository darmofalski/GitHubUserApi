package com.darmofalski.githubuserapi.userdetails.githubuserdetails;

import java.time.LocalDateTime;

public record GitHubUserDetails(Long id,
                         String login,
                         String name,
                         String type,
                         String avatar_url,
                         LocalDateTime created_at,
                         Integer followers,
                         Integer public_repos) {
}