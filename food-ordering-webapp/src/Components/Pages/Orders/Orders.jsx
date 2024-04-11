import {useState,useEffect} from 'react';
import { useNavigate,Link } from 'react-router-dom';
import queryString from 'query-string';
import {
    List,
    ListItem,
    ListIcon,
    OrderedList,
    UnorderedList,
  } from '@chakra-ui/react'
import { CheckIcon, ExternalLinkIcon,SpinnerIcon,ArrowBackIcon,ViewIcon } from '@chakra-ui/icons'
import { Button, ButtonGroup } from '@chakra-ui/react'
import './Orders.css'
import { getOrders,editOrderStatus } from '../../../Services/OrdersService';
import { Container } from '@chakra-ui/react'
import { Stack, HStack, VStack } from '@chakra-ui/react'
import {STATUS_ORDER} from '../../../ultils/Const'


const Orders = () => {
  const nevigate = useNavigate()
  const [orders,setOrders] = useState([])
  const handleOpenHomePage = () =>{
    nevigate('/home')
  }
  const fetchData = async () => {
    try {
      const response = await getOrders();
      setOrders(response.data); 
      console.log(response.data)
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleFetchOrders = () =>{
    fetchData()
  }

  const handleChangeStatusOrder=  async (order)=>{
    let status =''
    switch (order.status){
      case STATUS_ORDER.Processing:
        status = STATUS_ORDER.AlmostDone
        break
      case STATUS_ORDER.AlmostDone:
        status = STATUS_ORDER.Done
        break
      default:
        return;
    }
    const paramsString = queryString.stringify({id:order.id,advertising_id:order.advertising_id,status:status});
    try {
      const response = await editOrderStatus(paramsString);
      console.log(response.data)
      if (response.data.success === true){
        setOrders(response.data.orders)
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }

  const renderButton = (order) => {
    switch (order.status){
      case STATUS_ORDER.Processing:
        return (
          <Button leftIcon={<ExternalLinkIcon />} colorScheme='orange' variant='solid' onClick={()=>handleChangeStatusOrder(order)}>
            {order.status}
          </Button>
        );
      case STATUS_ORDER.AlmostDone:
        return (
          <Button leftIcon={<CheckIcon />} colorScheme='teal' variant='solid' onClick={()=>handleChangeStatusOrder(order)}>
            {order.status}
          </Button>
        );
      default:
        return null;
    }
  }

  return (
    <VStack>
        <h2>Orders</h2>
        <Button
          colorScheme='blue'
          leftIcon={<ArrowBackIcon/>}
          onClick={handleOpenHomePage}>
         Back Home Page
        </Button>
        <Button
          colorScheme='blue'
          leftIcon={<SpinnerIcon/>}
          onClick={handleFetchOrders}>
          Fetch Orders
        </Button>
        <List spacing={3} className='orders'>
        {orders.map((order) => (
            <ListItem key={order.id} className='order--item'>
                {order.id}
                {renderButton(order)}
                <Button leftIcon={<ViewIcon />} colorScheme='teal' variant='solid' onClick={()=>handleChangeStatusOrder(order)}>
                  <Link to={{ pathname: '/order-details', state: { order } }}>Details</Link>
                </Button>
            </ListItem>
        ))}
        </List>
    </VStack>
  );
}

export default Orders;
