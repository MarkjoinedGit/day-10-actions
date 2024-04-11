import random
from flask import Flask, Response, request, jsonify, send_from_directory
from flask_sock import Sock
from flask_cors import CORS
import json

app = Flask(__name__)
CORS(app)
sock = Sock(app)

counter = 0

orders = []
wses = []
ws_clients = {}

food=[
    {
        "id": 1,
        "name": "Hamburger",
        "image":"image/hamburger.png"
    },
    {
        "id": 2,
        "name": "Pho bo",
        "image":"image/phobo.jpg"
    },
    {
        "id": 3,
        "name": "Rice chicken",
        "image":"image/comga.jpg"
    }
]

orders=[
    {
        "id": 1,
        "advertisingID": "fffff:0000:1234",
        "items": ['Pho bo', 'Rice chicken'],
        "status": "Processing"
    },
    {
        "id": 2,
        "advertisingID": "fffff:0000:1834",
        "items": ['Hamburger', 'Rice chicken'],
        "status": "AlmostDone"
    },
    {
        "id": 3,
        "advertisingID": "fffff:0000:1934",
        "items": ['Hamburger','Pho bo','Rice chicken'],
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

@sock.route('/getOrder')
def getOrder(ws):
    wses.append(ws)
    adsId = None
    while True:
        data = ws.receive(30)
        if data:
            #ws.send(data)
            try:
                data_dict = json.loads(data)
                adsId = data_dict.get('adsId')
                if adsId:
                    ws_clients[adsId] = ws
            except json.JSONDecodeError:
                pass
        else:
            break

@app.route('/image/<path:path>')
def send_asset(path):
    return send_from_directory('image', path)


@app.route('/submit_order', methods=['POST'])
def submit_order():

    global counter
    data = request.get_json()

    if 'advertisingID' in data and 'items' in data:
        advertising_id = data['advertisingID']
        items = data['items']
        counter += 1
        id = counter
        orders.append({'id': id, 'advertisingID': advertising_id, 'items': items, 'status': 'On processing'})
        response = {'msg': f'Your order {id} is on processing.'}
        ws = ws_clients.get(advertising_id)
        if ws:
            ws.send(response["msg"])
        return jsonify({
            'success':True
        })
    
@app.route('/orders/get-all', methods=['GET'])
def getAllOrders():
    return jsonify(orders)    

@app.route('/orders/change-status', methods=['GET'])
def changeOrderStatus():
    order_id = request.args.get('id')
    advertising_id = request.args.get('advertising_id')
    status = request.args.get('status')
    for order in orders:
        if order['id'] == int(order_id):
            order['status'] = status
           
    response = {'msg': f'Your order {order_id} is on {status}.'}
    ws = ws_clients.get(advertising_id)
    if ws:
        ws.send(response["msg"])
    return jsonify({
        'success':True,
        'orders':orders
    })
if __name__ == '__main__':
    app.run(host='172.16.1.189', port=8080)