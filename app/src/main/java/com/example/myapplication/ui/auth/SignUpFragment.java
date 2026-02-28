package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.ui.Dashboard;
import com.example.myapplication.R;
import com.example.myapplication.data.repository.AuthRepository;
import com.example.myapplication.data.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.myapplication.data.local.*;

import java.util.regex.Pattern;

/**
 * SignUpFragment handles user registration validation.
 * - Uses NavController to return to LoginFragment
 */
public class SignUpFragment extends Fragment {

    // Containers for helper text
    private TextInputLayout nameContainer, emailContainer, passwordContainer, confirmContainer;

    // Inputs
    private TextInputEditText editFullName, editEmail, editPassword, editConfirmPassword;

    // Controls
    private Button btnCreateAccount;
    private TextView txtLoginLink;

    /**
     * Password regex (strong rules):
     * - At least 8 characters
     * - At least 1 uppercase
     * - At least 1 lowercase
     * - At least 1 digit
     * - At least 1 special char from @$!%*#?&
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$"
    );

    /**
     * Full name regex (Unicode-safe):
     * - Allows letters like ñ, é (\p{L})
     * - Allows spaces, dot, hyphen, apostrophe
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupLoginClickableText();
        setupLiveValidation();
        setupCreateAccount();
    }

    /**
     * Binds all views from XML.
     */
    private void bindViews(@NonNull View root) {
        nameContainer = root.findViewById(R.id.nameContainer);
        emailContainer = root.findViewById(R.id.emailContainer);
        passwordContainer = root.findViewById(R.id.passwordContainer);
        confirmContainer = root.findViewById(R.id.confirmContainer);

        editFullName = root.findViewById(R.id.editFullName);
        editEmail = root.findViewById(R.id.editEmail);
        editPassword = root.findViewById(R.id.editPassword);
        editConfirmPassword = root.findViewById(R.id.editConfirmPassword);

        btnCreateAccount = root.findViewById(R.id.btnCreateAccount);
        txtLoginLink = root.findViewById(R.id.txtLoginLink);
    }

