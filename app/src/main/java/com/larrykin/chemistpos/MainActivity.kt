package com.larrykin.chemistpos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.core.naviagation.NavGraph
import com.larrykin.chemistpos.ui.theme.ChemistPosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChemistPosTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMain() {
    ChemistPosTheme {
        val navController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NavGraph(navController = navController)
        }
    }
}
