import React, { useState } from 'react';
import axios from 'axios';

const LoginForm = ({ onLoginSuccess }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8081/api/user/login', {}, {
                params: {
                    username,
                    password
                }
            });
            const user = response.data;
            if (user.role === 'ADMIN') {
                setMessage(`Logged in successfully as ${user.username}`);
                onLoginSuccess(user); // Notify parent component of successful login and pass user data
            } else {
                setMessage('Login successful, but you do not have permission to view ALL Lists.');
            }
        } catch (error) {
            setMessage('Invalid credentials');
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h2>Login</h2>
            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px' }}>
                <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                    Username:
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={{ width: '100%', padding: '5px' }}
                    />
                </label>
                <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={{ width: '100%', padding: '5px' }}
                    />
                </label>
                <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>Login</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default LoginForm;