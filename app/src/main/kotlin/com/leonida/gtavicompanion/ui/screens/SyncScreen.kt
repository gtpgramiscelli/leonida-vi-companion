package com.leonida.gtavicompanion.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.model.Ps5Trophy
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.SyncUiState
import com.leonida.gtavicompanion.ui.components.ViceCard
import com.leonida.gtavicompanion.ui.components.ViceHeroCard
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.components.ViceSectionHeader
import com.leonida.gtavicompanion.ui.theme.ViceCardSurface
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
import java.io.File

@Composable
fun SyncScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    var psnIdInput by remember(state.psnOnlineId) { mutableStateOf(state.psnOnlineId) }
    var showCameraScanner by remember { mutableStateOf(false) }
    var showRawOcrText by remember { mutableStateOf(false) }
    var cameraPermissionDenied by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraPermissionDenied = false
            showCameraScanner = true
        } else {
            cameraPermissionDenied = true
        }
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            showRawOcrText = false
            viewModel.scanImageUri(
                uri = uri,
                sourceLabel = "Print do PS App / Galeria",
                isCameraCapture = false
            )
        }
    }

    fun openCameraWithPermissionCheck() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            showCameraScanner = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (showCameraScanner) {
        LiveTvCameraScanner(
            onCaptured = { uri ->
                showCameraScanner = false
                showRawOcrText = false
                viewModel.scanImageUri(
                    uri = uri,
                    sourceLabel = "Câmera na TV (OCR)",
                    isCameraCapture = true
                )
            },
            onCancel = { showCameraScanner = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ViceMidnight)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ViceDeepSurface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Voltar",
                    tint = ViceTextPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sincronização PS5",
                    style = MaterialTheme.typography.titleLarge,
                    color = ViceTextPrimary
                )
                Text(
                    text = "Troféus PSN + Scanner de Câmera na TV (Soma Automática)",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceOceanCyan
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ViceOutlineSoft)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. COMBINED PRECISION HERO
            item {
                ViceHeroCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        VicePillBadge(text = "SOMA CUMULATIVA • PS5", color = VicePalmMint)
                        Text(
                            text = "${state.unlockedTrophyIds.size}/${viewModel.totalMappedTrophiesCount} marcos • ${state.cameraScansCount} scans",
                            style = MaterialTheme.typography.labelSmall,
                            color = ViceTextSecondary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Precisão Máxima na Sua Jogatina",
                        style = MaterialTheme.typography.titleLarge,
                        color = ViceTextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "O app soma tudo automaticamente: seus troféus do PS5 + fotos dos menus na TV + prints do PlayStation App + suas marcações manuais. Nada que você já conquistou é apagado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ViceTextSecondary
                    )
                    if (state.lastSyncSummary.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ViceMidnight.copy(alpha = 0.72f))
                                .border(1.dp, VicePalmMint.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Última sincronização: ${state.lastSyncSummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = VicePalmMint
                            )
                        }
                    }
                }
            }

            // 2. LIVE SYNC / OCR FEEDBACK CARD (Preview Confirmation, Progress, or Result)
            item {
                when (val s = syncState) {
                    SyncUiState.Idle -> Unit

                    SyncUiState.ScanningOcr, SyncUiState.SyncingPsn -> {
                        ViceCard(accentBorder = ViceOceanCyan.copy(alpha = 0.6f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = ViceOceanCyan,
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = if (s is SyncUiState.ScanningOcr) {
                                        "Lendo números e troféus da imagem (OCR offline)..."
                                    } else {
                                        "Consultando perfil público PSN..."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ViceTextPrimary
                                )
                            }
                        }
                    }

                    is SyncUiState.PreviewDetection -> {
                        val det = s.detection
                        ViceCard(accentBorder = VicePalmMint.copy(alpha = 0.75f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VicePillBadge(text = "LEITURA RECONHECIDA!", color = VicePalmMint)
                                Text(det.sourceLabel, style = MaterialTheme.typography.labelSmall, color = ViceTextMuted)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Confira o que encontramos na tela:",
                                style = MaterialTheme.typography.titleSmall,
                                color = ViceTextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            det.highlights.forEach { bullet ->
                                Text(
                                    text = "• $bullet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VicePalmMint
                                )
                            }
                            if (!det.rawOcrText.isNullOrBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (showRawOcrText) "Ocultar texto lido" else "Ver texto bruto lido pela câmera",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ViceOceanCyan,
                                    modifier = Modifier.clickable { showRawOcrText = !showRawOcrText }
                                )
                                AnimatedVisibility(visible = showRawOcrText) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ViceMidnight)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = det.rawOcrText.take(600),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ViceTextMuted
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.dismissSyncFeedback() },
                                    modifier = Modifier.weight(0.85f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Descartar", color = ViceTextSecondary)
                                }
                                Button(
                                    onClick = {
                                        viewModel.confirmAndApplyDetection(det, s.isCameraCapture)
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VicePalmMint,
                                        contentColor = ViceMidnight
                                    )
                                ) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Somar ao Progresso", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }

                    is SyncUiState.AppliedSuccess -> {
                        ViceCard(accentBorder = VicePalmMint.copy(alpha = 0.7f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = VicePalmMint)
                                    Text(s.title, style = MaterialTheme.typography.titleSmall, color = VicePalmMint)
                                }
                                TextButton(onClick = { viewModel.dismissSyncFeedback() }) {
                                    Text("OK", color = ViceTextSecondary)
                                }
                            }
                            s.changes.forEach { ch ->
                                Text("• $ch", style = MaterialTheme.typography.bodySmall, color = ViceTextPrimary)
                            }
                        }
                    }

                    is SyncUiState.InfoMessage -> {
                        ViceCard(accentBorder = ViceSunsetGold.copy(alpha = 0.6f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.Info, contentDescription = null, tint = ViceSunsetGold)
                                    Text(s.title, style = MaterialTheme.typography.titleSmall, color = ViceSunsetGold)
                                }
                                TextButton(onClick = { viewModel.dismissSyncFeedback() }) {
                                    Text("Fechar", color = ViceTextSecondary)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(s.message, style = MaterialTheme.typography.bodySmall, color = ViceTextSecondary)
                        }
                    }
                }
            }

            // 3. OPÇÃO 1: CÂMERA NA TV + PRINT DO PS APP (COM INSTRUÇÕES DETALHADAS!)
            item {
                ViceSectionHeader(
                    title = "1. Scanner pela Câmera na TV (OCR)",
                    subtitle = "Lê contadores exatos e troféus direto da TV ou de prints do PS App — 100% offline"
                )
                Spacer(Modifier.height(10.dp))
                ViceCard(accentBorder = ViceNeonPink.copy(alpha = 0.45f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.Tv, contentDescription = null, tint = ViceNeonPink, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Como usar a câmera para máxima precisão:",
                            style = MaterialTheme.typography.titleSmall,
                            color = ViceNeonPink
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    InstructionStep(
                        number = "1",
                        title = "Qual menu abrir no seu PS5",
                        detail = "• Progresso & Contadores: aperte Options no DualSense → Progresso → Estatísticas / 100% / História (lê capítulo, WAiNK, animais, Stick Ups, estande de tiro, bancos e gangues).\n• Troféus: aperte o Botão PS → Troféus → Grand Theft Auto VI (ou tire um print no PlayStation App no celular!).",
                        accent = ViceOceanCyan
                    )
                    Spacer(Modifier.height(8.dp))
                    InstructionStep(
                        number = "2",
                        title = "Enquadre a TV sem reflexo",
                        detail = "Fique de frente para a TV a uns 60–100 cm, mantenha a lista dentro da moldura neon da câmera e deixe o flash DESLIGADO (o flash dá clarão na tela e atrapalha a leitura).",
                        accent = ViceSunsetGold
                    )
                    Spacer(Modifier.height(8.dp))
                    InstructionStep(
                        number = "3",
                        title = "Pode escanear várias abas seguidas!",
                        detail = "Cada foto soma com a anterior e com seus troféus PSN. O app sempre mostra uma prévia do que leu antes de somar ao seu progresso.",
                        accent = VicePalmMint
                    )

                    if (cameraPermissionDenied) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Permissão de câmera negada. Libere a câmera nas configurações do Android ou use o botão \"Importar Print (PS App)\" abaixo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ViceSunsetOrange
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { openCameraWithPermissionCheck() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ViceNeonPink,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Apontar Câmera para a TV", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Rounded.PhotoLibrary,
                            contentDescription = null,
                            tint = ViceOceanCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Importar Print do PS App / Galeria",
                            style = MaterialTheme.typography.labelLarge,
                            color = ViceOceanCyan
                        )
                    }
                }
            }

            // 4. OPÇÃO 2: SINCRONIZAÇÃO DE TROFÉUS PSN (PS5)
            item {
                ViceSectionHeader(
                    title = "2. Sincronização de Troféus PS5 (PSN)",
                    subtitle = "Conecta sua PSN ID pública e também permite marcar troféus com soma automática"
                )
                Spacer(Modifier.height(10.dp))
                ViceCard(accentBorder = ViceOceanCyan.copy(alpha = 0.45f)) {
                    Text(
                        text = "Sua PSN Online ID (Perfil Público)",
                        style = MaterialTheme.typography.titleSmall,
                        color = ViceTextPrimary
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Dica no PS5: Configurações > Usuários e Contas > Privacidade > Histórico de Jogos = \"Qualquer pessoa\".",
                        style = MaterialTheme.typography.bodySmall,
                        color = ViceTextMuted
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = psnIdInput,
                        onValueChange = { psnIdInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ex: SeuNickPSN", color = ViceTextMuted) },
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
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.savePsnIdAndSync(psnIdInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ViceOceanCyan,
                            contentColor = ViceMidnight
                        )
                    ) {
                        Icon(Icons.Rounded.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sincronizar Perfil Público PSN", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // 5. TROPHY LIST WITH ANTI-SPOILER SHIELD & CUMULATIVE AUTO-MAPPING
            item {
                ViceSectionHeader(
                    title = "Marcos de Troféus do PS5 (20 principais de 55)",
                    subtitle = "Ao sincronizar ou marcar um troféu, capítulo, segredos, cheats e 100% atualizam juntos!",
                    trailing = {
                        VicePillBadge(
                            text = "${state.unlockedTrophyIds.size}/${GtaViCatalog.ps5Trophies.size}",
                            color = ViceSunsetGold
                        )
                    }
                )
            }

            items(GtaViCatalog.ps5Trophies, key = { it.id }) { trophy ->
                val unlocked = trophy.id in state.unlockedTrophyIds
                val hiddenBySpoilerShield = trophy.isHiddenStory &&
                    !unlocked &&
                    trophy.minChapterVisible > state.currentChapter &&
                    state.currentChapter < 6

                TrophySyncRow(
                    trophy = trophy,
                    unlocked = unlocked,
                    hiddenBySpoilerShield = hiddenBySpoilerShield,
                    onToggle = { viewModel.togglePs5Trophy(trophy.id) }
                )
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun InstructionStep(
    number: String,
    title: String,
    detail: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ViceDeepSurface)
            .border(1.dp, ViceOutlineSoft, RoundedCornerShape(14.dp))
            .padding(11.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.2f))
                .border(1.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.Black
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = ViceTextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(detail, style = MaterialTheme.typography.bodySmall, color = ViceTextSecondary)
        }
    }
}

@Composable
private fun TrophySyncRow(
    trophy: Ps5Trophy,
    unlocked: Boolean,
    hiddenBySpoilerShield: Boolean,
    onToggle: () -> Unit
) {
    val borderColor = if (unlocked) VicePalmMint.copy(alpha = 0.55f) else ViceOutlineSoft
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (unlocked) VicePalmMint.copy(alpha = 0.08f) else ViceCardSurface)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
            .padding(13.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = when {
                unlocked -> Icons.Rounded.CheckCircle
                hiddenBySpoilerShield -> Icons.Rounded.Lock
                else -> Icons.Rounded.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = when {
                unlocked -> VicePalmMint
                hiddenBySpoilerShield -> ViceTextMuted
                else -> trophy.tier.color
            },
            modifier = Modifier
                .padding(top = 2.dp)
                .size(21.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = trophy.tier.color,
                    modifier = Modifier.size(14.dp)
                )
                VicePillBadge(text = trophy.tier.label, color = trophy.tier.color)
                if (trophy.grantsChapter != null && !hiddenBySpoilerShield) {
                    val chLabel = if (trophy.grantsChapter >= 6) "Pós-Game" else "Cap. ${trophy.grantsChapter}"
                    VicePillBadge(text = "→ $chLabel", color = ViceOceanCyan)
                }
            }
            Spacer(Modifier.height(5.dp))
            if (hiddenBySpoilerShield) {
                Text(
                    text = "Troféu Oculto de História (Protegido)",
                    style = MaterialTheme.typography.titleSmall,
                    color = ViceTextMuted
                )
                Text(
                    text = "Oculto pelo Escudo Anti-Spoiler. Sincronize pela câmera/PSN ou toque aqui se já ganhou.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextMuted
                )
            } else {
                Text(
                    text = trophy.nameEn,
                    style = MaterialTheme.typography.titleSmall,
                    color = ViceTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trophy.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextSecondary
                )
            }
        }
    }
}

@Composable
private fun LiveTvCameraScanner(
    onCaptured: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var torchEnabled by remember { mutableStateOf(false) }
    var isTakingPhoto by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    provider.unbindAll()
                    val camera = provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        capture
                    )
                    imageCapture = capture
                    boundCamera = camera
                } catch (e: Exception) {
                    cameraError = "Não foi possível abrir a câmera: ${e.message}"
                }
            },
            executor
        )
        onDispose {
            runCatching { providerFuture.get().unbindAll() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Neon TV Framing Reticle Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val left = size.width * 0.08f
            val right = size.width * 0.92f
            val top = size.height * 0.22f
            val bottom = size.height * 0.72f
            val corner = 34.dp.toPx()
            val stroke = 3.5.dp.toPx()

            // Corner brackets
            drawLine(ViceOceanCyan, Offset(left, top), Offset(left + corner, top), stroke, StrokeCap.Round)
            drawLine(ViceOceanCyan, Offset(left, top), Offset(left, top + corner), stroke, StrokeCap.Round)

            drawLine(ViceNeonPink, Offset(right, top), Offset(right - corner, top), stroke, StrokeCap.Round)
            drawLine(ViceNeonPink, Offset(right, top), Offset(right, top + corner), stroke, StrokeCap.Round)

            drawLine(ViceNeonPink, Offset(left, bottom), Offset(left + corner, bottom), stroke, StrokeCap.Round)
            drawLine(ViceNeonPink, Offset(left, bottom), Offset(left, bottom - corner), stroke, StrokeCap.Round)

            drawLine(ViceOceanCyan, Offset(right, bottom), Offset(right - corner, bottom), stroke, StrokeCap.Round)
            drawLine(ViceOceanCyan, Offset(right, bottom), Offset(right, bottom - corner), stroke, StrokeCap.Round)
        }

        // Top controls + On-screen Instructions Banner
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ViceMidnight.copy(alpha = 0.78f))
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Fechar câmera", tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ViceMidnight.copy(alpha = 0.78f))
                        .clickable {
                            val next = !torchEnabled
                            torchEnabled = next
                            boundCamera?.cameraControl?.enableTorch(next)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (torchEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                        contentDescription = "Alternar flash",
                        tint = if (torchEnabled) ViceSunsetGold else Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ViceMidnight.copy(alpha = 0.82f))
                    .border(1.dp, ViceOceanCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = cameraError
                        ?: "Enquadre o menu Progresso/Estatísticas ou Troféus na TV dentro dos cantos neon • 60–100 cm • Evite reflexo e mantenha o flash desligado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextPrimary
                )
            }
        }

        // Bottom Capture Shutter Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val capture = imageCapture ?: return@Button
                    if (isTakingPhoto) return@Button
                    isTakingPhoto = true
                    val dir = File(context.cacheDir, "ocr_captures").apply { mkdirs() }
                    val file = File(dir, "tv_scan_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                isTakingPhoto = false
                                onCaptured(Uri.fromFile(file))
                            }

                            override fun onError(exception: ImageCaptureException) {
                                isTakingPhoto = false
                                cameraError = "Erro ao capturar foto: ${exception.message}"
                            }
                        }
                    )
                },
                enabled = !isTakingPhoto && imageCapture != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ViceNeonPink,
                    contentColor = Color.White
                )
            ) {
                if (isTakingPhoto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Capturando...", style = MaterialTheme.typography.labelLarge)
                } else {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Capturar e Ler Tela do PS5", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
