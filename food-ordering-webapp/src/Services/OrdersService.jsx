import axios from 'axios';
import {URL_ENV} from '../ultils/Const'
const API_URL = `${URL_ENV}/orders`;
const getOrders = async () => {
  const requestURL = `${API_URL}/get-all`;
  return await axios.get(requestURL);
};

const editOrderStatus = async (paramsString) => {
    const requestURL = `${API_URL}/change-status?${paramsString}`;
    return await axios.get(requestURL);
  };

export { getOrders,editOrderStatus};