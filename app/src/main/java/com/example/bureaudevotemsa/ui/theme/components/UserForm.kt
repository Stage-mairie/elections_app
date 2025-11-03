package com.example.bureaudevotemsa.ui.theme.components

import android.app.DatePickerDialog
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun UserForm() {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }

    val context = LocalContext.current

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
            // Champ Nom
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 4.dp)
            )
            // Champ Prénom
            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prénom") },
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
                                    dateNaissance = String.format("%02d/%02d/%d", day, month + 1, year)
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
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(top = 8.dp)
            ) {
                Text("Rechercher")
            }
        }
    }
}

