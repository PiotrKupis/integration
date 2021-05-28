package edu.iis.mto.blog.domain;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.services.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BlogManagerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BlogPostRepository blogPostRepository;
    @MockBean
    private LikePostRepository likePostRepository;

    @Autowired
    private BlogService blogService;

    @Captor
    private ArgumentCaptor<User> userParam;
    @Captor
    private ArgumentCaptor<LikePost> likePostParam;

    private User dummyUser;
    private User dummyUserThatLikePost;
    private BlogPost dummyBlogPost;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setAccountStatus(AccountStatus.CONFIRMED);
        dummyUser.setEmail("kowalski@mail.com");
        dummyUser.setId(1L);

        dummyUserThatLikePost = new User();
        dummyUserThatLikePost.setAccountStatus(AccountStatus.NEW);
        dummyUserThatLikePost.setEmail("nowak@mail.com");
        dummyUserThatLikePost.setId(2L);

        dummyBlogPost = new BlogPost();
        dummyBlogPost.setEntry("Dummy Entry");
        dummyBlogPost.setUser(dummyUser);
        dummyBlogPost.setId(1L);
    }

    @Test
    void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("John", "Steward", "john@domain.com"));
        verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        assertThat(user.getAccountStatus(), equalTo(AccountStatus.NEW));
    }

    @Test
    void shouldThrowDomainErrorWhenUserTryLikesThePostWithDifferentStatusThanConfirmed() {

        when(userRepository.findById(any())).thenReturn(Optional.of(dummyUserThatLikePost));
        when(blogPostRepository.findById(any())).thenReturn(Optional.of(dummyBlogPost));
        assertThrows(DomainError.class, () -> blogService.addLikeToPost(dummyUserThatLikePost.getId(), dummyBlogPost.getId()));
    }

    @Test
    void shouldAddLikeToPostWhenUserIsConfirmed() {

        int expectedNumberOfLikesUnderPost = 1;
        dummyUserThatLikePost.setAccountStatus(AccountStatus.CONFIRMED);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyUserThatLikePost));
        when(blogPostRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyBlogPost));
        when(likePostRepository.findByUserAndPost(any(User.class), any(BlogPost.class))).thenReturn(Optional.empty());

        blogService.addLikeToPost(dummyUserThatLikePost.getId(), dummyBlogPost.getId());

        verify(likePostRepository).save(likePostParam.capture());
        List<LikePost> capturedLikePosts = likePostParam.getAllValues();
        assertEquals(expectedNumberOfLikesUnderPost, capturedLikePosts.size());

        LikePost capturedLikePost = capturedLikePosts.get(0);
        assertEquals(dummyUserThatLikePost, capturedLikePost.getUser());
        assertEquals(dummyBlogPost, capturedLikePost.getPost());
    }

}
