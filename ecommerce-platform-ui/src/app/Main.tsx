import React from 'react';

import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './components/pages/auth/LoginPage';
import RegisterPage from './components/pages/auth/RegisterPage';
import ProductsContainer from './components/pages/products/ProductsContainer';
import CartContainer from './components/pages/cart/CartContainer';
import AddProductPage from './components/pages/products/AddProductPage';
import MyProductsPage from './components/pages/products/MyProductsPage';
import MyProfilePage from './components/pages/user/MyProfilePage';
import EditProductPage from './components/pages/products/EditProductPage';
import { useAppSelector } from './hooks';
import SearchPage from './components/pages/search/SearchPage';
import { ALGOLIA_USAGE } from '../env-config';

const Main = () => {
    const jwt = useAppSelector(state => state.jwt.token);

    return (
        <Routes>
            <Route path='/' element={<LoginPage />} />
            <Route path='/login' element={<LoginPage />} />
            <Route path='/register' element={<RegisterPage />} />
            <Route path='/products' element={jwt.length > 0 ? <ProductsContainer /> : <Navigate to='/login' />} />
            <Route path='/cart' element={jwt.length > 0 ? <CartContainer /> : <Navigate to='/login' />} />
            <Route path='/product/add' element={jwt.length > 0 ? <AddProductPage /> : <Navigate to='/login' />} />
            <Route path='/profile/products' element={jwt.length > 0 ? <MyProductsPage /> : <Navigate to='/login' />} />
            <Route path='/profile' element={jwt.length > 0 ? <MyProfilePage /> : <Navigate to='/login' />} />
            <Route path='/product/edit' element={jwt.length > 0 ? <EditProductPage /> : <Navigate to='/login' />} />
            <Route path='/search' element={jwt.length > 0 && ALGOLIA_USAGE ? <SearchPage /> : <Navigate to='/login' />} />
        </Routes>
    );
}
export default Main;