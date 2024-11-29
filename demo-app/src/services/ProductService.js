import axios from 'axios';

const PRODUCT_API_BASE_URL = "http://localhost:8082/api/product"; // Ensure this matches your backend URL

const ProductService = {
    getProducts: () => {
        return axios.get(PRODUCT_API_BASE_URL);
    },
    addProduct: (product) => {
        return axios.post(PRODUCT_API_BASE_URL, product);
    },
    updateProduct: (productId, product) => {
        return axios.put(`${PRODUCT_API_BASE_URL}/${productId}`, product);
    },
    deleteProduct: (productId) => {
        return axios.delete(`${PRODUCT_API_BASE_URL}/${productId}`);
    }
};

export default ProductService;



