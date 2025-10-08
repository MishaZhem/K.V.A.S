import json

import pandas as pd
import numpy as np
import os

from catboost import CatBoostRegressor

def evaluate_graph(input_json):
    model_dir = os.path.join(os.path.dirname(__file__), "model")
    try:
        df_historic = pd.read_csv(os.path.join(model_dir, "data_graph.csv"))

        modelDist = CatBoostRegressor()
        modelDist.load_model(os.path.join(model_dir, "catboost_modelDist.cbm"))

        modelEarn = CatBoostRegressor()
        modelEarn.load_model(os.path.join(model_dir, "catboost_modelEarn.cbm"))

        modelLocLocCity = CatBoostRegressor()
        modelLocLocCity.load_model(os.path.join(model_dir, "catboost_modelLocLocCity.cbm"))

        modelTime = CatBoostRegressor()
        modelTime.load_model(os.path.join(model_dir, "catboost_modelTime.cbm"))
    except Exception as e:
        print(f"Error loading models: {e}")
        raise

    driver_data = json.loads(input_json)
    driver = pd.DataFrame([driver_data])

    driver["product"] = driver["productType"]
    driver["vehicle_type"] = driver["vehicleType"]
    driver["is_ev"] = driver["isEv"]
    driver["earner_type"] = driver["earnerType"]
    driver["fuel_type"] = driver["fuelType"]
    driver["weather"] = driver["weatherType"]
    driver["experience_months"] = driver["experienceMonths"]

    df_historic = df_historic[df_historic["earner_type"] == driver["earner_type"].iloc[0]]

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

    df_save = df_historic[['pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon']].copy()

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
      if not df_time.empty:
          df_time = df_time.sort_values(by='calc', ascending=False)
          count = min(10, len(df_time))
          if count > 0:
              avgTime.append(df_time["calc"].iloc[0:count].sum() / count)
          else:
              avgTime.append(0)
      else:
          avgTime.append(0)

    if not avgTime:
        return {"error": "No data to process"}

    auxTime = sorted(avgTime)
    treshold = auxTime[8] if len(auxTime) > 8 else 0

    ans = (np.array(avgTime) >= treshold).tolist()
    return ans