package com.example.bureaudevotemsa.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bureaudevotemsa.ui.theme.BureauDeVoteMSATheme
import com.example.bureaudevotemsa.ui.theme.Muted

@Composable
fun UserForm(onSearchClicked: (nom: String, prenom: String, dateNaissance: String) -> Unit) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var naissance by remember { mutableStateOf("") }
    val validDate = naissance.isBlank() || naissance.matches(Regex("\\d{4}")) ||
        naissance.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
    val canSearch = validDate && (nom.isNotBlank() || prenom.isNotBlank() || naissance.isNotBlank())
    val submit = { if (canSearch) onSearchClicked(nom.trim(), prenom.trim(), naissance.trim()) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                Text("MSA  •  SERVICE ÉLECTORAL", modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(19.dp))
            Text("Trouver un bureau de vote", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Recherchez un électeur en quelques secondes.", color = Muted,
                style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(26.dp))

            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("RECHERCHE D'UN ÉLECTEUR", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold, color = Muted)
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = nom, onValueChange = { nom = it }, label = { Text("Nom") },
                        placeholder = { Text("Ex. BAN pour BANA") }, singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = prenom, onValueChange = { prenom = it }, label = { Text("Prénom") },
                        placeholder = { Text("Prénom complet ou partiel") }, singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = naissance,
                        onValueChange = { naissance = it.filter { c -> c.isDigit() || c == '/' }.take(10) },
                        label = { Text("Date de naissance (facultative)") },
                        placeholder = { Text("1985 ou 14/07/1985") },
                        singleLine = true, isError = !validDate,
                        supportingText = { Text(if (validDate) "Une année suffit pour filtrer les résultats" else "Saisir AAAA ou JJ/MM/AAAA") },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { submit() }, enabled = canSearch,
                        shape = RoundedCornerShape(13.dp), contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                        Text("Rechercher un électeur", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = { nom = ""; prenom = ""; naissance = "" },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text("Effacer les champs")
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text("Astuce : commencez par quelques lettres du nom. Les accents et les majuscules ne sont pas nécessaires.",
                    modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserFormPreview() { BureauDeVoteMSATheme { UserForm { _, _, _ -> } } }
