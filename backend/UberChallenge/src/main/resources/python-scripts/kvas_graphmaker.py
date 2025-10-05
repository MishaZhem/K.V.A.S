import pandas as pd
import numpy as np
import catboost as catboost
from catboost import CatBoostRegressor
import sys
import os
from io import StringIO

# --- SCRIPT START ---
# This script now expects input in a specific format from stdin:
# A line containing '---DATA_GRAPH_CSV_START---'
# The content of data_graph.csv
# A line containing '---DATA_GRAPH_CSV_END---'
# The content of the driver CSV data
# --- SCRIPT END ---

# Read all stdin
full_input = sys.stdin.read()

# Split the input into the two CSV parts
try:
    data_graph_part = full_input.split('---DATA_GRAPH_CSV_END---')[0]
    driver_part = full_input.split('---DATA_GRAPH_CSV_END---')[1]

    data_graph_csv = data_graph_part.replace('---DATA_GRAPH_CSV_START---', '').strip()
    driver_csv = driver_part.strip()

    df_historic = pd.read_csv(StringIO(data_graph_csv))
    driver = pd.read_csv(StringIO(driver_csv))
except Exception as e:
    print(f"Error parsing input from stdin: {e}", file=sys.stderr)
    sys.exit(1)


model_dir = os.path.join(os.path.dirname(__file__), "models")

modelDist = CatBoostRegressor()
modelDist.load_model(os.path.join(model_dir, "catboost_modelDist.cbm"))

modelEarn = CatBoostRegressor()
modelEarn.load_model(os.path.join(model_dir, "catboost_modelEarn.cbm"))

modelLocLocCity = CatBoostRegressor()
modelLocLocCity.load_model(os.path.join(model_dir, "catboost_modelLocLocCity.cbm"))

modelTime = CatBoostRegressor()
modelTime.load_model(os.path.join(model_dir, "catboost_modelTime.cbm"))

driver["product"]=driver["productType"]
driver["vehicle_type"]=driver["vehicleType"]
driver["is_ev"]=driver["isEv"]
driver["earner_type"]=driver["earnerType"]
driver["fuel_type"]=driver["fuelType"]
driver["weather"]=driver["weatherType"]
driver["experience_months"]=driver["experienceMonths"]

driver = driver.head(1)

df_historic = df_historic[df_historic["earner_type"] == driver["earnerType"].iloc[0]]

df_historic["vehicle_type"] = driver["vehicle_type"].iloc[0]
df_historic["is_ev"] = driver["is_ev"].iloc[0]
df_historic["fuel_type"] = driver["fuel_type"].iloc[0]
df_historic["experience_months"] = driver["experience_months"].iloc[0]
df_historic["rating"] = driver["rating"].iloc[0]
df_historic["weather"] = driver["weather"].iloc[0]

df_historic = df_historic[['city_id', 'product', 'vehicle_type', 'is_ev', 'start_time',
       'pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon', 'surge_multiplier',
       'payment_type', 'weather', 'earner_type', 'fuel_type',
       'experience_months', 'rating']]

df_aux = pd.DataFrame()

df_aux["timeBetween"] = modelTime.predict(df_historic)

df_save = df_historic[['pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon']]

df_historic["pickup_lat"] = driver["driverLat"].iloc[0]
df_historic["pickup_lon"] = driver["driverLon"].iloc[0]
df_historic["drop_lat"] = df_save["pickup_lat"]
df_historic["drop_lon"] = df_save["pickup_lon"]

df_aux["timeTo"] = modelTime.predict(df_historic)

df_historic["pickup_lat"] = df_save["drop_lat"]
df_historic["pickup_lon"] = df_save["drop_lon"]
df_historic["drop_lat"] = driver["driverLat"].iloc[0]
df_historic["drop_lon"] = driver["driverLon"].iloc[0]

df_aux["timeBack"] = modelTime.predict(df_historic)
df_aux["earnGet"] = modelEarn.predict(df_historic)

df_aux["time"] = df_historic["start_time"]

df_aux["calc"] = df_aux["earnGet"]/((df_aux["timeTo"]+df_aux["timeBetween"]+df_aux["timeBack"])/60)

avgTime = []

for i in range(0, 24):
  df_time = df_aux[df_aux["time"]==i]

  df_time = df_time.sort_values(by='calc', ascending=False)

  #print(df_time["calc"].iloc[0:11])

  avgTime.append(sum(df_time["calc"].iloc[0:11])/10)

auxTime = avgTime.copy()

auxTime.sort()

treshold = auxTime[8]

ans = np.array(avgTime)

ans = ans >= treshold

# Output as CSV: comma-separated 0/1 for booleans
print(','.join('1' if x else '0' for x in ans))
