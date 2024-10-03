package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.home.presentation.ui.BottomNavigationBar


@Composable
fun HomeScreen(navController: NavController) {
   Scaffold (
       topBar = {
           StatusBar()
       },
       bottomBar = {
           BottomNavigationBar()
       }
   ){ padding ->//this lambda function is used to pass padding to the children from the parent defined
       // in the scaffold to prevent the children from overlapping the bottom navigation bar and the status bar
       Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(padding)
         ) {

         }


    }
}



@Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    HomeScreen(navController)
}