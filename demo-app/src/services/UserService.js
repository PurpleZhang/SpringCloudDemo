import axios from 'axios';

const USER_API_BASE_URL = "http://localhost:8081/api/user"; // Ensure this matches your backend URL

const UserService = {
    getUsers: () => {
        return axios.get(USER_API_BASE_URL);
    },
    addUser: (user) => {
        return axios.post(USER_API_BASE_URL, user);
    },
    updateUser: (userId, user) => {
        return axios.put(`${USER_API_BASE_URL}/${userId}`, user);
    }
};

export default UserService;



