import pandas as pd
import sys
import os
from catboost import CatBoostRegressor

def main():
    if len(sys.argv) != 3:
        print("Usage: python kvas_usemodel.py <input_csv> <output_csv>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    model_path = os.path.join(script_dir, "catboost_model_dist.cbm")

    df_test = pd.read_csv(input_file)

    modelDist = CatBoostRegressor()
    modelDist.load_model(model_path)

    ypred = modelDist.predict(df_test)

    ypred = pd.DataFrame({"distance": ypred})
    ypred.to_csv(output_file, index=False)

    print(f"Prediction completed. Results saved to {output_file}")

if __name__ == "__main__":
    main()
