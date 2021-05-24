package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LikePostRepository likePostRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;

    private User dummyUser;
    private BlogPost dummyBlogPost;

    @BeforeEach
    void setUp() {

        likePostRepository.deleteAll();
        likePostRepository.flush();
        blogPostRepository.deleteAll();
        blogPostRepository.flush();

        dummyUser = new User();
        dummyUser.setAccountStatus(AccountStatus.CONFIRMED);
        dummyUser.setEmail("kowalski@mail.com");

        dummyBlogPost = new BlogPost();
        dummyBlogPost.setEntry("Dummy Entry");
        dummyBlogPost.setUser(dummyUser);

        testEntityManager.persistAndFlush(dummyUser);
        testEntityManager.persistAndFlush(dummyBlogPost);
    }

    @Test
    void shouldSaveNewLikePost() {

        LikePost likePost = new LikePost();
        likePost.setUser(dummyUser);
        likePost.setPost(dummyBlogPost);
        LikePost persistedLikePost = likePostRepository.save(likePost);
        assertThat(persistedLikePost.getId(), notNullValue());
    }
}