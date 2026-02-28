package com.example.myapplication.ui.hotline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

/**
 * HotlineFragment provides quick access to crisis support contacts.
 *
 * The user can tap:
 * - an "email/message" icon to open the SMS app with a pre-filled message, or
 * - a "phone" icon to open the dialer with a hotline number.
 *
 * Note: These actions open external apps (Messaging or Dialer), so intents are used.
 */
public class HotlineFragment extends Fragment implements View.OnClickListener {

    // Hotline numbers used by the icons in this screen.
    private static final String HOTLINE_1 = "180018881553";
    private static final String HOTLINE_2 = "09188784673";
    private static final String HOTLINE_3 = "09228938944";

    // Default message that is pre-filled when the user opens the SMS app.
    private static final String DEFAULT_SMS_BODY = "Please help me.";

    // Message (SMS) icons.
    private ImageView messageIcon1;
    private ImageView messageIcon2;
    private ImageView messageIcon3;

    // Phone (dial) icons.
    private ImageView phoneIcon1;
    private ImageView phoneIcon2;
    private ImageView phoneIcon3;

    /**
     * Required empty public constructor.
     * The Android system uses this when recreating the fragment.
     */
    public HotlineFragment() {
        // No initialization is needed here.
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotline, container, false);
        bindViews(view);
        setClickListeners();

        return view;
    }

    /**
     * Finds and assigns the views from the fragment layout.
     */
    private void bindViews(@NonNull View view) {
        messageIcon1 = view.findViewById(R.id.imageEmail);
        messageIcon2 = view.findViewById(R.id.imageEmail2);
        messageIcon3 = view.findViewById(R.id.imageEmail3);
        phoneIcon1 = view.findViewById(R.id.imagePhone);
        phoneIcon2= view.findViewById(R.id.imagePhone2);
        phoneIcon3= view.findViewById(R.id.imagePhone3);
    }


    /**
     * Sets click listeners for all message and phone icons.
     */
    private void setClickListeners() {
        messageIcon1.setOnClickListener(this);
        messageIcon2.setOnClickListener(this);
        messageIcon3.setOnClickListener(this);

        phoneIcon1.setOnClickListener(this);
        phoneIcon2.setOnClickListener(this);
        phoneIcon3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

            // Message icons open the user's SMS app.
        if(id == R.id.imageEmail){
            openSMSApp("180018881553");
        }else if(id == R.id.imageEmail2){
            openSMSApp("09188784673");
        }else if(id == R.id.imageEmail3){
            openSMSApp("09228938944");

            // Phone icons open the device dialer.
        }else if(id == R.id.imagePhone){
            openDialer("1800-1888-1553");
        }else if(id == R.id.imagePhone2){
            openDialer("09188784673");
        }else if(id == R.id.imagePhone3){
            openDialer("1800-1888-1553");
        }

    }

    /**
     * Opens the user's messaging app with a pre-filled phone number and message body.
     * This uses ACTION_SENDTO so only messaging apps can handle the intent.
     */
    private void openSMSApp(String phoneNumber) {
        try {
            Uri smsUri = Uri.parse("smsto:" + phoneNumber);

            // Use ACTION_SEND TO with smsto: URI (this ensures only SMS apps handle it)
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber)); // "smsto:"  is the recommended scheme for SMS intents.
            intent.putExtra("sms_body", "Please help me.");

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "No messaging app found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to open Messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the phone dialer with the provided number.
     * This uses ACTION_DIAL, which does not require call permissions.
     */
    private void openDialer(String phoneNumber) {
        try {
            Uri telUri = Uri.parse("tel:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber)); // "tel:" is the correct scheme
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to open dialer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}