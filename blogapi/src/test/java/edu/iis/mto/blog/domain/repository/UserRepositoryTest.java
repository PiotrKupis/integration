package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.flush();

        user = new User();
        user.setFirstName("Jan");
        user.setLastName("Nowak");
        user.setEmail("john@domain.com");
        user.setAccountStatus(AccountStatus.NEW);
    }

    @Test
    void shouldFindNoUsersIfRepositoryIsEmpty() {

        List<User> users = repository.findAll();
        assertThat(users, hasSize(0));
    }

    @Test
    void shouldFindOneUsersIfRepositoryContainsOneUserEntity() {
        User persistedUser = repository.save(user);
        List<User> users = repository.findAll();

        assertThat(users, hasSize(1));
        assertThat(users.get(0)
                        .getEmail(),
                equalTo(persistedUser.getEmail()));
    }

    @Test
    void shouldStoreANewUser() {

        User persistedUser = repository.save(user);
        assertThat(persistedUser.getId(), notNullValue());
    }

    @Test
    void shouldFindUserByFirstName() {

        int expectedNumberOfResults = 1;
        User persistedUser = entityManager.persist(user);

        List<User> users = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(persistedUser.getFirstName(), " ", " ");

        assertEquals(expectedNumberOfResults, users.size());
        assertEquals(persistedUser.getId(), users.get(0).getId());
    }

    @Test
    void shouldFindUserByLastName() {

        int expectedNumberOfResults = 1;
        User persistedUser = entityManager.persist(user);

        List<User> users = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", persistedUser.getLastName(), " ");

        assertEquals(expectedNumberOfResults, users.size());
        assertEquals(persistedUser.getId(), users.get(0).getId());
    }

    @Test
    void shouldFindUserByEmail() {

        int expectedNumberOfResults = 1;
        User persistedUser = entityManager.persist(user);

        List<User> users = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", " ", persistedUser.getEmail());

        assertEquals(expectedNumberOfResults, users.size());
        assertEquals(persistedUser.getId(), users.get(0).getId());
    }

    @Test
    void shouldNotFindUser() {

        int expectedNumberOfResults = 0;
        entityManager.persist(user);

        List<User> users = repository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", " ", " ");

        assertEquals(expectedNumberOfResults, users.size());
    }


}
