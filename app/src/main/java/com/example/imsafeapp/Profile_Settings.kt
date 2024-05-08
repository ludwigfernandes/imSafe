package com.example.imsafeapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.squareup.picasso.Picasso


class Profile_Settings : AppCompatActivity() {

    private lateinit var vibrator: Vibrator
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        val logout: AppCompatButton = findViewById(R.id.logout)
        val goback: ImageView = findViewById(R.id.backbutton)
        val contact_us: TextView = findViewById(R.id.connect)
        val termsandconditions: TextView = findViewById(R.id.termsAndConditionsLink)
        val changepassword: TextView = findViewById(R.id.changepassword)
        val edit_profile: Button = findViewById(R.id.editprof)


        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        auth = FirebaseAuth.getInstance()

        // Retrieve the phone number from the Intent extras
        val phoneNumber = intent.getStringExtra("phoneNumber")

        // Find the phoneTextView by its ID
        val phone: TextView = findViewById(R.id.phoneTextView)

        // Set the phone number as the text for phoneTextView
        phone.text = phoneNumber

        edit_profile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }

        val Share: ImageButton = findViewById(R.id.share)
        Share.setOnClickListener {
            shareApkLink()
        }

        //Lets the user log out of his profile.
        logout.setOnClickListener {
            signOutGoogle()
            vibrateForTwoSeconds()

            startActivity(Intent(this, LoginActivity::class.java))
        }



        //Lets the user change their password
        changepassword.setOnClickListener {
            startActivity(Intent(this, Forgot_Password::class.java))
        }


        //Lets the user go back to homepage
        goback.setOnClickListener {
            startActivity(Intent(this, Homepage::class.java))
        }


        //Opens the terms and conditions
        termsandconditions.setOnClickListener {
            val content = getString(R.string.terms_and_conditions)

            // Display terms and conditions content with line breaks
            val formattedContent = Html.fromHtml(content.replace("\n", "<br/>"))
            AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage(formattedContent)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        //Redirects the user to a gmail account
        contact_us.setOnClickListener {
            // Create an intent with ACTION_SENDTO action
            val emailIntent = Intent(Intent.ACTION_SENDTO)

            // Set the data (URI) for the intent with recipient and subject
            val uriText = "mailto:johannalvares2@gmail.com" +
                    "?subject=" + Uri.encode("Subject of the email") // Add subject here
            val uri = Uri.parse(uriText)
            emailIntent.data = uri

            // Ensure the intent is handled by the Gmail app
            emailIntent.setPackage("com.google.android.gm")

            // Check if there's an activity to handle this intent
            if (emailIntent.resolveActivity(packageManager) != null) {
                // If Gmail is installed, start the intent directly
                startActivity(emailIntent)
            } else {
                // If Gmail app is not installed, you can handle it based on your requirement
                // Here, you can open a web-based email service or show a message to the user
                // For demonstration, we'll just open a web-based email service
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"))
                startActivity(webIntent)
            }
        }


        //Adds the name and email from the recently signed in gmail account.
        val account = GoogleSignIn.getLastSignedInAccount(this)

        // Update TextViews with the user's name and email
        account?.let {
            val nameTextView = findViewById<TextView>(R.id.nameTextView) // Find the TextView by its ID
            val emailTextView = findViewById<TextView>(R.id.emailTextView) // Find the TextView by its ID
            nameTextView.text = it.displayName
            emailTextView.text = it.email
        }

        // Replace it with your actual phone number retrieval logic
        val phoneTextView = findViewById<TextView>(R.id.phoneTextView) // Find the TextView by its ID
        phoneTextView.text = "+91 87672 02131" // Example number


        //Repales image with one associated with Google Account
        val profileImageView: ImageView = findViewById(R.id.profileImageView)

        if (account != null) {
            // Load the profile photo using Picasso library (or any other image loading library)
            Picasso.get().load(account.photoUrl).into(profileImageView)
        } else {
            // If the user is not signed in with Google, you can set a default image or hide the ImageView
            profileImageView.setImageResource(R.drawable.baseline_add_photo_alternate_24)
        }

    }

    private fun vibrateForTwoSeconds() {
        // Check if the device supports vibration
        if (vibrator.hasVibrator()) {
            // Vibrate for two seconds (2000 milliseconds)
            vibrator.vibrate(100)
        }
    }

    private fun signOutGoogle() {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        // Create a GoogleSignInClient with the options specified by gso
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign out Google account
        googleSignInClient.signOut()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Google sign out successful
                    Log.d(TAG, "Google sign out successful")
                    // Clear the name, email, and profile image
                    clearAccountDetails()
                } else {
                    // Google sign out failed
                    Log.w(TAG, "Google sign out failed", task.exception)
                }
            }
    }

    private fun clearAccountDetails() {
        // Find the TextViews and ImageView by their IDs
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val phone: TextView = findViewById(R.id.phoneTextView)

        // Clear the text and image
        nameTextView.text = ""
        emailTextView.text = ""
        profileImageView.setImageDrawable(null)
        phone.text = ""
    }


    private fun shareApkLink() {
        val apkUrl = "https://mega.nz/file/TyA1TYjL#96uEVH-RM2_PdZpewfv6PPeftb9ve2umH4clkSo-bSI"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Download the imSAFE app from: $apkUrl")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }


}