    /**
     * Makes "Login" clickable and routes back to LoginFragment using NavController
    */
    private void setupLoginClickableText() {
        if (txtLoginLink == null) return;

        String fullText = "Already have an account? Login";
        SpannableString spannable = new SpannableString(fullText);

        String clickablePart = "Login";
        int start = fullText.indexOf(clickablePart);
        int end = start + clickablePart.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_signUpFragment_to_loginFragment);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.button_green));
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtLoginLink.setText(spannable);
        txtLoginLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtLoginLink.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Live helper text validation while user types.*/
    private void setupLiveValidation() {
        if (editFullName != null) {
            editFullName.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (nameContainer != null) {
                        nameContainer.setHelperText(getFullNameError(getText(editFullName)));
                    }
                }
            });
        }

        if (editEmail != null) {
            editEmail.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (emailContainer != null) {
                        emailContainer.setHelperText(getEmailError(getText(editEmail)));
                    }
                }
            });
        }

        if (editPassword != null) {
            editPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String pass = getText(editPassword);

                    // Progressive helper while typing
                    if (passwordContainer != null) {
                        passwordContainer.setHelperText(getPasswordLiveHelper(pass));
                    }

                    // Also re-check confirm password live
                    if (confirmContainer != null) {
                        confirmContainer.setHelperText(getConfirmPasswordError(
                                getText(editPassword), getText(editConfirmPassword)
                        ));
                    }
                }
            });
        }

        if (editConfirmPassword != null) {
            editConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (confirmContainer != null) {
                        confirmContainer.setHelperText(getConfirmPasswordError(
                                getText(editPassword), getText(editConfirmPassword)
                        ));
                    }
                }
            });
        }
    }

    /**
     * Validates all fields again when "Sign up" is clicked.
    */
    private void setupCreateAccount() {
        if (btnCreateAccount == null) return;

        btnCreateAccount.setOnClickListener(v -> {

            String name = getText(editFullName);
            String email = getText(editEmail);
            String pass = getText(editPassword);
            String confirm = getText(editConfirmPassword);

            String nameError = getFullNameError(name);
            String emailError = getEmailError(email);
            String passError = getPasswordError(pass);
            String confirmError = getConfirmPasswordError(pass, confirm);

            if (nameContainer != null) nameContainer.setHelperText(nameError);
            if (emailContainer != null) emailContainer.setHelperText(emailError);
            if (passwordContainer != null) passwordContainer.setHelperText(passError);
            if (confirmContainer != null) confirmContainer.setHelperText(confirmError);

            boolean hasError = (nameError != null || emailError != null || passError != null || confirmError != null);
            if (hasError) {
                Toast.makeText(requireContext(), "Please fix the highlighted fields.", Toast.LENGTH_LONG).show();
                return;
            }

            // Proceed with signup (Room + BCrypt via AuthRepository)
            handleSignup(name, email, pass);
        });
    }

    /**
     * Creates user in DB, sets session, then navigates to Dashboard
     */
    private void handleSignup(String fullName, String email, String password) {
        AuthRepository repo = new AuthRepository(requireContext());
        SessionManager session = new SessionManager(requireContext());

        AppExecutors.db().execute(() -> {

            // 1) prevent duplicates by checking if email already in the database
            boolean exists = repo.emailExists(email);

            if (!isAdded()) return;

            if (exists) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Email already exists.", Toast.LENGTH_LONG).show()
                );
                return;
            }

            // 2) insert new user (repo should hash internally)
            long newUserId = repo.createUser(fullName, email, password);

            // 3) set session
            session.setLoggedInUserId(newUserId);

            // 4) move forward
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(requireContext(), Dashboard.class));
                requireActivity().finish(); // prevent back to auth
            });
        });
    }

    // ---------------- Validation Methods  ----------------

    /**
     * Full name validation:
     * - Required
     * - Min 2 characters
     * - Unicode letters + spaces + hyphen + apostrophe
     */
    private String getFullNameError(String name) {
        if (name == null) name = "";
        if (name.isEmpty()) return "Full name is required";
        if (name.length() < 2) return "Name must be at least 2 characters";
        if (!NAME_PATTERN.matcher(name).matches()) return "Name can only contain letters and spaces";
        return null;
    }

    /**
     * Email validation:
     * - Required
     * - Valid email format
     */
    private String getEmailError(String email) {
        if (email == null) email = "";
        if (email.isEmpty()) return "Email is required";
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email address";
        return null;
    }

    /**
     * Password validation (regex):
     * - Required
     * - Must match strong password pattern
     */
    private String getPasswordError(String password) {
        if (password == null) password = "";
        if (password.isEmpty()) return "Password is required";
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Password must be 8+ chars and include uppercase, lowercase, number, and symbol";
        }
        return null;
    }

    /**
     * Confirm password validation:
     * - Required
     * - Must match password
     *
     */
    private String getConfirmPasswordError(String password, String confirm) {
        if (password == null) password = "";
        if (confirm == null) confirm = "";
        if (confirm.isEmpty()) return "Confirm password is required";
        if (!password.isEmpty() && !confirm.equals(password)) return "Passwords do not match";
        return null;
    }

    // ---------------- Utility Methods ----------------

    /**
     * Safely gets trimmed text from a TextInputEditText.
     */
    private String getText(TextInputEditText editText) {
        return (editText == null || editText.getText() == null) ? "" : editText.getText().toString().trim();
    }

    /**
     * SimpleTextWatcher: override only what you need.
     */
    public abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
    }

    /**
     * Returns a progressive helper message for password while typing.
     * Shows only what is still missing (better UX).
     *
     * @return helper message or null if the password is strong
     */
    private String getPasswordLiveHelper(String password) {
        String p = (password == null) ? "" : password;

        // If empty, show nothing until user types something (same approach as your activity)
        if (p.isEmpty()) return null;

        if (p.length() < 8) return "Add at least 8 characters";
        if (!containsUppercase(p)) return "Add 1 uppercase letter";
        if (!containsLowercase(p)) return "Add 1 lowercase letter";
        if (!containsDigit(p)) return "Add 1 number";
        if (!containsSpecial(p)) return "Add 1 special character (@$!%*#?&)";

        return null; // strong
    }

    private boolean containsUppercase(String s) {
        return s != null && s.matches(".*[A-Z].*");
    }

    private boolean containsLowercase(String s) {
        return s != null && s.matches(".*[a-z].*");
    }

    private boolean containsDigit(String s) {
        return s != null && s.matches(".*\\d.*");
    }

    private boolean containsSpecial(String s) {
        // Match the same special set you use in your regex
        return s != null && s.matches(".*[@$!%*#?&].*");
    }
}
