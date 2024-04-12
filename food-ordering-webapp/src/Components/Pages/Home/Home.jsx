import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Stack, HStack, VStack } from '@chakra-ui/react'
import { Button, ButtonGroup } from '@chakra-ui/react'
import { HamburgerIcon ,StarIcon} from '@chakra-ui/icons'


const Home = () => {
  const nevigate = useNavigate()

  const handleOpenOrdersPage = () =>{
    nevigate('/orders')
  }

  const handleOpenMobileApp=()=>{
    var appScheme = "food_ordering_app://";
    window.location.href = appScheme
  }
  
  return (
    <VStack className='home'>
      <h2> FOOD ORDERING WEBSITE!</h2>
        <Button
          colorScheme='blue'
          leftIcon={<HamburgerIcon/>}
          onClick={handleOpenOrdersPage}>
            Orders
        </Button>
        <Button
          colorScheme='blue'
          leftIcon={<StarIcon/>}
          onClick={handleOpenMobileApp}>
            Food Ordering Mobile App
        </Button>
    </VStack>
  );
}

export default Home;
