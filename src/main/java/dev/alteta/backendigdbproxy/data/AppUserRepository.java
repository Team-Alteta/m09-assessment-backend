package dev.alteta.backendigdbproxy.data;

import dev.alteta.backendigdbproxy.models.AppUser;

public interface AppUserRepository {
    AppUser findByUsername(String username);

    AppUser add(AppUser appUser);
}
