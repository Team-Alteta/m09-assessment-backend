package dev.alteta.backendigdbproxy.data;

import dev.alteta.backendigdbproxy.data.mappers.AppUserMapper;
import dev.alteta.backendigdbproxy.models.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AppUserJdbcTemplateRepository implements AppUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppUserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public AppUser findByUsername(String username) {

        String sql = """
                select 
                     u.app_user_id,
                     u.username,
                     u.password_hash,
                     u.enabled
                from app_user u
                where u.username = ?;
                """;
        return jdbcTemplate.query(sql, new AppUserMapper(getAuthorities(username)), username).stream()
                .findFirst().orElse(null);
    }

    @Override
    @Transactional
    public AppUser add(AppUser appUser) {
        final String sql = """
            insert into app_user (username, password_hash, enabled) values (?,?,?);
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, appUser.getUsername());
            ps.setString(2, appUser.getPassword());
            ps.setBoolean(3, appUser.isEnabled());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }
        appUser.setId(keyHolder.getKey().intValue());

        updateRoles(appUser);

        return appUser;
    }

    private void updateRoles(AppUser appUser) {
        jdbcTemplate.update("delete from app_user_role where app_user_id = ?;", appUser.getId());
        for (var authority : appUser.getAuthorities()) {
            String sql = """
                    insert into app_user_role (app_user_id, app_role_id)
                    values (?, (select app_role_id from app_role where `name` = ?));
                    """;
            jdbcTemplate.update(sql, appUser.getId(), authority.getAuthority());
        }
    }

    private List<String> getAuthorities(String username) {
        final String sql = """
                select 
                    r.name
                from app_role r
                inner join app_user_role ur on ur.app_role_id = r.app_role_id
                inner join app_user u on u.app_user_id = ur.app_user_id
                where u.username = ?;
                """;
        return jdbcTemplate.query(sql, (rs, i) -> rs.getString("name"), username);
    }
}
