package com.example.firstapp_luismunoz.ui.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.firstapp_luismunoz.fakes.FakeUserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FakeUserRepository repository;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeUserRepository();
        viewModel = new LoginViewModel(repository);
    }

    @Test
    public void register_validCredentials_reportsSuccess() {
        viewModel.register("luis", "secret1");

        assertEquals("Registration successful", viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void register_duplicateUsername_reportsAlreadyExists() {
        viewModel.register("luis", "secret1");
        viewModel.getMessages().getValue().getContentIfNotHandled();

        viewModel.register("luis", "other12");

        assertEquals("User already exists", viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void register_emptyFields_reportsError() {
        viewModel.register("", "");

        assertEquals("Please enter all fields", viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void register_shortPassword_reportsError() {
        viewModel.register("luis", "123");

        assertEquals("Password must be at least 6 characters",
                viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void login_correctCredentials_emitsLoginSuccess() {
        viewModel.register("luis", "secret1");

        viewModel.login("luis", "secret1");

        assertEquals("luis", viewModel.getLoginSuccess().getValue().getContentIfNotHandled());
        assertFalse(viewModel.isBusy().getValue());
    }

    @Test
    public void login_wrongPassword_reportsInvalidCredentials() {
        viewModel.register("luis", "secret1");
        viewModel.getMessages().getValue().getContentIfNotHandled();

        viewModel.login("luis", "wrong");

        assertNull(viewModel.getLoginSuccess().getValue());
        assertEquals("Invalid credentials", viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void login_repositoryFailure_reportsErrorAndClearsBusy() {
        repository.failNextCall = true;

        viewModel.login("luis", "secret1");

        assertEquals("Login failed. Please try again.", viewModel.getMessages().getValue().getContentIfNotHandled());
        assertFalse(viewModel.isBusy().getValue());
    }
}
