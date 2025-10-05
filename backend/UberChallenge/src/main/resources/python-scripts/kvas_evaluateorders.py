# -*- coding: utf-8 -*-
"""KVAS_EvaluateOrders.ipynb - stdin/stdout version (NO FILES!)"""

import pandas as pd
import numpy as np
import sys
import os
from io import StringIO
from catboost import CatBoostRegressor

# --- SCRIPT START ---
# This script now expects input in a specific format from stdin:
# A line containing '---DATA_GRAPH_CSV_START---'
# The content of data_graph.csv
# A line containing '---DATA_GRAPH_CSV_END---'
# The content of the driver CSV data
# --- SCRIPT END ---


# Load models once at startup
model_dir = os.path.join(os.path.dirname(__file__), "models")
try:
    modelDist = CatBoostRegressor()
    modelDist.load_model(os.path.join(model_dir, "catboost_modelDist.cbm"))

    modelEarn = CatBoostRegressor()
    modelEarn.load_model(os.path.join(model_dir, "catboost_modelEarn.cbm"))

    modelLocLocCity = CatBoostRegressor()
    modelLocLocCity.load_model(os.path.join(model_dir, "catboost_modelLocLocCity.cbm"))

    modelTime = CatBoostRegressor()
    modelTime.load_model(os.path.join(model_dir, "catboost_modelTime.cbm"))
except Exception as e:
    print(f"ERROR_LOADING_MODELS: {e}", file=sys.stderr)
    sys.exit(1)

# Read CSV data from stdin
try:
    input_data = sys.stdin.read()
    df_data = pd.read_csv(StringIO(input_data))
except Exception as e:
    print(f"ERROR_READING_INPUT: {e}", file=sys.stderr)
    sys.exit(1)

# Process data
df_data["pickup_lat"] = df_data["startLat"]
df_data["pickup_lon"] = df_data["startLon"]

df_data["drop_lat"] = df_data["endLat"]
df_data["drop_lon"] = df_data["endLon"]

df_aux = df_data[["pickup_lat", "pickup_lon"]]

df_data["city_id"] = modelLocLocCity.predict(df_aux)
df_data["city_id"] = df_data["city_id"].astype(int)

df_data["product"] = df_data["productType"]
df_data["vehicle_type"] = df_data["vehicleType"]
df_data["is_ev"] = df_data["isEv"]
df_data["start_time"] = df_data["startingTime"]
df_data["surge_multiplier"] = 1
df_data["payment_type"] = df_data["paymentType"]
df_data["weather"] = df_data["weatherType"]
df_data["earner_type"] = df_data["earnerType"]
df_data["fuel_type"] = df_data["fuelType"]
df_data["experience_months"] = df_data["experienceMonths"]

df_new = df_data[['city_id', 'product', 'vehicle_type', 'is_ev', 'start_time',
                   'pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon', 'surge_multiplier',
                   'payment_type', 'weather', 'earner_type', 'fuel_type',
                   'experience_months', 'rating']]

df_ans = pd.DataFrame()

df_ans["dist_Order"] = modelDist.predict(df_new)
df_ans["time_Order"] = modelTime.predict(df_new)
df_ans["earn_Order"] = modelEarn.predict(df_new)

# Swap coordinates for pickup distance calculation
df_new["drop_lat"] = df_new["pickup_lat"]
df_new["drop_lon"] = df_new["pickup_lon"]

df_new["pickup_lat"] = df_data["driverLat"]
df_new["pickup_lon"] = df_data["driverLon"]

df_ans["dist_Pick"] = modelDist.predict(df_new)
df_ans["time_Pick"] = modelTime.predict(df_new)

df_ans["moneyPerHour"] = df_ans["earn_Order"] / ((df_ans["time_Order"] + df_ans["time_Pick"]) / 60)

# Write output to stdout
try:
    output = df_ans.to_csv(index=False)
    print(output, end='')  # No extra newline
except Exception as e:
    print(f"ERROR_WRITING_OUTPUT: {e}", file=sys.stderr)
    sys.exit(1)