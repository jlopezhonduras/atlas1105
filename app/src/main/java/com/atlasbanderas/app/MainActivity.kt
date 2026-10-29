package com.atlasbanderas.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import java.text.Normalizer
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AtlasTheme { AtlasApp() } }
    }
}

private val AtlasNavy = Color(0xFF0B1D35)
private val AtlasOcean = Color(0xFF0B5E75)
private val AtlasCoral = Color(0xFFE8684A)
private val AtlasPaper = Color(0xFFF7F4ED)

private val AtlasColors = lightColorScheme(
    primary = AtlasOcean,
    onPrimary = Color.White,
    secondary = AtlasCoral,
    background = AtlasPaper,
    onBackground = AtlasNavy,
    surface = Color(0xFFFFFEFA),
    onSurface = AtlasNavy,
)

@Composable
private fun AtlasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AtlasColors, content = content)
}

private enum class InfoDialog { ABOUT, TERMS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AtlasApp() {
    var query by rememberSaveable { mutableStateOf("") }
    var descending by rememberSaveable { mutableStateOf(false) }
    var visibleDialog by remember { mutableStateOf<InfoDialog?>(null) }

    val visibleCountries = remember(query, descending) {
        val normalizedQuery = query.normalized()
        CountryCatalog.countries
            .filter { it.name.normalized().contains(normalizedQuery) }
            .let { countries -> if (descending) countries.reversed() else countries }
    }

    Scaffold(
        containerColor = AtlasPaper,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.icon_atlas_banderas),
                            contentDescription = "Logo de Atlas de Banderas 1105",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(34.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Atlas de Banderas", fontWeight = FontWeight.ExtraBold, color = AtlasNavy)
                    }
                },
                actions = {
                    TextButton(onClick = { visibleDialog = InfoDialog.ABOUT }) { Text("Acerca de") }
                    TextButton(onClick = { visibleDialog = InfoDialog.TERMS }) { Text("Términos") }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AtlasPaper),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AtlasHero()
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Buscar un país") },
                    placeholder = { Text("Ej. Guatemala") },
                    leadingIcon = { Text("⌕", fontSize = 26.sp, color = AtlasOcean) },
                    shape = RoundedCornerShape(16.dp),
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${visibleCountries.size} ${if (visibleCountries.size == 1) "país" else "países"}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextButton(onClick = { descending = !descending }) {
                        Text(if (descending) "Orden: Z–A" else "Orden: A–Z")
                    }
                }
            }
            if (visibleCountries.isEmpty()) {
                item { EmptyState() }
            } else {
                items(visibleCountries, key = { it.code }) { country ->
                    CountryCard(country)
                }
            }
        }
    }

    visibleDialog?.let { dialog ->
        AtlasDialog(dialog) { visibleDialog = null }
    }
}

@Composable
private fun AtlasHero() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AtlasNavy,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("UN MAPA PARA EXPLORAR", color = Color(0xFFF6CA79), fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.3.sp)
            Spacer(Modifier.height(9.dp))
            Text("Todas las banderas.\nUn solo atlas.", color = Color.White, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Text("Toca una bandera para abrir búsquedas del país en Google, TikTok o YouTube.", color = Color(0xFFC9D5E4), lineHeight = 21.sp)
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBadge("195", "países")
                StatBadge("3", "búsquedas")
            }
        }
    }
}

@Composable
private fun StatBadge(number: String, label: String) {
    Surface(color = Color.White.copy(alpha = .11f), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)) {
            Text(number, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
            Text(label, color = Color(0xFFC9D5E4), fontSize = 11.sp)
        }
    }
}

@Composable
private fun CountryCard(country: Country) {
    var expanded by rememberSaveable(country.code) { mutableStateOf(false) }
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https://flagcdn.com/w320/${country.code}.png")
                        .build(),
                    contentDescription = "Bandera de ${country.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 76.dp, height = 56.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFFE6ECEF))
                        .clickable(role = Role.Button) { expanded = !expanded },
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(country.name, color = AtlasNavy, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(country.code.uppercase(Locale.ROOT), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 1.1.sp)
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Ocultar" else "Buscar")
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column {
                    Spacer(Modifier.height(13.dp))
                    HorizontalDivider(color = Color(0xFFE0E5E2))
                    Spacer(Modifier.height(13.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SearchButton(SearchSite.GOOGLE, Modifier.weight(1f)) { context.openSearch("google", country.name) }
                        SearchButton(SearchSite.TIKTOK, Modifier.weight(1f)) { context.openSearch("tiktok", country.name) }
                        SearchButton(SearchSite.YOUTUBE, Modifier.weight(1f)) { context.openSearch("youtube", country.name) }
                    }
                }
            }
        }
    }
}

