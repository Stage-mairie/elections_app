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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération des données
        val nom = intent.getStringExtra("EXTRA_NOM")
        val prenom = intent.getStringExtra("EXTRA_PRENOM")
        val dateNaissance = intent.getStringExtra("EXTRA_DATE_NAISSANCE")

        // Test Recupération de données
        Log.d("Recup", "Nom: $nom, Prénom: $prenom, Date: $dateNaissance")
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


fun searchElecteur(
    electeurs: List<Electeurs>,
    nom: String?,
    prenom: String?,
    dateNaissance: String?
): List<Electeurs> {
    if (nom.isNullOrBlank() || prenom.isNullOrBlank() || dateNaissance.isNullOrBlank()) {
        Log.d("Search", "Champs vides : nom=$nom, prenom=$prenom, date=$dateNaissance")
        return emptyList()
    }

    val result = electeurs.filter { e ->
        val matchNom = e.nomDeNaissance.equals(nom, ignoreCase = true)
        val matchPrenom = e.prenoms?.contains(prenom, ignoreCase = true) ?: false
        val matchDate = e.dateDeNaissance?.replace("\\", "/")
            ?.equals(dateNaissance.replace("\\", "/"), ignoreCase = true) ?: false

        Log.d(
            "Search",
            "Test: ${e.nomDeNaissance} ${e.prenoms} ${e.dateDeNaissance} → nom=$matchNom prenom=$matchPrenom date=$matchDate"
        )

        matchNom && matchPrenom && matchDate
    }

    Log.d("Search", "Résultats trouvés: ${result.size}")
    return result
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen() {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    // Récupération des infos passées depuis l'intent
    val nom = activity.intent.getStringExtra("EXTRA_NOM")
    val prenom = activity.intent.getStringExtra("EXTRA_PRENOM")
    val dateNaissance = activity.intent.getStringExtra("EXTRA_DATE_NAISSANCE")

    val electeurs by produceState<List<Electeurs>?>(initialValue = null, key1 = Unit) {
        value = withContext(Dispatchers.IO) { chargerElecteurs(context) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Résultats") },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF35A5C0),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        when {
            electeurs == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF35A5C0))
                }
            }

            else -> {
                val resultats = searchElecteur(electeurs!!, nom, prenom, dateNaissance)

                if (resultats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aucun électeur trouvé.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(resultats) { e ->
                            ElecteurCard(e)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ElecteurCard(e: Electeurs) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = e.nomDeNaissance,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text("Nom d'usage : ${e.nomUsage ?: "-"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Prénoms : ${e.prenoms}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Date de naissance : ${e.dateDeNaissance ?: "-"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Code du bureau : ${e.codeBureauVote}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = e.libelleBureauVote,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
