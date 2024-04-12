import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Orders from './Components/Pages/Orders';
import Home from './Components/Pages/Home';
function App() {
  return (
    <div className="App">
      <Router>
        <div>
          <Routes>
            <Route path="/" element={<Home/>} />
            <Route path="/home" exact element={<Home/>} />
            <Route path="/orders" element={<Orders/>} />
          </Routes>
        </div>
    </Router>
    </div>
  );
}

export default App;
