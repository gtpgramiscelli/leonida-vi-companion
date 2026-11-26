package com.leonida.gtavicompanion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.UpdateUiState
import com.leonida.gtavicompanion.ui.theme.ViceCardSurface
import com.leonida.gtavicompanion.ui.theme.ViceDangerRed
import com.leonida.gtavicompanion.ui.theme.ViceDangerSoft
import com.leonida.gtavicompanion.ui.theme.ViceDeepSurface
import com.leonida.gtavicompanion.ui.theme.ViceMidnight
import com.leonida.gtavicompanion.ui.theme.ViceNeonPink
import com.leonida.gtavicompanion.ui.theme.ViceOceanCyan
import com.leonida.gtavicompanion.ui.theme.ViceOutline
import com.leonida.gtavicompanion.ui.theme.ViceOutlineSoft
import com.leonida.gtavicompanion.ui.theme.VicePalmMint
import com.leonida.gtavicompanion.ui.theme.ViceSunsetGold
import com.leonida.gtavicompanion.ui.theme.ViceSunsetOrange
import com.leonida.gtavicompanion.ui.theme.ViceTextMuted
import com.leonida.gtavicompanion.ui.theme.ViceTextPrimary
import com.leonida.gtavicompanion.ui.theme.ViceTextSecondary

