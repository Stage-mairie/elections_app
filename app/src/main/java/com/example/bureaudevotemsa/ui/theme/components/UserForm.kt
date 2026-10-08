package com.example.bureaudevotemsa.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bureaudevotemsa.ElecteurCard
import com.example.bureaudevotemsa.Electeurs
import com.example.bureaudevotemsa.chargerElecteurs
import com.example.bureaudevotemsa.FastElecteurSearch
import com.example.bureaudevotemsa.ui.theme.Muted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Recherche locale : les résultats restent sur cet écran, sans navigation. */
@Suppress("UNUSED_PARAMETER")
@Composable
fun UserForm(onSearchClicked: (nom: String, prenom: String, dateNaissance: String) -> Unit) {
    val context = LocalContext.current
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var naissance by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Electeurs>>(emptyList()) }
    var hasMore by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }

    // Le chargement JSON et la normalisation de tous les noms sont faits une seule fois.
    val index by produceState<FastElecteurSearch?>(initialValue = null, context) {
        val data = withContext(Dispatchers.IO) { chargerElecteurs(context.applicationContext) }
        value = withContext(Dispatchers.Default) { FastElecteurSearch(data) }
    }
    val dateValid = naissance.isBlank() || naissance.matches(Regex("\\d{4}")) ||
        naissance.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    val hasQuery = nom.isNotBlank() || prenom.isNotBlank() || naissance.isNotBlank()
    val ready = hasQuery && dateValid && index != null

    // Annule la recherche précédente si un nouveau caractère est saisi.
    LaunchedEffect(nom, prenom, naissance, index) {
        if (!ready) {
            results = emptyList()
            hasMore = false
            searching = false
        } else {
            searching = true
            delay(120)
            val found = withContext(Dispatchers.Default) {
                index!!.find(nom, prenom, naissance)
            }
            results = found.electeurs
            hasMore = found.hasMore
            searching = false
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val tablet = maxWidth >= 700.dp
        val maxContentWidth = if (tablet) 920.dp else 600.dp
        Column(
            modifier = Modifier.fillMaxSize().widthIn(max = maxContentWidth)
                .align(Alignment.TopCenter).padding(horizontal = if (tablet) 24.dp else 16.dp, vertical = 12.dp)
        ) {
            Text("Trouver un bureau de vote", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold)
            Text("Les résultats s'affichent pendant la saisie.", color = Muted,
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(14.dp)) {
                    @Composable
                    fun NameField(value: String, change: (String) -> Unit, title: String, hint: String) {
                        OutlinedTextField(value = value, onValueChange = change, label = { Text(title) },
                            placeholder = { Text(hint) }, singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next), modifier = Modifier.fillMaxWidth())
                    }
                    if (tablet) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) { NameField(nom, { nom = it }, "Nom", "Ex. BAN") }
                            Box(Modifier.weight(1f)) { NameField(prenom, { prenom = it }, "Prénom", "Ex. Marie") }
                        }
                    } else {
                        NameField(nom, { nom = it }, "Nom", "Ex. BAN")
                        Spacer(Modifier.height(8.dp))
                        NameField(prenom, { prenom = it }, "Prénom", "Ex. Marie")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = naissance,
                        onValueChange = { naissance = it.filter { c -> c.isDigit() || c == '/' }.take(10) },
                        label = { Text("Naissance (facultatif)") },
                        placeholder = { Text("1985 ou 14/07/1985") },
                        singleLine = true, isError = !dateValid,
                        supportingText = { Text(if (dateValid) "Année ou date complète" else "Format : AAAA ou JJ/MM/AAAA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = { nom = ""; prenom = ""; naissance = "" },
                        modifier = Modifier.align(Alignment.End).heightIn(min = 48.dp),
                        enabled = hasQuery) { Text("Effacer la recherche") }
                }
            }
            Spacer(Modifier.height(14.dp))

            when {
                index == null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Préparation de la recherche…")
                    }
                }
                !hasQuery -> Text("Saisissez un nom, un prénom ou une année pour commencer.",
                    color = Muted, style = MaterialTheme.typography.bodyLarge)
                !dateValid -> Text("Corrigez la date pour afficher les résultats.",
                    color = MaterialTheme.colorScheme.error)
                searching -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Recherche en cours…")
                    }
                }
                else -> {
                    Text(if (hasMore) "50+ résultats — affinez votre recherche" else "${results.size} résultat${if (results.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Vérifiez l'identité avant de communiquer le bureau de vote.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    if (results.isEmpty()) {
                        Text("Aucun électeur trouvé. Essayez moins de lettres ou vérifiez la saisie.",
                            style = MaterialTheme.typography.bodyLarge)
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(results) { electeur -> ElecteurCard(electeur) }
                            if (hasMore) {
                                item {
                                    Text("50 premiers résultats affichés : précisez le nom ou la date pour affiner.",
                                        color = Muted, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
