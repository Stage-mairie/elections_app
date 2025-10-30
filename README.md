# 📱 Bureaux de vote — Saint-André

## Présentation
Application Android (Kotlin / Android Studio) pour tablette.
Permet aux habitants de Saint-André de retrouver leur bureau de vote rapidement.

L’utilisateur remplit un formulaire simple et voit directement son bureau.

## Objectifs
- 🔹 Retrouver son bureau de vote facilement
- 🔹 Interface rapide et intuitive
- 🔹 Fonctionnement hors ligne sur tablette

## Fonctionnement
1. L’utilisateur remplit le formulaire avec ses informations.
2. L’application cherche dans les données locales (JSON généré depuis Excel).
3. Le bureau de vote correspondant s’affiche immédiatement.

## Fonctionnalités
- 📝 Formulaire simple pour l’électeur
- 🔍 Recherche automatique du bureau
- ✅ Affichage clair du bureau
- 📴 Utilisation hors ligne, données stockées localement

## Organisation du projet
- 📦 Application Android : Kotlin / Android Studio
- 📄 Fichier JSON : tous les bureaux de vote, généré depuis Excel
- ⚙️ Script Excel → JSON : conversion automatique des données

## Mise à jour
1. Mettre à jour le fichier Excel maître
2. Générer un nouveau JSON avec le script
3. Remplacer le JSON dans l’application

## Contexte
Projet de la ville de Saint-André pour faciliter l’accès aux bureaux de vote sur tablette pour les citoyens.
