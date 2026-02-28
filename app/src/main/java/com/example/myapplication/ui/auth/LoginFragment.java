package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.ui.Dashboard;
import com.example.myapplication.R;
import com.example.myapplication.data.*;
import com.example.myapplication.data.local.*;
import com.example.myapplication.data.repository.*;

import com.example.myapplication.data.local.entity.UserEntity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;



/**
 * LoginFragment handles the Login UI and validation logic.
 *
 * Login validation rules
 * - Email: Required + valid email format</li>
 * - Password: Required only</li>
 * - On failed login: Show a generic "Invalid email or password"</li>
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // --- UI Containers (for helper text) ---
    private TextInputLayout emailContainer;
    private TextInputLayout passwordContainer;

    // --- Input Fields ---
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;

    // --- Controls ---
    private Button btnLogin;
    private CheckBox checkShowPassword; // used as show/hide toggle

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Uses the fragment version of your existing login layout
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupListeners();
        setupLiveValidation();
        setupSignUpLink(view);

        // SessionManager session = new SessionManager(requireContext());
        // if (session.getLoggedInUserId() > 0) {
        //     openDashboard();
        //     requireActivity().finish();
        // }
    }

    /**
     * Binds XML views to Java fields.
     */
    private void bindViews(@NonNull View root) {
        btnLogin = root.findViewById(R.id.button);

        emailContainer = root.findViewById(R.id.emailContainer);
        passwordContainer = root.findViewById(R.id.passwordContainer);

        editEmail = root.findViewById(R.id.editEmail);
        editPassword = root.findViewById(R.id.editPassword);

        // NOTE: Your checkbox id is "checkBox" in XML.
        checkShowPassword = root.findViewById(R.id.checkBox);
    }

    /**
     * Sets up click listeners for Login button and password toggle checkbox.
     */
    private void setupListeners() {
        if (btnLogin != null) btnLogin.setOnClickListener(this);

        if (checkShowPassword != null) {
            checkShowPassword.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> togglePasswordVisibility(isChecked)
            );

        }
    }

    /**
     * Shows helper validation messages while user types.
     */
    private void setupLiveValidation() {
        if (editEmail != null && emailContainer != null) {
            editEmail.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    emailContainer.setHelperText(getEmailErrorMessage(s.toString()));
                }
            });
        }

        if (editPassword != null && passwordContainer != null) {
            editPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passwordContainer.setHelperText(getPasswordErrorMessage(s.toString()));
                }
            });
        }
    }

    /**
     * Makes "Sign Up" clickable and routes to SignUpFragment via NavController.
     */
    private void setupSignUpLink(@NonNull View root) {
        TextView txtSignUpLink = root.findViewById(R.id.txtSignUpLink);
        if (txtSignUpLink == null) return;

        String fullText = "Don’t have an account? Sign Up";
        SpannableString spannable = new SpannableString(fullText);

        String clickablePart = "Sign Up";
        int start = fullText.indexOf(clickablePart);
        int end = start + clickablePart.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // NavController-based navigation (per feedback)
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_signUpFragment);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.button_green));
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSignUpLink.setText(spannable);
        txtSignUpLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtSignUpLink.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        if (v != null && v.getId() == R.id.button) {
            attemptLogin();
        }
    }

    /**
     * Attempts login by validating inputs and then checking credentials.
     */
    private void attemptLogin() {
        String email = getTrimmedText(editEmail);
        String password = getTrimmedText(editPassword);

        // Field-level validation
        String emailError = getEmailErrorMessage(email);
        String passwordError = getPasswordErrorMessage(password);

        // Show helper texts
        if (emailContainer != null) emailContainer.setHelperText(emailError);
        if (passwordContainer != null) passwordContainer.setHelperText(passwordError);

        // Stop if validation fails
        if (emailError != null || passwordError != null) {
            Toast.makeText(requireContext(), "Please fix the highlighted fields.", Toast.LENGTH_LONG).show();
            return;
        }

        // Real DB-backed credential check (Room)
        tryLogin(email, password);
    }

    /**
     * Email validation for login:
     * - Required
     * - Valid email format
     *
     * @return error message or null if valid
     */
    private String getEmailErrorMessage(String email) {
        String value = email == null ? "" : email.trim();

        if (value.isEmpty()) return "Required";
        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) return "Invalid email";
        return null;
    }

    /**
     * Password validation for login
     * - Required only
     *
     * @return error message or null if valid
     */
    private String getPasswordErrorMessage(String password) {
        String value = password == null ? "" : password.trim();
        if (value.isEmpty()) return "Password required";
        return null;
    }

    /**
     * Toggles password visibility (used by checkbox).
     */
    private void togglePasswordVisibility(boolean showPassword) {
        if (editPassword == null) return;

        if (showPassword) {
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        // Keep cursor at end
        if (editPassword.getText() != null) {
            editPassword.setSelection(editPassword.getText().length());
        }
    }

    /**
     * Opens the Dashboard screen after successful login.
     *
     */
    private void openDashboard() {
        startActivity(new Intent(requireContext(), Dashboard.class));
    }

    private String getTrimmedText(TextInputEditText input) {
        return (input == null || input.getText() == null) ? "" : input.getText().toString().trim();
    }

    /**
     * SimpleTextWatcher reduces boilerplate by letting you override only needed methods.
     */
    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void tryLogin(String email, String password) {
        AuthRepository repo = new AuthRepository(requireContext());
        SessionManager session = new SessionManager(requireContext());

        AppExecutors.db().execute(() -> {
            UserEntity user = repo.getUserByEmail(email);

            boolean ok = false;
            long userId = -1;

            if (user != null) {
                ok = repo.verifyPassword(password, user.passwordHash);
                if (ok) userId = user.id;
            }

            boolean finalOk = ok;
            long finalUserId = userId;

            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                if (finalOk) {
                    session.setLoggedInUserId(finalUserId);
                    openDashboard();
                    requireActivity().finish(); // prevent back to login
                } else {
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
