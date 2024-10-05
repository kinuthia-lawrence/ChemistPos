package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.home.data.BottomNavigationBarItems

val items = listOf(
    BottomNavigationBarItems(
        title = "Dashboard",
        icon = Icons.Rounded.Home,
        route = "dashboard"
    ),
    BottomNavigationBarItems(
        title = "Sales",
        icon = Icons.AutoMirrored.Rounded.Send,
        route = "sales"
    ),
    BottomNavigationBarItems(
        title = "Stock",
        icon = Icons.Rounded.AddCircle,
        route = "stock"
    ),
    BottomNavigationBarItems(
        title = "Services",
        icon = Icons.Rounded.Build,
        route = "services"
    ),
    BottomNavigationBarItems(
        title = "Cart",
        icon = Icons.Rounded.ShoppingCart,
        route = "cart"
    )
)

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEach { bottomNavigationBarItems ->
                NavigationBarItem(
                    selected = currentRoute == bottomNavigationBarItems.route,
                    onClick = {
                        if (currentRoute != bottomNavigationBarItems.route) {
                            navController.navigate(bottomNavigationBarItems.route) {
                                // Pop up to the start of the graph to avoid building up a backstack
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = bottomNavigationBarItems.icon,
                            contentDescription = bottomNavigationBarItems.title,
                            modifier = Modifier,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = {
                        Text(
                            text = bottomNavigationBarItems.title,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = rememberNavController(), currentRoute = "dashboard")
}
