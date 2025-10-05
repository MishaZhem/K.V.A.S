import pandas as pd
import numpy as np
import catboost as catboost

from catboost import CatBoostRegressor

driver = pd.read_csv("/content/driver.csv")

df_historic = pd.read_csv("/content/data_graph.csv")

modelDist = CatBoostRegressor()
modelDist.load_model("catboost_modelDist.cbm")

modelEarn = CatBoostRegressor()
modelEarn.load_model("catboost_modelEarn.cbm")

modelLocLocCity = CatBoostRegressor()
modelLocLocCity.load_model("catboost_modelLocLocCity.cbm")

modelTime = CatBoostRegressor()
modelTime.load_model("catboost_modelTime.cbm")

driver = driver.head(1)

driver["product"]=driver["productType"]
driver["vehicle_type"]=driver["vehicleType"]
driver["is_ev"]=driver["isEv"]
driver["earner_type"]=driver["earnerType"]
driver["fuel_type"]=driver["fuelType"]
driver["weather"]=driver["weatherType"]
driver["experience_months"]=driver["experienceMonths"]

df_historic.head()

df_historic = df_historic[df_historic["earner_type"] == driver["earnerType"].iloc[0]]

driver = driver.head(1)

df_historic["vehicle_type"] = driver["vehicle_type"]
df_historic["is_ev"] = driver["is_ev"]
df_historic["fuel_type"] = driver["fuel_type"]
df_historic["experience_months"] = driver["experience_months"]
df_historic["rating"] = driver["rating"]
df_historic["weather"] = driver["weather"]

df_historic = df_historic[['city_id', 'product', 'vehicle_type', 'is_ev', 'start_time',
       'pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon', 'surge_multiplier',
       'payment_type', 'weather', 'earner_type', 'fuel_type',
       'experience_months', 'rating']]

df_aux = pd.DataFrame()

df_aux["timeBetween"] = modelTime.predict(df_historic)

df_save = df_historic[['pickup_lat', 'pickup_lon', 'drop_lat', 'drop_lon']]

df_historic["pickup_lat"] = driver["driverLat"]
df_historic["pickup_lon"] = driver["driverLon"]
df_historic["drop_lat"] = df_save["pickup_lat"]
df_historic["drop_lon"] = df_save["pickup_lon"]

df_aux["timeTo"] = modelTime.predict(df_historic)

df_historic["pickup_lat"] = df_save["drop_lat"]
df_historic["pickup_lon"] = df_save["drop_lon"]
df_historic["drop_lat"] = driver["driverLat"]
df_historic["drop_lon"] = driver["driverLon"]

df_aux["timeBack"] = modelTime.predict(df_historic)
df_aux["earnGet"] = modelEarn.predict(df_historic)

df_aux["time"] = df_historic["start_time"]

df_aux["calc"] = df_aux["earnGet"]/((df_aux["timeTo"]+df_aux["timeBetween"]+df_aux["timeBack"])/60)

df_aux.head()

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

print(ans, end="")