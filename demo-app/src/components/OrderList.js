import React, { useEffect, useState } from 'react';
import OrderService from '../services/OrderService';
import ProductService from '../services/ProductService';

const OrderList = ({ updateProductList }) => {
    const [orders, setOrders] = useState([]);
    const [showAddForm, setShowAddForm] = useState(false);
    const [userId, setUserId] = useState('');
    const [productId, setProductId] = useState('');
    const [quantity, setQuantity] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const response = await OrderService.getOrders();
            console.log('Fetched orders:', response.data);
            setOrders(response.data);
        } catch (error) {
            console.error('Error fetching orders:', error);
        }
    };

    const cancelOrder = async (orderId) => {
        try {
            // Cancel the order
            await OrderService.cancelOrder(orderId);

            // Refresh the order list and show success message
            fetchOrders();

            // Refresh the product list by calling the callback function
            updateProductList();

            setSuccessMessage(`Order with ID ${orderId} cancelled successfully.`);
            setErrorMessage('');
        } catch (error) {
            console.error('Error cancelling order:', error);
            setErrorMessage('Failed to cancel order.');
            setSuccessMessage('');
        }
    };

    const completeOrder = async (orderId) => {
        try {
            await OrderService.updateOrderStatus(orderId, 'COMPLETED');
            fetchOrders(); // Refresh the order list after updating status
        } catch (error) {
            console.error('Error completing order:', error);
        }
    };

    const handleAddOrder = async (e) => {
        e.preventDefault();
        try {
            const response = await OrderService.addOrder({
                userId: parseInt(userId),
                productId: parseInt(productId),
                quantity: parseInt(quantity)
            });
            setSuccessMessage(`Order added successfully with ID ${response.data.id}`);
            setUserId('');
            setProductId('');
            setQuantity('');
            setShowAddForm(false); // Hide the form after adding an order
            // Refresh the product list by calling the callback function
            updateProductList();
            
            fetchOrders(); // Refresh the order list after adding a new order
            setErrorMessage(''); // Clear any previous error messages
        } catch (error) {
            if (error.response) {
                setErrorMessage(error.response.data.message || 'An unexpected error occurred.');
            } else {
                setErrorMessage('An unexpected error occurred while adding the order.');
            }
            setSuccessMessage(''); // Clear any previous success messages
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '50px', marginBottom: '50px' }}>
            <h2>Order List</h2>
            <button onClick={() => setShowAddForm(!showAddForm)} style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer', marginBottom: '20px' }}>
                {showAddForm ? 'Hide Add Form' : 'Add New Order'}
            </button>
            {showAddForm && (
                <form onSubmit={handleAddOrder} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        User ID:
                        <input
                            type="number"
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Product ID:
                        <input
                            type="number"
                            value={productId}
                            onChange={(e) => setProductId(e.target.value)}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Quantity:
                        <input
                            type="number"
                            value={quantity}
                            onChange={(e) => setQuantity(e.target.value)}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>Add Order</button>
                </form>
            )}
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
            {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
            <table border="1" cellPadding="5" cellSpacing="0" style={{ margin: 'auto', width: '80%', maxWidth: '600px' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>User ID</th>
                        <th>Product ID</th>
                        <th>Quantity</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.userId}</td>
                            <td>{order.productId}</td>
                            <td>{order.quantity}</td>
                            <td>{order.status}</td>
                            <td>
                                {order.status === 'PENDING' && (
                                    <>
                                        <button onClick={() => cancelOrder(order.id)} style={{ padding: '5px 10px', backgroundColor: '#f44336', color: 'white', border: 'none', cursor: 'pointer', marginRight: '5px' }}>Cancel</button>
                                        <button onClick={() => completeOrder(order.id)} style={{ padding: '5px 10px', backgroundColor: '#2196F3', color: 'white', border: 'none', cursor: 'pointer' }}>Complete</button>
                                    </>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default OrderList;