import random
from flask import Flask, Response, request, jsonify
from flask_cors import CORS
app = Flask(__name__)
CORS(app)
counter = 0

food=[
    {
        "id": 'F001',
        "name": "Hamburger",
        "image":"image\hamburger.jpg"
    },
    {
        "id": 'F002',
        "name": "Pho bo",
        "image":"image\phobo.jpg"
    },
    {
        "id": 'F003',
        "name": "Rice chicken",
        "image":"image\comga.jpg"
    }
]

orders=[
    {
        "id": 'O001',
        "advertisingID": "fffff:0000:1234",
        "items": ['F002', 'F003'],
        "status": "Processing"
    },
    {
        "id": 'O002',
        "advertisingID": "fffff:0000:1834",
        "items": ['F001', 'F003'],
        "status": "AlmostDone"
    },
    {
        "id": 'O003',
        "advertisingID": "fffff:0000:1934",
        "items": ['F001','F002','F003'],
        "status": "Done"
    }
]

@app.route('/food',methods=['GET'])
def getFood():
    return jsonify(
            {
                'food': food,
                'result': 'success'
            }
    )


@app.route('/submit_order', methods=['POST'])
def submit_order():
    global counter

    data = request.get_json()

    if 'advertisingID' in data and 'items' in data:
        advertising_id = data['advertisingID']
        items = data['items']

        counter += 1
        id = counter
        
        orders.append({'id': id, 'advertisingID': advertising_id, 'items': items, 'status': 'Đang xử lý'})
        
        response = {'message': f'Đơn hàng {id} của bạn đang xử lý.'}
        
        return jsonify(response), 200
    else:
        return jsonify({'error': 'Thiếu thông tin cần thiết'}), 400
    
    
@app.route('/orders/get-all', methods=['GET'])
def getAllOrders():
    return jsonify(orders)

@app.route('/orders/change-status', methods=['GET'])
def changeOrderStatus():
    order_id = request.args.get('id')
    status = request.args.get('status')
    for order in orders:
        if order['id'] == order_id:
            order['status'] = status
    return jsonify(orders)
if __name__ == '__main__':
    app.run(host='192.168.40.143', port=8080)