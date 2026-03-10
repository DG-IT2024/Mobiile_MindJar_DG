package com.example.myapplication.ui.auth;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.ui.Dashboard;
import com.example.myapplication.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.myapplication.data.repository.AuthRepository;

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

    // --- Google Auth ---
    private ImageView iconGoogle;

    private GoogleSignInClient googleSignInClient; // null if Google auth is not configured

    private final AuthRepository repo = new AuthRepository();


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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupGoogleAuth();
        setupListeners();
        setupLiveValidation();
        setupSignUpLink(view);

    }

    // -------------------------------------------------------------------------
    // View binding
    // -------------------------------------------------------------------------

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

        //Google Auth
        iconGoogle = root.findViewById(R.id.iconGoogle);
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    // Sets up click listeners for Login button and password toggle checkbox.

    private void setupListeners() {
        if (btnLogin != null) btnLogin.setOnClickListener(this);

        if (checkShowPassword != null) {
            checkShowPassword.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> togglePasswordVisibility(isChecked)
            );

        }

        if (iconGoogle != null) {
            iconGoogle.setOnClickListener(v -> {

                // null-guard — prevents NPE when Google auth is unconfigured
                if (googleSignInClient == null) {
                    Toast.makeText(requireContext(),
                            "Google Sign-In is not available",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
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

        // Firebase Auth credential check
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

        repo.login(email, password, new AuthRepository.AuthCallback() {

            @Override
            public void onSuccess() {
                // Firebase is already signed in — no manual session needed
                openDashboard();
                requireActivity().finish(); // prevent back to login
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(),
                        "Invalid email or password",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // -------------------------------------------------------------------------
    //  Google Auth setup
    // -------------------------------------------------------------------------
    /**
     * If default_web_client_id is missing or empty (e.g. google-services.json not linked),
     * googleSignInClient is set to null and a Toast is shown.  setupListeners() checks for
     * null before calling getSignInIntent(), so no NPE can occur.
    */
    private void setupGoogleAuth() {
        String webClientId = getString(R.string.default_web_client_id);
        if (webClientId == null || webClientId.trim().isEmpty()) {
            Toast.makeText(requireContext(),
                    "Google auth is not configured",
                    Toast.LENGTH_LONG).show();
            googleSignInClient = null;
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }


    /**
     * Receives the result from Google's sign-in screen.
     *
     * Happy path:
     *   RESULT_OK  → extract GoogleSignInAccount → get idToken → hand off to AuthRepository
     *
     * Error paths:
     *   idToken null     → shows a Toast (shouldn't happen if SHA-1 is registered in Firebase)
     *   ApiException     → shows the status code (e.g. 10 = developer_error = bad SHA-1/client ID)
     *   RESULT_CANCELLED → user pressed Back; show brief Toast and do nothing
     */

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        String idToken = account != null ? account.getIdToken() : null;
                        if (idToken != null && !idToken.isEmpty()) {
                            repo.loginWithGoogleIdToken(idToken, new AuthRepository.AuthCallback() {
                                @Override
                                public void onSuccess() {
                                    openDashboard();
                                    requireActivity().finish();
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Toast.makeText(requireContext(),
                                            errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(),
                                    "Google token is null",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (ApiException e) {
                        Toast.makeText(requireContext(),
                                "Google Sign-In failed: " + e.getStatusCode(),
                                Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(requireContext(), "Google Sign-In cancelled", Toast.LENGTH_SHORT).show();
                }
            });


}
