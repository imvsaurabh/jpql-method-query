package com.github.imvsaurabh.jpql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import static com.github.imvsaurabh.jpql.TestTblSpecification.findByFlightAndRecLoc;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class TestTbl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fltKey;
    private String recLoc;
    private String eventData;
}

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(TestTblRepository repository) {
        return args -> {
            String fltKey = "100:26052024:UA";
            String recLoc = "ABC123";
            repository.findAll(findByFlightAndRecLoc(fltKey, recLoc)).forEach(System.out::println);
        };
    }
}

class TestTblSpecification {
    public static Specification<TestTbl> findByFlightAndRecLoc(String flightKey, String recLoc) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("fltKey"), flightKey),
                criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("recLoc")),
                        criteriaBuilder.equal(root.get("recLoc"), recLoc)
                )
        ));
    }
}

@Repository
interface TestTblRepository extends JpaRepository<TestTbl, Long>, JpaSpecificationExecutor<TestTbl> {

    @Query(value = "select t from TestTbl t where t.fltKey=:fltKey and (t.recLoc is null or t.recLoc=:recLoc)")
    List<TestTbl> justFindIt(String fltKey, String recLoc);
}
