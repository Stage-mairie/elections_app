package com.example.bureaudevotemsa

import android.content.Context
import android.util.Log

object ElecteurRepository {

    private var index: ElecteurSearchIndex? = null

    suspend fun initialiser(context: Context) {
        if (index != null) return
        val electeurs = chargerElecteurs(context)
        index = ElecteurSearchIndex(electeurs)
        Log.d("Repository", "Index construit : ${electeurs.size} électeurs")
    }

    fun search(nom: String?, prenom: String?, date: String?): List<Electeurs> {
        return index?.search(nom, prenom, date) ?: emptyList()
    }
}