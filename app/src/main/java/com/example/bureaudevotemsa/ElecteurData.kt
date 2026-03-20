package com.example.bureaudevotemsa

import android.content.Context
import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.Normalizer

// ─────────────────────────────────────────────
// Extension de normalisation
// ─────────────────────────────────────────────

fun String.normaliser(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}"), "")
        .trim()
        .lowercase()
}

// ─────────────────────────────────────────────
// Modèle
// ─────────────────────────────────────────────

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

// ─────────────────────────────────────────────
// Chargement JSON
// ─────────────────────────────────────────────

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
        emptyList()
    }
}

// ─────────────────────────────────────────────
// Index de recherche
// ─────────────────────────────────────────────

class ElecteurSearchIndex(electeurs: List<Electeurs>) {

    private data class ElecteurNormalise(
        val original: Electeurs,
        val nomN: String,
        val nomUsageN: String?,
        val prenomN: String?
    )

    private val donnees: List<ElecteurNormalise> = electeurs.map { e ->
        ElecteurNormalise(
            original  = e,
            nomN      = e.nomDeNaissance.normaliser(),
            nomUsageN = e.nomUsage?.normaliser(),
            prenomN   = e.prenoms?.normaliser()
        )
    }

    private val parNom: Map<String, List<ElecteurNormalise>> = buildMap {
        donnees.forEach { e ->
            getOrPut(e.nomN) { mutableListOf() }.also { (it as MutableList).add(e) }
            if (e.nomUsageN != null && e.nomUsageN != e.nomN) {
                getOrPut(e.nomUsageN) { mutableListOf() }.also { (it as MutableList).add(e) }
            }
        }
    }

    private val parDate: Map<String, List<ElecteurNormalise>> = donnees
        .groupBy { it.original.dateDeNaissance?.replace("\\", "/")?.trim() ?: "" }
        .filterKeys { it.isNotBlank() }

    fun search(nom: String?, prenom: String?, dateNaissance: String?): List<Electeurs> {
        val nomN    = nom?.normaliser()?.takeIf { it.isNotBlank() }
        val prenomN = prenom?.normaliser()?.takeIf { it.isNotBlank() }
        val dateN   = dateNaissance?.replace("\\", "/")?.trim()?.takeIf { it.isNotBlank() }

        if (nomN == null && prenomN == null && dateN == null) return emptyList()

        val candidates = when {
            nomN != null  -> parNom[nomN].orEmpty()
            dateN != null -> parDate[dateN].orEmpty()
            else          -> emptyList()
        }

        return candidates
            .filter { e ->
                (nomN == null || e.nomN == nomN || e.nomUsageN == nomN)
                        &&
                        (prenomN == null || e.prenomN?.contains(prenomN) == true)
                        &&
                        (dateN == null || e.original.dateDeNaissance?.replace("\\", "/")?.trim() == dateN)
            }
            .map { it.original }
    }
}