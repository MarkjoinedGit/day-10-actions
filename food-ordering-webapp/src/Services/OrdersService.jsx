import axios from 'axios';
const API_URL = `api/orders`;
const getOrders = async () => {
  const requestURL = `${API_URL}/get-all`;
  return await axios.get(requestURL);
};

const editOrderStatus = async (paramsString) => {
    const requestURL = `${API_URL}/edit-status?${paramsString}`;
    return await axios.post(requestURL);
  };

export { getOrders,editOrderStatus};