@Composable
fun UpdateAvailableBanner(
    versionTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        ViceNeonPink.copy(alpha = 0.24f),
                        ViceSunsetOrange.copy(alpha = 0.20f),
                        ViceOceanCyan.copy(alpha = 0.24f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Rounded.SystemUpdate,
                contentDescription = null,
                tint = ViceSunsetGold,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Nova versão $versionTag disponível no GitHub!",
                style = MaterialTheme.typography.labelMedium,
                color = ViceTextPrimary
            )
        }
        Spacer(Modifier.width(8.dp))
        VicePillBadge(text = "ATUALIZAR", color = VicePalmMint)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBottomSheet(
    currentRepo: String,
    currentVersion: String,
    updateState: UpdateUiState,
    viewModel: CompanionViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var repoInput by remember(currentRepo) { mutableStateOf(currentRepo) }
    var invalidRepoError by remember { mutableStateOf(false) }

    LaunchedEffect(updateState) {
        if (updateState is UpdateUiState.ReadyToInstall) {
            viewModel.refreshInstallPermissionState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ViceDeepSurface,
        contentColor = ViceTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    VicePillBadge(text = "GITHUB RELEASES • AUTO-UPDATE", color = ViceOceanCyan)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Atualizações do App",
                        style = MaterialTheme.typography.headlineSmall,
                        color = ViceTextPrimary
                    )
                }
                VicePillBadge(text = "v$currentVersion", color = ViceSunsetGold)
            }

            // Status panel
            when (updateState) {
                UpdateUiState.NotConfigured -> {
                    StatusPanel(
                        accent = ViceSunsetGold,
                        icon = Icons.Rounded.CloudDownload,
                        title = "Conecte seu repositório do GitHub",
                        body = "Informe abaixo seu usuario/repositorio (ex: seunome/leonida-vi). Sempre que subirmos uma nova Release lá, o app avisa automaticamente e instala em 1 toque — sem perder seu progresso!"
                    )
                }

                UpdateUiState.Idle -> {
                    StatusPanel(
                        accent = ViceOceanCyan,
                        icon = Icons.Rounded.CloudDownload,
                        title = "Pronto para verificar",
                        body = "Toque em \"Verificar agora\" para consultar a última Release em $currentRepo."
                    )
                }

                UpdateUiState.Checking -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ViceCardSurface)
                            .border(1.dp, ViceOceanCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = ViceOceanCyan,
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = "Consultando GitHub Releases...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ViceTextPrimary
                        )
                    }
                }

                is UpdateUiState.UpToDate -> {
                    StatusPanel(
                        accent = VicePalmMint,
                        icon = Icons.Rounded.CheckCircle,
                        title = "Seu app está atualizado!",
                        body = "Você já está usando a versão mais recente (v${updateState.version})."
                    )
                }

                is UpdateUiState.UpdateAvailable -> {
                    val rel = updateState.release
                    val sizeMb = if (rel.apkSizeBytes > 0) {
                        String.format("%.1f MB", rel.apkSizeBytes / (1024.0 * 1024.0))
                    } else {
                        "APK"
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(ViceCardSurface)
                            .border(1.2.dp, ViceNeonPink.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            VicePillBadge(text = "NOVA VERSÃO ${rel.tagName}", color = ViceNeonPink)
                            Text(sizeMb, style = MaterialTheme.typography.labelSmall, color = ViceTextMuted)
                        }
                        Text(
                            text = rel.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = ViceTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rel.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = ViceTextSecondary
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { viewModel.downloadUpdate(rel) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ViceNeonPink,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(19.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Baixar e Atualizar (${rel.tagName})", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                is UpdateUiState.Downloading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ViceCardSurface)
                            .border(1.dp, ViceOceanCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Baixando ${updateState.release.tagName}...",
                                style = MaterialTheme.typography.titleSmall,
                                color = ViceTextPrimary
                            )
                            Text(
                                text = "${updateState.progressPercent}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = ViceOceanCyan
                            )
                        }
                        LinearProgressIndicator(
                            progress = { updateState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = ViceOceanCyan,
                            trackColor = ViceMidnight
                        )
                    }
                }

                is UpdateUiState.ReadyToInstall -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(ViceCardSurface)
                            .border(1.2.dp, VicePalmMint.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.InstallMobile, contentDescription = null, tint = VicePalmMint)
                            Text(
                                text = "Download concluído (${updateState.release.tagName})!",
                                style = MaterialTheme.typography.titleSmall,
                                color = VicePalmMint
                            )
                        }
                        if (updateState.needsUnknownSourcesPermission) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ViceSunsetGold.copy(alpha = 0.12f))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Rounded.Security,
                                    contentDescription = null,
                                    tint = ViceSunsetGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Primeira vez atualizando: o Android vai pedir para ativar \"Permitir desta fonte\" para o Leonida VI. Depois volte e toque no botão abaixo!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ViceSunsetGold
                                )
                            }
                        } else {
                            Text(
                                text = "Seu progresso de 100%, segredos e capítulo atual serão mantidos intactos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ViceTextSecondary
                            )
                        }
                        Button(
                            onClick = { viewModel.launchInstall(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VicePalmMint,
                                contentColor = ViceMidnight
                            )
                        ) {
                            Icon(Icons.Rounded.InstallMobile, contentDescription = null, modifier = Modifier.size(19.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (updateState.needsUnknownSourcesPermission) {
                                    "Permitir e Instalar Atualização"
                                } else {
                                    "Instalar Atualização Agora"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                is UpdateUiState.Error -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ViceDangerSoft)
                            .border(1.dp, ViceDangerRed.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = ViceDangerRed)
                        Text(
                            text = updateState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = ViceTextPrimary
                        )
                    }
                }
            }

            // Repo configuration section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ViceCardSurface)
                    .border(1.dp, ViceOutlineSoft, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Repositório GitHub (público)",
                    style = MaterialTheme.typography.titleSmall,
                    color = ViceTextPrimary
                )
                OutlinedTextField(
                    value = repoInput,
                    onValueChange = {
                        repoInput = it
                        invalidRepoError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("ex: seuusuario/leonida-vi", color = ViceTextMuted) },
                    isError = invalidRepoError,
                    supportingText = if (invalidRepoError) {
                        { Text("Use usuario/repositorio ou cole a URL do GitHub", color = ViceDangerRed) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ViceOceanCyan,
                        unfocusedBorderColor = ViceOutline,
                        focusedContainerColor = ViceMidnight,
                        unfocusedContainerColor = ViceMidnight,
                        focusedTextColor = ViceTextPrimary,
                        unfocusedTextColor = ViceTextPrimary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val ok = viewModel.saveGithubRepo(repoInput)
                            invalidRepoError = !ok
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salvar Repo", color = ViceOceanCyan)
                    }
                    Button(
                        onClick = {
                            if (repoInput.trim() != currentRepo) {
                                val ok = viewModel.saveGithubRepo(repoInput)
                                invalidRepoError = !ok
                            } else {
                                viewModel.checkForUpdate(silent = false)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ViceOceanCyan,
                            contentColor = ViceMidnight
                        )
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Verificar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPanel(
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, color = accent)
            Spacer(Modifier.height(3.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, color = ViceTextSecondary)
        }
    }
}
