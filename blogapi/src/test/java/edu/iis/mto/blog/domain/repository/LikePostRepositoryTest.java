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

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertNotNull(persistedLikePost.getId());
    }

    @Test
    void shouldAddSavedLikePostToLikesInBlogPost() {

        LikePost likePost = new LikePost();
        likePost.setUser(dummyUser);
        likePost.setPost(dummyBlogPost);
        LikePost persistedLikePost = likePostRepository.save(likePost);

        testEntityManager.refresh(dummyBlogPost);
        assertThat(dummyBlogPost.getLikesCount(), equalTo(1));
        assertThat(dummyBlogPost.getLikes().get(0), equalTo(persistedLikePost));
    }

    @Test
    void shouldChangeUserInLikePost() {

        LikePost persistedLikePost = new LikePost();
        persistedLikePost.setUser(dummyUser);
        persistedLikePost.setPost(dummyBlogPost);
        testEntityManager.persistAndFlush(persistedLikePost);

        User user = new User();
        user.setAccountStatus(AccountStatus.CONFIRMED);
        user.setEmail("dummy@mail.com");
        testEntityManager.persistAndFlush(user);

        LikePost likePost = new LikePost();
        likePost.setId(persistedLikePost.getId());
        likePost.setPost(dummyBlogPost);
        likePost.setUser(user);
        likePostRepository.save(likePost);
        testEntityManager.flush();

        testEntityManager.refresh(persistedLikePost);
        assertThat(persistedLikePost.getUser().getId(), equalTo(user.getId()));
        assertThat(persistedLikePost.getUser().getEmail(), equalTo("dummy@mail.com"));
    }

    @Test
    void shouldChangeBlogPostInLikePost() {

        LikePost persistedLikePost = new LikePost();
        persistedLikePost.setUser(dummyUser);
        persistedLikePost.setPost(dummyBlogPost);
        testEntityManager.persistAndFlush(persistedLikePost);

        BlogPost newBlogPost = new BlogPost();
        newBlogPost.setEntry("Dummy BlogPost Entry");
        newBlogPost.setUser(dummyUser);
        testEntityManager.persistAndFlush(newBlogPost);

        LikePost likePost = new LikePost();
        likePost.setId(persistedLikePost.getId());
        likePost.setPost(newBlogPost);
        likePost.setUser(dummyUser);
        likePostRepository.save(likePost);

        testEntityManager.flush();
        testEntityManager.refresh(persistedLikePost);
        assertThat(persistedLikePost.getPost().getId(), equalTo(newBlogPost.getId()));
        assertThat(persistedLikePost.getPost().getEntry(), equalTo("Dummy BlogPost Entry"));
    }

    @Test
    void shouldFindLikePostByUserAndPost() {

        LikePost persistedLikePost = new LikePost();
        persistedLikePost.setUser(dummyUser);
        persistedLikePost.setPost(dummyBlogPost);
        testEntityManager.persistAndFlush(persistedLikePost);

        Optional<LikePost> optionalLikePost = likePostRepository.findByUserAndPost(dummyUser, dummyBlogPost);

        assertThat(optionalLikePost.isPresent(), equalTo(true));
        LikePost likePost = optionalLikePost.get();
        assertThat(likePost.getPost().getId(), equalTo(dummyBlogPost.getId()));
        assertThat(likePost.getUser().getId(), equalTo(dummyUser.getId()));
    }
}