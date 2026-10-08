package com.example.bureaudevotemsa

/** Index de recherche construit une seule fois, hors du thread d'affichage. */
class FastElecteurSearch(electeurs: List<Electeurs>) {
    private data class Entry(
        val electeur: Electeurs,
        val nom: String,
        val nomUsage: String,
        val prenoms: String,
        val date: String,
        val annee: String
    )

    data class Result(val electeurs: List<Electeurs>, val hasMore: Boolean)

    private val entries = electeurs.map { e ->
        val date = e.dateDeNaissance.orEmpty().replace('\\', '/').trim()
        Entry(
            electeur = e,
            nom = normaliserRecherche(e.nomDeNaissance),
            nomUsage = normaliserRecherche(e.nomUsage.orEmpty()),
            prenoms = normaliserRecherche(e.prenoms.orEmpty()),
            date = date,
            annee = date.takeLast(4)
        )
    }

    /** Au plus [limit] cartes : on ne fabrique jamais une liste géante à chaque frappe. */
    fun find(nom: String, prenom: String, naissance: String, limit: Int = 50): Result {
        val n = normaliserRecherche(nom)
        val p = normaliserRecherche(prenom)
        val d = naissance.trim()
        if (n.isEmpty() && p.isEmpty() && d.isEmpty()) return Result(emptyList(), false)

        val matches = ArrayList<Electeurs>(limit)
        for (entry in entries) {
            if (n.isNotEmpty() && !entry.nom.startsWith(n) && !entry.nomUsage.startsWith(n)) continue
            if (p.isNotEmpty() && !entry.prenoms.contains(p)) continue
            if (d.isNotEmpty() && entry.date != d && !(d.length == 4 && entry.annee == d)) continue
            if (matches.size == limit) return Result(matches, true)
            matches.add(entry.electeur)
        }
        return Result(matches, false)
    }
}