private enum class SearchSite(val label: String) {
    GOOGLE("Google"),
    TIKTOK("TikTok"),
    YOUTUBE("YouTube"),
}

@Composable
private fun SearchButton(site: SearchSite, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .semantics { contentDescription = "Buscar en ${site.label}" },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AtlasOcean),
    ) {
        BrandIcon(site)
    }
}

@Composable
private fun BrandIcon(site: SearchSite) {
    Canvas(modifier = Modifier.size(24.dp)) {
        when (site) {
            SearchSite.GOOGLE -> drawGoogleMark()
            SearchSite.TIKTOK -> drawTikTokMark()
            SearchSite.YOUTUBE -> drawYouTubeMark()
        }
    }
}

private fun DrawScope.drawGoogleMark() {
    val stroke = 3.3.dp.toPx()
    val inset = stroke / 2f
    val arcSize = Size(size.width - stroke, size.height - stroke)
    val arcTopLeft = Offset(inset, inset)
    val outline = Stroke(width = stroke, cap = StrokeCap.Round)
    drawArc(Color(0xFF4285F4), -38f, 83f, false, arcTopLeft, arcSize, style = outline)
    drawArc(Color(0xFF34A853), 45f, 77f, false, arcTopLeft, arcSize, style = outline)
    drawArc(Color(0xFFFBBC05), 122f, 83f, false, arcTopLeft, arcSize, style = outline)
    drawArc(Color(0xFFEA4335), 205f, 117f, false, arcTopLeft, arcSize, style = outline)
    drawLine(
        color = Color(0xFF4285F4),
        start = Offset(size.width * .50f, size.height * .50f),
        end = Offset(size.width - inset, size.height * .50f),
        strokeWidth = stroke,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawTikTokMark() {
    fun note(color: Color, shift: Float) {
        val stroke = size.width * .16f
        drawLine(
            color = color,
            start = Offset(size.width * .60f + shift, size.height * .14f),
            end = Offset(size.width * .60f + shift, size.height * .68f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * .60f + shift, size.height * .16f),
            end = Offset(size.width * .87f + shift, size.height * .27f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(color, radius = size.width * .20f, center = Offset(size.width * .37f + shift, size.height * .72f))
    }
    note(Color(0xFF25F4EE), -1.8.dp.toPx())
    note(Color(0xFFFE2C55), 1.8.dp.toPx())
    note(Color(0xFF111111), 0f)
}

private fun DrawScope.drawYouTubeMark() {
    val top = size.height * .20f
    val iconHeight = size.height * .60f
    drawRoundRect(
        color = Color(0xFFFF0033),
        topLeft = Offset(0f, top),
        size = Size(size.width, iconHeight),
        cornerRadius = CornerRadius(size.height * .18f, size.height * .18f),
    )
    val triangle = Path().apply {
        moveTo(size.width * .43f, size.height * .34f)
        lineTo(size.width * .43f, size.height * .66f)
        lineTo(size.width * .70f, size.height * .50f)
        close()
    }
    drawPath(triangle, Color.White)
}

@Composable
private fun EmptyState() {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Color.White) {
        Column(modifier = Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No encontramos ese país", color = AtlasNavy, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Prueba con otro nombre o borra la búsqueda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AtlasDialog(dialog: InfoDialog, dismiss: () -> Unit) {
    val (title, body) = when (dialog) {
        InfoDialog.ABOUT -> "Acerca de" to "Atlas de Banderas 1105 es una guía visual para explorar las banderas de 195 países en español. Al tocar una bandera aparecen enlaces para buscar ese país en Google, TikTok y YouTube."
        InfoDialog.TERMS -> "Términos y condiciones" to "Este borrador tiene fines informativos y educativos. Las búsquedas se abren en servicios de terceros y se rigen por sus términos. Las banderas se muestran para identificar países; no representan afiliación, patrocinio ni una posición política adicional."
    }
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(title, color = AtlasNavy, fontWeight = FontWeight.ExtraBold) },
        text = { Text(body, lineHeight = 21.sp) },
        confirmButton = { TextButton(onClick = dismiss) { Text("Entendido") } },
    )
}

private fun String.normalized(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.ROOT)

private fun Context.openSearch(service: String, countryName: String) {
    val query = Uri.encode("$countryName país")
    val url = when (service) {
        "google" -> "https://www.google.com/search?q=$query"
        "tiktok" -> "https://www.tiktok.com/search?q=$query"
        else -> "https://www.youtube.com/results?search_query=$query"
    }
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
