package com.larrykin.chemistpos.core.domain

import android.util.Log
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

class CodeGenerator @Inject constructor(
    private val userRepository: UserRepository
) {
    private var generatedCode: String? = null

    // Generate and send code to admin email
    fun generateAndSendCode(scope: CoroutineScope, adminEmail: String, onResult: (String) -> Unit) {
        scope.launch {
            try {
                val adminEmailLower = adminEmail.trim().lowercase()

                generatedCode = Random.nextInt(100000, 999999).toString()
                Log.d("CodeGenerator", "Generated code $generatedCode")

                // Get all users
                val allUsers: List<User> =
                    when (val result = userRepository.getAllUsers().firstOrNull()) {
                        is GetAllUsersResult.Success -> result.users
                        else -> emptyList()
                    }
                // Find admin user
                val adminUser = allUsers.find { it.role == Role.ADMIN }

                if (adminUser == null) {
                    // No admin user exists, send code to developer email
                    sendEmail(
                        "kinuthialawrence343@gmail.com",
                        "Verification Code",
                        "Your verification code is $generatedCode",
                        { result: String ->
                            onResult("$result. The code has been sent to Developer at kinuthialawrence343@gmail.com, contact him on +254748590146 to get the code")
                        }
                    )
                    Log.d("CodeGenerator", "No admin user exists, sending code to developer")
                } else {
                    val user = allUsers.find { it.email == adminEmailLower }
                    if (user?.role == Role.ADMIN) {
                        // Email has role ADMIN, send code to this email
                        sendEmail(
                            adminEmailLower,
                            "Verification Code",
                            "Your verification code is $generatedCode",
                            onResult
                        )
                        Log.d("CodeGenerator", "Sending code to admin $adminEmailLower")
                    } else {
                        // Email does not have role ADMIN, alert the admin
                        sendEmail(
                            adminUser.email,
                            "Alert",
                            "Someone tried to register or change password with email $adminEmailLower",
                            { result: String ->
                                onResult("Error: The email entered does not have admin privileges")
                            }
                        )
                        Log.d("CodeGenerator", "Email $adminEmailLower does not have admin privileges")
                    }
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
                Log.e("CodeGenerator", "Error generating and sending code", e)
            }
        }
    }

    // Function to send email
    private fun sendEmail(to: String, subject: String, body: String, onResult: (String) -> Unit, followUpMessage: String? = null) {
        val username = "kinuthialawrence343@gmail.com"
        val password = "jlan vjur jayj jsoy" // App password

        // Set email properties
        val props = System.getProperties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
            put("mail.smtp.ssl.protocols", "TLSv1.2")
        }

        // Create session
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create message
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(username))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    this.subject = subject
                    setText(body)
                }

                Transport.send(message)
                Log.d("CodeGenerator", "Email sent successfully to $to")
                val resultMessage = "Code sent to $to"
                onResult(followUpMessage?.let { "$resultMessage, $it" } ?: resultMessage)
            } catch (e: UnknownHostException) {
                Log.e("CodeGenerator", "Unknown host: ${e.message}")
                onResult("Error: Unknown host - ${e.message}")
            } catch (e: MessagingException) {
                Log.e("CodeGenerator", "Error sending email", e)
                onResult("Error: Check Your Network. ${e.message}")
            } catch (e: Exception) {
                Log.e("CodeGenerator", "Unexpected error", e)
                onResult("Error: Unexpected error - ${e.message}")
            }
        }
    }

    // Verify the input code
    fun verifyCode(inputCode: String, onResult: (Boolean) -> Unit) {
        onResult(inputCode == generatedCode)
    }
}