from openpyxl import Workbook
import pandas as pd
import re

df = pd.read_excel("electeurs.xlsx", parse_dates=False)

colonnes_voulues = [
    "nom de naissance", "nom d'usage", "prénoms",
    "date de naissance", "code du bureau de vote", "libellé du bureau de vote"
]
df = df[colonnes_voulues]

def corriger_date(date_str):
    if pd.isna(date_str):
        return None
    match = re.match(r"^0{2}/0{2}/(\d{4})$", str(date_str).strip())
    if match:
        return f"01/01/{match.group(1)}"
    try:
        return pd.to_datetime(date_str, format="%d/%m/%Y", errors="raise").strftime("%d/%m/%Y")
    except Exception:
        return None

df["date de naissance"] = df["date de naissance"].apply(corriger_date)

df.to_json("electeurs.json", orient="records", force_ascii=False, indent=4)