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
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.produceState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight


class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val prenoms: String,
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

        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        val list = json.decodeFromString<List<Electeurs>>(jsonString)
        list
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen() {
    val context = LocalContext.current
    val activity = context as ComponentActivity

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
        if (electeurs == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF35A5C0))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(electeurs!!) { e ->
                    ElecteurCard(e)
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
            Text("Date de naissance : ${e.dateDeNaissance ?: "01/01/1900"}",
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
