package com.example.playlistmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.SwitchThumbActiveColor
import com.example.playlistmaker.ui.theme.SwitchThumbInactiveColor
import com.example.playlistmaker.ui.theme.SwitchTrackActiveColor
import com.example.playlistmaker.ui.theme.SwitchTrackInactiveColor
import com.example.playlistmaker.ui.util.AppTopBar

@Composable
fun SettingsScreen(
    isDarkModeEnabled: Boolean,
    onToggleDarkMode: () -> Unit,
    onShareApp: () -> Unit,
    onSupport: () -> Unit,
    onUserAgreement: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(false, stringResource(R.string.settings)) {}

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onToggleDarkMode() }
                .padding(horizontal = 16.dp)
                .height(61.dp)
        ) {
            Text(
                text = stringResource(R.string.dark_mode),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = { onToggleDarkMode() },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = SwitchThumbActiveColor,
                    uncheckedThumbColor = SwitchThumbInactiveColor,
                    checkedTrackColor = SwitchTrackActiveColor,
                    uncheckedTrackColor = SwitchTrackInactiveColor
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShareApp() }
                .padding(horizontal = 16.dp)
                .height(61.dp)
        ) {
            Text(
                text = stringResource(R.string.share),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground


            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_share),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSupport() }
                .padding(horizontal = 16.dp)
                .height(61.dp)
        ) {

            Text(
                text = stringResource(R.string.support),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground

            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_support),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserAgreement() }
                .padding(horizontal = 16.dp)
                .height(61.dp)
        ) {
            Text(
                text = stringResource(R.string.user_agreement),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground

            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
