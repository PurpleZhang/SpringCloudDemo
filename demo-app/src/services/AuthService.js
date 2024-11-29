import axios from 'axios';

const API_URL = 'http://localhost:8081/api/user'; // Adjust the URL based on your backend

const login = async (username, password) => {
    return await axios.post(`${API_URL}/login`, {}, {
        params: {
            username,
            password
        }
    });
};

const logout = () => {
    localStorage.removeItem('user');
};

const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem('user'));
};

export default {
    login,
    logout,
    getCurrentUser
};