package com.example.bureaudevotemsa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import androidx.compose.material3.Text
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.produceState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération des données
        val nom = intent.getStringExtra("EXTRA_NOM")
        val prenom = intent.getStringExtra("EXTRA_PRENOM")
        val dateNaissance = intent.getStringExtra("EXTRA_DATE_NAISSANCE")

        // Test Recupération de données

        setContent {
            SecondScreen()
        }
    }
}

@Serializable
data class Electeurs(
    @SerialName("nom de naissance")
    val nomDeNaissance: String,
    @SerialName("nom d'usage")
    val nomUsage: String? = null,
    @SerialName("prénoms")
    val prenoms: String? = null,
    @SerialName("date de naissance")
    val dateDeNaissance: String? = null,
    @SerialName("code du bureau de vote")
    val codeBureauVote: Int,
    @SerialName("libellé du bureau de vote")
    val libelleBureauVote: String
)

fun chargerElecteurs(context: Context): List<Electeurs> {
    return try {
        val jsonString = context.assets.open("electeurs.json")
            .bufferedReader()
            .use { it.readText() }

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }

        json.decodeFromString<List<Electeurs>>(jsonString)
    } catch (e: Exception) {
        Log.e("JSON", "Erreur de chargement JSON : ${e.message}")
        e.printStackTrace()
        emptyList()
    }
}

/** Recherche par debut de nom, prenom partiel et annee/date de naissance. */
fun searchElecteur(
    electeurs: List<Electeurs>,
    nom: String?,
    prenom: String?,
    dateNaissance: String?
): List<Electeurs> {
    val nomQuery = normaliserRecherche(nom.orEmpty())
    val prenomQuery = normaliserRecherche(prenom.orEmpty())
    val dateQuery = dateNaissance.orEmpty().trim()
    if (nomQuery.isEmpty() && prenomQuery.isEmpty() && dateQuery.isEmpty()) return emptyList()

    return electeurs.filter { e ->
        val nomOk = nomQuery.isEmpty() ||
            normaliserRecherche(e.nomDeNaissance).startsWith(nomQuery) ||
            normaliserRecherche(e.nomUsage.orEmpty()).startsWith(nomQuery)
        val prenomOk = prenomQuery.isEmpty() ||
            normaliserRecherche(e.prenoms.orEmpty()).contains(prenomQuery)
        val dateOk = dateQuery.isEmpty() || correspondDate(e.dateDeNaissance, dateQuery)
        nomOk && prenomOk && dateOk
    }
}

internal fun normaliserRecherche(value: String): String =
    java.text.Normalizer.normalize(value.trim(), java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(java.util.Locale.ROOT)
        .replace(Regex("[-'’\\s]+"), " ")

/** Accepte l'annee AAAA ou une date complete JJ/MM/AAAA. */
internal fun correspondDate(dateElecteur: String?, saisie: String): Boolean {
    val source = dateElecteur.orEmpty().replace('\\', '/').trim()
    val query = saisie.trim().replace('\\', '/')
    if (query.matches(Regex("\\d{4}"))) return source.takeLast(4) == query
    if (!query.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) return false
    return source == query
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen() {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val nom = activity.intent.getStringExtra("EXTRA_NOM")
    val prenom = activity.intent.getStringExtra("EXTRA_PRENOM")
    val naissance = activity.intent.getStringExtra("EXTRA_DATE_NAISSANCE")
    val electeurs by produceState<List<Electeurs>?>(initialValue = null) {
        value = withContext(Dispatchers.IO) { chargerElecteurs(context) }
    }
    com.example.bureaudevotemsa.ui.theme.BureauDeVoteMSATheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
            TopAppBar(
                title = { Text("Résultats de recherche", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour à la recherche")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }) { insets ->
            when (val data = electeurs) {
                null -> Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> {
                    val resultats = remember(data, nom, prenom, naissance) { searchElecteur(data, nom, prenom, naissance) }
                    Column(Modifier.fillMaxSize().padding(insets)) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp)) {
                            Text("${resultats.size} résultat${if (resultats.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(5.dp))
                            Text("Vérifiez l'identité avant de communiquer le bureau de vote.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = com.example.bureaudevotemsa.ui.theme.Muted)
                        }
                        if (resultats.isEmpty()) {
                            Column(Modifier.fillMaxSize().padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Aucun électeur trouvé", style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(10.dp))
                                Text("Vérifiez l'orthographe ou essayez moins de lettres.",
                                    textAlign = TextAlign.Center,
                                    color = com.example.bureaudevotemsa.ui.theme.Muted)
                                Spacer(Modifier.height(20.dp))
                                Button(onClick = { activity.finish() }) { Text("Modifier la recherche") }
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(resultats) { e -> ElecteurCard(e) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElecteurCard(e: Electeurs) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text(e.nomDeNaissance.uppercase(java.util.Locale.ROOT),
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(e.prenoms.orEmpty().ifBlank { "Prénom non renseigné" },
                style = MaterialTheme.typography.bodyLarge)
            if (!e.nomUsage.isNullOrBlank() && e.nomUsage != e.nomDeNaissance) {
                Text("Nom d'usage : ${e.nomUsage}", style = MaterialTheme.typography.bodySmall,
                    color = com.example.bureaudevotemsa.ui.theme.Muted)
            }
            Text("Né(e) le ${e.dateDeNaissance ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = com.example.bureaudevotemsa.ui.theme.Muted)
            Spacer(Modifier.height(16.dp))
            Divider(color = com.example.bureaudevotemsa.ui.theme.Line)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer) {
                    Column(Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BUREAU", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Text(e.codeBureauVote.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Bureau de vote", style = MaterialTheme.typography.labelMedium,
                        color = com.example.bureaudevotemsa.ui.theme.Muted)
                    Text(e.libelleBureauVote, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
