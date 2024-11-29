import React, { useEffect, useState } from 'react';
import AllList from './components/AllList';
import LoginForm from './components/LoginForm';
import AuthService from './services/AuthService';

const App = () => {
    const [currentUser, setCurrentUser] = useState(undefined);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
        }
    }, []);

    const onLoginSuccess = (user) => {
        localStorage.setItem('user', JSON.stringify(user));
        setCurrentUser(user);
    };

    const logOut = () => {
        AuthService.logout();
        setCurrentUser(undefined);
    };

    return (
        <div>
            {!currentUser ? (
                <LoginForm onLoginSuccess={onLoginSuccess} />
            ) : (
                <>
                    <header style={{ textAlign: 'center', marginTop: '20px' }}>
                        <h1>Welcome, {currentUser.username}</h1>
                        {currentUser.role === 'ADMIN' ? (
                            <>
                                <button onClick={logOut} style={{ padding: '10px 20px', backgroundColor: '#f44336', color: 'white', border: 'none', cursor: 'pointer' }}>Logout</button>
                                <AllList />
                            </>
                        ) : (
                            <p>You do not have permission to view ALL Lists.</p>
                        )}
                    </header>
                </>
            )}
        </div>
    );
};

export default App;