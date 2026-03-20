package com.example.bureaudevotemsa

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ElecteurRepository {

    private val _estPret = MutableStateFlow(false)
    val estPret: StateFlow<Boolean> = _estPret

    private var index: ElecteurSearchIndex? = null

    suspend fun initialiser(context: Context) {
        if (index != null) return
        val electeurs = chargerElecteurs(context)
        index = ElecteurSearchIndex(electeurs)
        _estPret.value = true
        Log.d("Repository", "Index construit : ${electeurs.size} électeurs")
    }

    fun search(nom: String?, prenom: String?, date: String?): List<Electeurs> {
        return index?.search(nom, prenom, date) ?: emptyList()
    }
}