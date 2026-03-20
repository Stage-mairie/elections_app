package com.example.bureaudevotemsa.ui.theme.components

import android.R.attr.title
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import android.app.DatePickerDialog
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bureaudevotemsa.ElecteurRepository
import com.example.bureaudevotemsa.ui.theme.BureauDeVoteMSATheme
import java.util.*

@Composable
fun UserForm(
    onSearchClicked: (nom: String, prenom: String, dateNaissance: String) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }

    var showHint by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val estPret by ElecteurRepository.estPret.collectAsState()

    // Bouton actif seulement si l'index est prêt ET au moins un champ rempli
    val isSearchEnabled = estPret && (nom.isNotBlank() || prenom.isNotBlank() || dateNaissance.isNotBlank())

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Titre de l'application
            Text(
                text = "Bureau de Vote MSA",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bandeau de chargement — visible uniquement pendant l'initialisation
            if (!estPret) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(bottom = 12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chargement de la liste des électeurs...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            // Champ Nom
            OutlinedTextField(
                value = nom,
                onValueChange = { if (estPret) nom = it },
                label = { Text("Nom") },
                singleLine = true,
                enabled = estPret,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 4.dp)
            )
            // Champ Prénom
            OutlinedTextField(
                value = prenom,
                onValueChange = { if (estPret) prenom = it },
                label = { Text("Prénom") },
                singleLine = true,
                enabled = estPret,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 4.dp)
            )

            // Champ Date de naissance
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 4.dp)
            ) {
                // TextField read-only
                OutlinedTextField(
                    value = dateNaissance,
                    onValueChange = { },
                    label = { Text("Date de naissance") },
                    enabled = estPret,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("JJ/MM/AAAA") },
                    trailingIcon = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                            contentDescription = "Sélectionner une date"
                        )
                    }
                )

                // Overlay invisible pour gérer le clic
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            val c = Calendar.getInstance()
                            val annee = c.get(Calendar.YEAR)
                            val mois = c.get(Calendar.MONTH)
                            val jour = c.get(Calendar.DAY_OF_MONTH)

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dateNaissance =
                                        String.format("%02d/%02d/%d", day, month + 1, year)
                                },
                                annee,
                                mois,
                                jour
                            ).show()
                        }
                )
            }


            // Search button
            Button(
                onClick = {
                    onSearchClicked(nom, prenom, dateNaissance)
                },
                enabled = isSearchEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(top = 8.dp)
            ) {
                Text("Rechercher")
            }

            // Reset button
            Button(
                onClick = {
                    nom = ""
                    prenom = ""
                    dateNaissance = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(top = 8.dp)
                ) {

                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Réinitialiser",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Réinitialiser")
            }

        }

        IconButton(
            onClick = { showHint = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(36.dp)
        ) {
            Icon(Icons.Filled.Info, contentDescription = "Hint")
        }

        if (showHint) {
            AlertDialog(
                onDismissRequest = { showHint = false },
                title = { Text("Information - Date de naissance") },
                text = { Text(
                    "Si la date n'est pas précise, utilisez le 01/01/XXXX, où XXXX est l'année de naissance."
                )  },
                confirmButton = {
                    TextButton(onClick = { showHint = false }) {
                        Text("OK")
                    }
                }

            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun UserFormPreview() {
    BureauDeVoteMSATheme(dynamicColor = false) {UserForm(onSearchClicked = { _, _, _ -> }) }
}

