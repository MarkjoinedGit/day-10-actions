import random
from flask import Flask, Response, request, jsonify

app = Flask(__name__)

counter = 0

orders = []
food=[
    {
        "id": 1,
        "name": "Hamburger",
        "image":"image\hamburger.jpg"
    },
    {
        "id": 2,
        "name": "Pho bo",
        "image":"image\phobo.jpg"
    },
    {
        "id": 3,
        "name": "Rice chicken",
        "image":"image\comga.jpg"
    }
]
menu=[
    {
        "id": 1,
        "advertisingID": "1",
        "items": ["dish 1", "dish 2", "dish 3"],
        "status": "Processing"
    },
    {
        "id": 2,
        "advertisingID": "2",
        "items": ["dish A", "dish B", "dish C"],
        "status": "almost done"
    },
    {
        "id": 3,
        "advertisingID": "3",
        "items": ["dish X", "dish Y", "dish Z"],
        "status": "Done"
    }
]

@app.route('/food',methods=['GET'])
def getFood():
    return jsonify(
            {
                'food': [
                {
                    "id": 1,
                    "name": "Hamburger",
                    "image":"image/hamburger.jpg"
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
                ],
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
    
@app.route('/menu/get_detail', methods=['GET'])
def get_menu_item_by_id():
    item_id = request.args.get('id')
    if item_id is not None:
        try:
            item_id = int(item_id)
            item = next((i for i in menu if i['id'] == item_id), None)
            if item:
                return jsonify({'item': item})
            else:
                return jsonify({'message': 'Menu item not found', 'result': 'failure'}), 404
        except ValueError:
            return jsonify({'message': 'Invalid item id', 'result': 'failure'}), 400
    else:
        return jsonify({'message': 'Item id is missing', 'result': 'failure'}), 400
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)