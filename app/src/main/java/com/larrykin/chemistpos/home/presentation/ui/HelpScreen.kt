package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
fun HelpScreen() {
    val uriHandler = LocalUriHandler.current

    // Hyperlinks
//    val whatsappUrl = "https://wa.me/254748590146"
    val whatsappUrl = "https://wa.me/message/IHXTOP6QANKBG1"
    val email = "mailto:kinuthialawrence343@gmail.com"
    val websiteUrl = "https://larrykin343.onrender.com"

    val whatsappLink = createHyperlink("Available on our Whatsapp.", whatsappUrl)
    val emailLink = createHyperlink("kinuthialawrence343@gmail.com", email)
    val websiteLink = createHyperlink("larrykin343 Technologies", websiteUrl)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Help Documentation 💁‍♀️",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Developer Info
            Text(text = "Developer: +254 748 590 146", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            ClickableText(
                text = whatsappLink,
                onClick = { handleHyperlinkClick(uriHandler, whatsappLink) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // First Time Registration Instructions
            Text(text = "First-Time Registration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "1. Go to the Register page and enter your email.")
            Text(text = "2. Click 'Send Code' and contact the developer at +254 748 590 146 to receive the code.")
            Text(text = "3. Enter the code sent by the developer and create your account.")
            Text(text = "4. Note: The first registered user becomes the Super Admin with admin privileges.")
            Text(text = "5. To register other users, follow the same steps but use the Admin's email. The code will be sent to the Admin, who will verify it and complete the registration.")
            Text(text = "6. All subsequent users will have the 'user' role without rights to create or delete accounts.", modifier = Modifier.padding(bottom = 16.dp))

            // Login Instructions
            Text(text = "Login Instructions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "1. Open the Application: Launch the Chemist POS app.")
            Text(text = "2. Enter Your Credentials:")
            Text(text = "    • Username: Enter your registered email.")
            Text(text = "    • Password: Enter your password.")
            Text(text = "3. Click on the Login Button: Press Login to access the system.")
            Text(text = "4. Encountering Issues?:")
            Text(text = "    • Ensure your email and password are correct.")
            Text(text = "    • Check the error message in red and follow the instructions.")
            Text(text = "    • If issues persist, contact support (details below).", modifier = Modifier.padding(bottom = 16.dp))

            // Password Reset Instructions
            Text(text = "Forgotten Password?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "1. Enter the Admin's email on the 'Forgot Password' page.")
            Text(text = "2. Click 'Send Code' to receive a reset code via email.")
            Text(text = "3. Enter the code sent to the Admin.")
            Text(text = "4. If the code is correct, you can change the password.", modifier = Modifier.padding(bottom = 16.dp))

            // Contact Information
            Text(text = "Contact Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            ClickableText(
                text = emailLink,
                onClick = { handleHyperlinkClick(uriHandler, emailLink) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ClickableText(
                text = whatsappLink,
                onClick = { handleHyperlinkClick(uriHandler, whatsappLink) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ClickableText(
                text = websiteLink,
                onClick = { handleHyperlinkClick(uriHandler, websiteLink) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Security Tips
            Text(text = "Security Tips", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "• Choose a strong password with at least 8 characters.")
            Text(text = "• Do not share your credentials.")
            Text(text = "• Recognize phishing attempts and avoid suspicious links.")
            Text(text = "• Update your password regularly.")
            Text(text = "• Enable Two-Factor Authentication (2FA).", modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
fun createHyperlink(text: String, url: String): AnnotatedString {
    return AnnotatedString.Builder().apply {
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic
            )
        ) {
            append(text)
        }
        addStringAnnotation(tag = "URL", annotation = url, start = 0, end = text.length)
    }.toAnnotatedString()
}

fun handleHyperlinkClick(uriHandler: UriHandler, annotatedString: AnnotatedString) {
    annotatedString.getStringAnnotations(tag = "URL", start = 0, end = annotatedString.length)
        .firstOrNull()?.let { annotation ->
            uriHandler.openUri(annotation.item)
        }
}