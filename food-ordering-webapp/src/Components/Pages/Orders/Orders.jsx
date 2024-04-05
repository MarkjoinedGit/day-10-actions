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
  const handleOpenHomePage = () =>{
    nevigate('/home')
  }

  const fetchData = async () =>{
    getOrders().then(response => {
      console.log(response.data);
      const jsonString = JSON.stringify(response.data);
    setOrders([
      {
        id:1,
        name:'ban 01',
        status:STATUS_ORDER.Processing
      },
      {
        id:2,
        name:'ban 01',
        status:STATUS_ORDER.AlmostDone
      },
      {
        id:3,
        name:'ban 01',
        status:STATUS_ORDER.Done
      }
    ])
    })
    .catch(error => {
      console.error('Error fetching data:', error);
    });
    
  }

  const handleFetchOrders = () =>{
    fetchData()
  }

  const [orders,setOrders] = useState([])

  
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
    const paramsString = queryString.stringify({id:order.id,status:status});
    editOrderStatus(paramsString)
    .then(response => {
        console.log('Data posted successfully:', response.data);
    })
    .catch(error => {
        console.error('Error posting data:', error);
    });
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
            <ListItem className='order--item'>
                {order.name}
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
