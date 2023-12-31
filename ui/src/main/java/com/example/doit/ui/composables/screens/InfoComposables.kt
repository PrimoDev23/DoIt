package com.example.doit.ui.composables.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.doit.common.R
import com.example.doit.ui.composables.DrawerMenuButton
import com.example.doit.ui.composables.RootScaffold
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph


private const val GITHUB_URL = "https://github.com/PrimoDev23/DoIt"

@RootNavGraph
@Destination
@Composable
fun InfoScreen() {
    val context = LocalContext.current

    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.info_screen_title),
        navigationIcon = {
            DrawerMenuButton()
        }
    ) {
        val packageInfo = remember {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoTextSection(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.info_version_name),
                text = packageInfo.versionName
            )

            InfoTextSection(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.info_version_code),
                text = packageInfo.longVersionCode.toString()
            )

            Spacer(modifier = Modifier.weight(1f))

            InfoProfileSection(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoTextSection(
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .then(modifier)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun InfoProfileSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                    context.startActivity(browserIntent)
                },
            painter = painterResource(
                id = if (isSystemInDarkTheme()) {
                    R.drawable.github_logo_white
                } else {
                    R.drawable.github_logo
                }
            ),
            contentDescription = stringResource(id = R.string.info_screen_github)
        )
    }
}