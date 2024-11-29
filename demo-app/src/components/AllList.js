import React, { useEffect, useState } from 'react';
import ProductList from './ProductList';
import UserList from './UserList';
import OrderList from './OrderList';
import ProductService from '../services/ProductService';

const AllList = () => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const response = await ProductService.getProducts();
            console.log('Fetched products:', response.data);
            setProducts(response.data);
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const refreshProductList = () => {
        fetchProducts();
    };

    return (
        <div>
            <h2>All Lists</h2>
            <ProductList products={products} updateProductList={refreshProductList} />
            <UserList />
            <OrderList updateProductList={refreshProductList} />
        </div>
    );
};

export default AllList;