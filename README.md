# Bureau de vote MSA / MSA Polling Station

Application Android permettant de retrouver le bureau de vote d'un electeur a partir de son nom, de son prenom et/ou de sa date de naissance.

Android application that helps a voter find their polling station using their surname, first name, and/or date of birth.

## Pour les citoyens / For voters

### Francais

1. Ouvrez l'application **Bureau de Vote MSA**.
2. Saisissez au moins un critere : nom, prenom ou date de naissance.
3. La date est facultative : saisissez simplement une annee `AAAA` ou une date complete `JJ/MM/AAAA` au clavier.
4. Les résultats s’actualisent automatiquement sous les champs au fil de la saisie.
5. Vérifiez l’identité, puis consultez le numéro et le libellé du bureau de vote.
6. Si plus de 50 personnes correspondent, affinez avec le prénom ou la date de naissance. La liste est limitée aux 50 premiers résultats pour garder une saisie fluide.

Le nom correspond au **debut** du nom de naissance ou du nom d'usage (ex. `BAN` retrouve `BANA`). Le prenom accepte une correspondance partielle. Les recherches ignorent les majuscules, les accents et les differences de tirets/apostrophes. La saisie d'une **annee seule** filtre sur l'annee de naissance ; les criteres renseignes se combinent (ET).

Le bouton **Effacer la recherche** vide le formulaire. Si aucun electeur ne correspond, l'application affiche **Aucun electeur trouve**.

### English

1. Open the **Bureau de Vote MSA** app.
2. Enter at least one criterion: surname, first name, or date of birth.
3. Birth date is optional: type a year `YYYY` or a full date `DD/MM/YYYY` directly.
4. Matching voters appear automatically under the fields as you type.
5. Verify the voter’s identity and read the polling station number and name.
6. When there are more than 50 matches, narrow the search with a first name or birth date. Only the first 50 matches are shown to keep typing responsive.

Surname searches match the **beginning** of either birth or usual surname (e.g. `BAN` matches `BANA`). Given names support partial matches. Names are case- and accent-insensitive and normalize hyphens/apostrophes. A year-only input filters by birth year, and provided criteria are combined (AND).

Use **Effacer la recherche** (Clear search) to reset all fields. If no voter matches, the app displays **Aucun electeur trouve** (No voter found).

## Pour les developpeurs / For developers

### Francais

#### Technologies

- Kotlin 1.9
- Android Gradle Plugin 8.5
- Jetpack Compose et Material 3
- Android SDK compile/target 34
- Android minimum SDK 29
- Donnees locales au format JSON, lues depuis `app/src/main/assets/electeurs.json`

#### Prerequis

- Android Studio recent avec le plugin Kotlin et le SDK Android 34
- JDK 17 ou une version compatible avec la version d'Android Gradle Plugin utilisee
- Un emulateur Android ou un appareil avec Android 10 (API 29) ou plus recent

#### Compiler et tester

Depuis la racine du projet, dans un terminal compatible Unix (Linux, macOS, Git Bash ou WSL) :

```sh
./gradlew assembleDebug
./gradlew test
```

Pour installer la version debug sur un appareil connecte :

```sh
./gradlew installDebug
```

Le depot contient actuellement le wrapper Unix `gradlew`, mais pas le wrapper Windows `gradlew.bat`. Sous Windows, utilisez Git Bash/WSL ou lancez la configuration `app` directement depuis Android Studio.

Le projet peut aussi etre ouvert directement dans Android Studio, puis execute avec la configuration `app`.

#### Mettre a jour les electeurs

1. Placez le fichier source `electeurs.xlsx` dans `scripts/`.
2. Installez les dependances Python :

```powershell
python -m pip install openpyxl pandas
```

3. Depuis le dossier `scripts/`, executez :

```powershell
python create_json.py
```

Le script cree `scripts/electeurs.json`. Copiez ensuite ce fichier vers `app/src/main/assets/electeurs.json` avant de compiler l'application.

Le fichier Excel doit contenir les colonnes suivantes : `nom de naissance`, `nom d'usage`, `prénoms`, `date de naissance`, `code du bureau de vote` et `libellé du bureau de vote`. Les dates invalides sont ignorees. Une date `00/00/AAAA` est normalisee en `01/01/AAAA`.

