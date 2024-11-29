import axios from 'axios';

const ORDER_API_BASE_URL = "http://localhost:8083/api/order"; // Ensure this matches your backend URL

const OrderService = {
    getOrders: () => {
        return axios.get(`${ORDER_API_BASE_URL}/`);
    },
    cancelOrder: (orderId) => {
        return axios.put(`${ORDER_API_BASE_URL}/${orderId}/status`, 'CANCELLED', {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    },
    updateOrderStatus: (orderId, status) => {
        return axios.put(`${ORDER_API_BASE_URL}/${orderId}/status`, status, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    },
    addOrder: (orderData) => {
        return axios.post(`${ORDER_API_BASE_URL}/`, orderData);
    }
};

export default OrderService;



