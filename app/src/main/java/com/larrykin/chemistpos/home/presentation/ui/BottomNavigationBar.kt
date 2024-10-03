package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.larrykin.chemistpos.home.data.BottomNavigationBarItems

val items = listOf(
    BottomNavigationBarItems(
        title = "Home",
        icon = Icons.Rounded.Home
    ),
    BottomNavigationBarItems(
        title = "Inventory",
        icon = Icons.Rounded.Edit
    ),
    BottomNavigationBarItems(
        title = "Sales",
        icon = Icons.Rounded.ShoppingCart
    ),
    BottomNavigationBarItems(
        title = "Orders",
        icon = Icons.Rounded.Place
    ),
    BottomNavigationBarItems(
        title = "Menu",
        icon = Icons.Rounded.Menu
    ),
)

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            items.forEachIndexed { index, bottomNavigationBarItems ->
                NavigationBarItem(
                    selected = index == 0,
                    onClick = { /*TODO*/ },
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
    BottomNavigationBar()
}