#### Organisation du code

- `MainActivity.kt` affiche le formulaire principal.
- `ui/theme/Color.kt` et `ui/theme/Theme.kt` definissent les couleurs et le theme visuel.
- `ui/theme/components/UserForm.kt` contient la recherche instantanée et les résultats intégrés, avec une disposition adaptée aux tablettes.
- `ResultsActivity.kt` fournit le modèle électeur, le chargement JSON, la recherche classique et les cartes de résultat.
- `FastElecteurSearch.kt` prépare un index de noms normalisés en arrière-plan pour accélérer la recherche instantanée.
- `app/src/main/assets/electeurs.json` contient la liste embarquee dans l'application.
- `scripts/create_json.py` transforme le fichier Excel en JSON compatible avec l'application.

### English

#### Technologies

- Kotlin 1.9
- Android Gradle Plugin 8.5
- Jetpack Compose and Material 3
- Android SDK compile/target 34
- Android minimum SDK 29
- Local JSON data loaded from `app/src/main/assets/electeurs.json`

#### Requirements

- A recent Android Studio installation with the Kotlin plugin and Android SDK 34
- JDK 17 or a version compatible with the Android Gradle Plugin used by the project
- An Android emulator or device running Android 10 (API 29) or newer

#### Build and test

From the project root, in a Unix-compatible terminal (Linux, macOS, Git Bash, or WSL):

```sh
./gradlew assembleDebug
./gradlew test
```

To install the debug build on a connected device:

```sh
./gradlew installDebug
```

The repository currently includes the Unix wrapper `gradlew`, but not the Windows wrapper `gradlew.bat`. On Windows, use Git Bash/WSL or run the `app` configuration directly from Android Studio.

You can also open the project in Android Studio and run the `app` configuration.

#### Updating voter data

1. Put the source file `electeurs.xlsx` in `scripts/`.
2. Install the Python dependencies:

```powershell
python -m pip install openpyxl pandas
```

3. From the `scripts/` directory, run:

```powershell
python create_json.py
```

The script creates `scripts/electeurs.json`. Copy that file to `app/src/main/assets/electeurs.json` before building the app.

The Excel file must contain these columns: `nom de naissance`, `nom d'usage`, `prénoms`, `date de naissance`, `code du bureau de vote`, and `libellé du bureau de vote`. Invalid dates are ignored. A date such as `00/00/YYYY` is normalized to `01/01/YYYY`.

#### Code structure

- `MainActivity.kt` displays the main search screen.
- `ui/theme/components/UserForm.kt` displays instant search results and adjusts its layout for tablets.
- `ResultsActivity.kt` provides the voter model, JSON loading, filtering and result cards.
- `app/src/main/assets/electeurs.json` contains the dataset bundled with the app.
- `scripts/create_json.py` converts the Excel source file into the JSON format expected by the app.

## Donnees et limites / Data and limitations

### Francais

Les donnees sont embarquees localement dans l'application et la recherche est effectuee sur l'appareil. Il n'y a actuellement ni synchronisation avec un serveur ni mise a jour automatique des listes : une nouvelle version de l'application doit etre distribuee pour fournir un nouveau fichier JSON.

### English

The data is bundled locally in the application and searches run on the device. There is currently no server synchronization or automatic list update: distributing a new app build is required to ship a new JSON file.

## Bonnes pratiques / Good practices

- Le nom peut être saisi partiellement : `BAN` retrouve par exemple `BANA`.
- La date est facultative : saisissez une année `AAAA` ou une date `JJ/MM/AAAA`.
- Les données de naissance normalisées en `01/01/AAAA` ne garantissent pas que le jour et le mois soient exacts : privilégiez l'année si nécessaire.
- Le code du bureau de vote est présenté en évidence sur la fiche de résultat.
- Les fichiers Excel et JSON contenant des informations personnelles doivent rester confidentiels et ne doivent pas être publiés dans un dépôt public.

Surname prefixes and partial first names are supported. Birth year or full date is optional. A normalized `01/01/YYYY` date does not guarantee an actual day and month. The polling-station code is displayed prominently. Keep voter datasets private.
