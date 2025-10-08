from flask import Flask, request, jsonify
import kvas_evaluateorders as ml_job
import kvas_graphmaker as ml_graph

app = Flask(__name__)

@app.route('/api/ml/jobs', methods=['POST'])
def process_jobs_data():
    try:
        input_json_str = request.data.decode('utf-8')
        if not input_json_str:
            return jsonify({"error": "Request body is empty"}), 400

        jobs_list = ml_job.evaluate_orders(input_json_str)

        response_data = {"jobs": jobs_list}
        return jsonify(response_data)

    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route('/api/ml/graph', methods=['POST'])
def process_graph_data():
    try:
        input_json_str = request.data.decode('utf-8')
        if not input_json_str:
            return jsonify({"error": "Request body is empty"}), 400

        graph_data = ml_graph.evaluate_graph(input_json_str)

        response_data = {"graphData": graph_data}
        return jsonify(response_data)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run()