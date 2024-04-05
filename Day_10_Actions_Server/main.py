from flask import Flask, Response, request, jsonify

app = Flask(__name__)

order_counter = 0

orders = []

@app.route('/submit_order', methods=['POST'])
def submit_order():
    global order_counter

    data = request.get_json()

    if 'advertisingID' in data and 'order_items' in data:
        advertising_id = data['advertisingID']
        order_items = data['order_items']

        order_counter += 1
        order_id = order_counter
        
        orders.append({'order_id': order_id, 'advertisingID': advertising_id, 'order_items': order_items, 'status': 'Đang xử lý'})
        
        response = {'message': f'Đơn hàng {order_id} của bạn đang xử lý.'}
        
        return jsonify(response), 200
    else:
        return jsonify({'error': 'Thiếu thông tin cần thiết'}), 400

if __name__ == '__main__':
    app.run(debug=True)