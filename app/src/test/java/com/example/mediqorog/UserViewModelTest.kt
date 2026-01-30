package com.example.mediqorog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mediqorog.model.User
import com.example.mediqorog.repository.UserRepository
import com.example.mediqorog.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signUp_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        val mockUser = User(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            role = "patient"
        )

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signUp(any(), any(), any())).doReturn(Result.success(mockUser))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.signUp("test@example.com", "password123", "John Doe") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Account created successfully!", callbackMessage)
        assertEquals(mockUser, viewModel.user.first())

        verify(repo).signUp(eq("test@example.com"), eq("password123"), eq("John Doe"))
    }

    @Test
    fun signUp_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signUp(any(), any(), any())).doReturn(Result.failure(Exception("Email already exists")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.signUp("test@example.com", "password123", "John Doe") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Email already exists", callbackMessage)
        assertNull(viewModel.user.first())

        verify(repo).signUp(eq("test@example.com"), eq("password123"), eq("John Doe"))
    }

    @Test
    fun signIn_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        val mockUser = User(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            role = "patient"
        )

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signIn(any(), any())).doReturn(Result.success(mockUser))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        var isAdmin = true
        viewModel.signIn("test@example.com", "password123") { success, message, admin ->
            callbackSuccess = success
            callbackMessage = message
            isAdmin = admin
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Welcome back!", callbackMessage)
        assertEquals(false, isAdmin)
        assertEquals(mockUser, viewModel.user.first())

        verify(repo).signIn(eq("test@example.com"), eq("password123"))
    }

    @Test
    fun signIn_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signIn(any(), any())).doReturn(Result.failure(Exception("Invalid credentials")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        var isAdmin = true
        viewModel.signIn("test@example.com", "wrongpassword") { success, message, admin ->
            callbackSuccess = success
            callbackMessage = message
            isAdmin = admin
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Invalid credentials", callbackMessage)
        assertEquals(false, isAdmin)

        verify(repo).signIn(eq("test@example.com"), eq("wrongpassword"))
    }

    @Test
    fun signInWithGoogle_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()
        val account = mock<GoogleSignInAccount>()

        val mockUser = User(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            role = "patient"
        )

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signInWithGoogle(any())).doReturn(Result.success(mockUser))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        var isAdmin = true
        viewModel.signInWithGoogle(account) { success, message, admin ->
            callbackSuccess = success
            callbackMessage = message
            isAdmin = admin
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Welcome!", callbackMessage)
        assertEquals(false, isAdmin)
        assertEquals(mockUser, viewModel.user.first())

        verify(repo).signInWithGoogle(eq(account))
    }

    @Test
    fun signInWithGoogle_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()
        val account = mock<GoogleSignInAccount>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signInWithGoogle(any())).doReturn(Result.failure(Exception("Google sign in failed")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        var isAdmin = true
        viewModel.signInWithGoogle(account) { success, message, admin ->
            callbackSuccess = success
            callbackMessage = message
            isAdmin = admin
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Google sign in failed", callbackMessage)
        assertEquals(false, isAdmin)

        verify(repo).signInWithGoogle(eq(account))
    }

    @Test
    fun signOut_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        val mockUser = User(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            role = "patient"
        )

        whenever(repo.getCurrentUser()).doReturn(mockUser)
        whenever(repo.signOut()).doReturn(Result.success(Unit))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.signOut { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Signed out successfully", callbackMessage)
        assertNull(viewModel.user.first())

        verify(repo).signOut()
    }

    @Test
    fun signOut_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.signOut()).doReturn(Result.failure(Exception("Sign out failed")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.signOut { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Sign out failed", callbackMessage)

        verify(repo).signOut()
    }

    @Test
    fun resetPassword_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.resetPassword(any())).doReturn(Result.success(Unit))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.resetPassword("test@example.com") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Password reset email sent", callbackMessage)

        verify(repo).resetPassword(eq("test@example.com"))
    }

    @Test
    fun resetPassword_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.resetPassword(any())).doReturn(Result.failure(Exception("Failed to send reset email")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.resetPassword("test@example.com") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Failed to send reset email", callbackMessage)

        verify(repo).resetPassword(eq("test@example.com"))
    }

    @Test
    fun getCurrentUser_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        val mockUser = User(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            role = "patient"
        )

        whenever(repo.getCurrentUser()).doReturn(mockUser)

        val viewModel = UserViewModel(repo)

        // Give coroutines time to complete
        testDispatcher.scheduler.advanceUntilIdle()

        var returnedUser: User? = null
        viewModel.getCurrentUser { user ->
            returnedUser = user
        }

        // Give the callback time to execute
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockUser, returnedUser)
        assertEquals(mockUser, viewModel.user.first())

        verify(repo, atLeastOnce()).getCurrentUser()
    }

    @Test
    fun getCurrentUser_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        // Return null first (for ViewModel init), then throw on subsequent calls
        whenever(repo.getCurrentUser())
            .doReturn(null)
            .thenThrow(RuntimeException("Failed to get user"))

        val viewModel = UserViewModel(repo)

        // Give coroutines time to complete
        testDispatcher.scheduler.advanceUntilIdle()

        var returnedUser: User? = User(uid = "dummy", email = "dummy@test.com")
        viewModel.getCurrentUser { user ->
            returnedUser = user
        }

        // Give the callback time to execute
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(returnedUser)

        verify(repo, atLeast(2)).getCurrentUser()
    }

    @Test
    fun updateAllUsersWithRole_success_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.updateAllUsersWithRole()).doReturn(Result.success("Users updated successfully"))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.updateAllUsersWithRole { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Users updated successfully", callbackMessage)

        verify(repo).updateAllUsersWithRole()
    }

    @Test
    fun updateAllUsersWithRole_error_test(): Unit = runBlocking {
        val repo = mock<UserRepository>()

        whenever(repo.getCurrentUser()).doReturn(null)
        whenever(repo.updateAllUsersWithRole()).doReturn(Result.failure(Exception("Update failed")))

        val viewModel = UserViewModel(repo)

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.updateAllUsersWithRole { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Update failed", callbackMessage)

        verify(repo).updateAllUsersWithRole()
    }
}