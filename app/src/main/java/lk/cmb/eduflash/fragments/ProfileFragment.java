package lk.cmb.eduflash.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import lk.cmb.eduflash.LoginActivity;
import lk.cmb.eduflash.R;
import lk.cmb.eduflash.User;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etEmail;
    private Button btnEdit, btnSave, btnSignOut;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnSave = view.findViewById(R.id.btnSave);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Setup button listeners
        btnEdit.setOnClickListener(v -> toggleEditMode(true));
        btnSave.setOnClickListener(v -> {
            saveProfileData();
            toggleEditMode(false);
        });
        btnSignOut.setOnClickListener(v -> showSignOutDialog());

        // Set initial UI state and load data
        toggleEditMode(false);
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Reference to the specific user's node in the database
            userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Automatically map the snapshot to the User object
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            etUsername.setText(user.getUsername());
                            etEmail.setText(user.getEmail());

                            // Save to SharedPreferences as a cache for offline viewing
                            cacheUserData(user.getUsername(), user.getEmail());
                        }
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // If user is not logged in, try to load from the cache
            loadUserDataFromCache();
            Toast.makeText(getContext(), "Not signed in. Showing cached data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileData() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabaseRef != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("username", username);
            updates.put("email", email);

            userDatabaseRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    // Update the local cache
                    cacheUserData(username, email);
                } else {
                    Toast.makeText(getContext(), "Failed to save profile.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Cannot save profile. User not logged in.", Toast.LENGTH_LONG).show();
        }
    }

    private void toggleEditMode(boolean isEditing) {
        etUsername.setEnabled(isEditing);
        etEmail.setEnabled(isEditing);
        etUsername.setFocusableInTouchMode(isEditing);
        etEmail.setFocusableInTouchMode(isEditing);

        if (isEditing) {
            etUsername.requestFocus();
            etUsername.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextFocused));
            etEmail.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextFocused));
        } else {
            etUsername.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextDefault));
            etEmail.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextDefault));
        }

        btnSave.setEnabled(isEditing);
        btnEdit.setEnabled(!isEditing);
    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    // Clear cached user data
                    clearUserDataCache();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // --- SharedPreferences Helper Methods ---
    private void cacheUserData(String username, String email) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_profile_cache", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("username", username)
                .putString("email", email)
                .apply();
    }

    private void loadUserDataFromCache() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_profile_cache", Context.MODE_PRIVATE);
        etUsername.setText(prefs.getString("username", ""));
        etEmail.setText(prefs.getString("email", ""));
    }

    private void clearUserDataCache() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_profile_cache", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
