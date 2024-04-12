import {useState,useEffect} from 'react';
import { useNavigate } from 'react-router-dom';
import queryString from 'query-string';
import {
    List,
    ListItem,
    HStack,
    VStack,
    Button
  } from '@chakra-ui/react'
import { CheckIcon, ExternalLinkIcon,SpinnerIcon,ArrowBackIcon } from '@chakra-ui/icons'
import './Orders.css'
import { getOrders,editOrderStatus } from '../../../Services/OrdersService';
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
  useEffect(()=>{
    fetchData()
  },[])

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
    const paramsString = queryString.stringify({id:order.id,advertising_id:order.advertisingID,status:status});
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
            {STATUS_ORDER.AlmostDone}
          </Button>
        );
      case STATUS_ORDER.AlmostDone:
        return (
          <Button leftIcon={<CheckIcon />} colorScheme='teal' variant='solid' onClick={()=>handleChangeStatusOrder(order)}>
            {STATUS_ORDER.Done}
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
    order.status !== STATUS_ORDER.Done && (
      <ListItem key={order.id} className='order--item'>
        <HStack>
          <p>ID: {order.id} |</p>
          {order.items.map((item) => (
            <p key={item.name}>
              {item.name}: {item.count},
            </p>
          ))}
        </HStack>
        {renderButton(order)}
      </ListItem>
    )
  ))}
</List>
    </VStack>
  );
}

export default Orders;
