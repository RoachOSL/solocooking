/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common.persistence;

import java.util.List;

import dev.soloprogramming.solocooking.common.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseMigrationIT extends BaseIntegrationTest {

    private static final String APPLIED_MIGRATION_VERSIONS_SQL = """
            SELECT version
            FROM flyway_schema_history
            WHERE success
            ORDER BY installed_rank
            """;
    private static final String APPLICATION_INDEX_DEFINITIONS_SQL = """
            SELECT indexname, indexdef
            FROM pg_indexes
            WHERE schemaname = 'public'
              AND indexname IN (
                  'idx_recipe_ingredient_section_id',
                  'idx_recipe_section_recipe_id'
              )
            ORDER BY indexname
            """;
    private static final List<DatabaseIndex> EXPECTED_INDEXES = List.of(
            new DatabaseIndex(
                    "idx_recipe_ingredient_section_id",
                    "CREATE INDEX idx_recipe_ingredient_section_id "
                            + "ON public.recipe_ingredient USING btree (section_id)"
            ),
            new DatabaseIndex(
                    "idx_recipe_section_recipe_id",
                    "CREATE INDEX idx_recipe_section_recipe_id "
                            + "ON public.recipe_section USING btree (recipe_id)"
            )
    );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyInitialMigrationAndPreserveItsHistoryDuringCleanup() {
        // when
        var appliedVersions = jdbcTemplate.queryForList(APPLIED_MIGRATION_VERSIONS_SQL, String.class);

        // then
        assertThat(appliedVersions).contains("1");
    }

    @Test
    void shouldCreateIndexesForCurrentQueryPatterns() {
        // when
        var indexes = jdbcTemplate.query(
                APPLICATION_INDEX_DEFINITIONS_SQL,
                (resultSet, rowNumber) -> new DatabaseIndex(
                        resultSet.getString("indexname"),
                        resultSet.getString("indexdef")
                )
        );

        // then
        assertThat(indexes).containsExactlyElementsOf(EXPECTED_INDEXES);
    }

    private record DatabaseIndex(String name, String definition) {
    }
}
