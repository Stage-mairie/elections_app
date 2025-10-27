from openpyxl import Workbook
import pandas as pd

df = pd.read_excel("scripts/electeurs.xlsx", parse_dates=True)

colonnes_voulues = ["nom de naissance", "nom d'usage", "prénoms", 
                    "date de naissance", "code du bureau de vote", "libellé du bureau de vote"]
df = df[colonnes_voulues]

df["date de naissance"] = pd.to_datetime(df["date de naissance"], format="%d/%m/%Y", errors="coerce")
df["date de naissance"] = df["date de naissance"].dt.strftime("%d/%m/%Y")

df.to_json("electeurs.json", orient="records", force_ascii=False, indent=4)