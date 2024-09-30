package com.larrykin.chemistpos.authentication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.larrykin.chemistpos.R
import com.larrykin.chemistpos.authentication.domain.getDeveloperWebsiteUrl
import com.larrykin.chemistpos.core.naviagation.Screen
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000) // Display splash screen for 3 seconds
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true } // Avoid returning to splash
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SplashImage(modifier = Modifier.weight(0.75f))
        SplashText( modifier = Modifier.weight(0.25f))
    }

}


@Composable
fun SplashImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.splash_background),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )

}

@Composable
fun SplashText(modifier: Modifier=Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Pos",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Developed by",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                color = Color.White,
                fontStyle = FontStyle.Italic
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))

        //Hyperlink to the developer's website
        val uriHandler = LocalUriHandler.current
        val annotatedString = remember {
            AnnotatedString.Builder().apply {
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("larrykin343 Technologies")
                }
                addStringAnnotation(
                    tag = "URL",
                    annotation = getDeveloperWebsiteUrl(),
                    start = 0,
                    end = "larrykin343 Technologies".length
                )
            }.toAnnotatedString()
        }
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
            }
        )
    }
}