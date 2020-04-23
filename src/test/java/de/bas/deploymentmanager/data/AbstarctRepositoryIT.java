package de.bas.deploymentmanager.data;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Testcontainers
public class AbstarctRepositoryIT {

    private static final String LOCATION = "db/migration/postgres";
    EntityManager em;
    EntityTransaction tx;

    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer()
            .withDatabaseName("postgres")
            .withPassword("docker")
            .withUsername("docker");

    @BeforeEach
    public void init() {
        Map<String, String> configuration = getConfigMap();

        createEntityManager(configuration);

        runFlyway();
    }

    private void runFlyway() {
        Flyway flyway = Flyway.configure().dataSource(postgres.getJdbcUrl()
                , postgres.getUsername()
                , postgres.getPassword()).locations(LOCATION).load();
        flyway.clean();
        flyway.migrate();
    }

    private void createEntityManager(Map<String, String> configuration) {
        this.em = Persistence.
                createEntityManagerFactory("integration-test", configuration).
                createEntityManager();
        this.tx = this.em.getTransaction();
    }

    @NotNull
    private Map<String, String> getConfigMap() {
        Map<String, String> configuration = new HashMap();
        configuration.put("javax.persistence.jdbc.url", postgres.getJdbcUrl());
        configuration.put("javax.persistence.jdbc.user", postgres.getUsername());
        configuration.put("javax.persistence.jdbc.password", postgres.getPassword());
        return configuration;
    }

    /**
     * Setzt in das Repository den EntityManager
     * <p>
     * repository = injectEntityManager(new ProjectRepository())
     *
     * @param repository
     * @param <S>
     * @return
     */
    public <S extends AbstractRepository> S injectEntityManager(S repository) {
        repository.entityManager = em;
        return repository;
    }

    public void executeNativQuery(String sql) {
        tx.begin();
        em.createNativeQuery(sql).executeUpdate();
        tx.commit();
    }

    public <T> void withTx(Consumer<T> consumer) {
        tx.begin();
        consumer.accept(null);
        tx.commit();

    }

    public void begin() {
        tx.begin();
    }

    public void commit() {
        tx.commit();
    }


}
