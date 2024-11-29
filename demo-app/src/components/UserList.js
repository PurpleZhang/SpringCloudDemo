import React, { useEffect, useState } from 'react';
import UserService from '../services/UserService';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [newUser, setNewUser] = useState({ username: '', password: '', role: '' });
    const [editingUserId, setEditingUserId] = useState(null);
    const [editedUser, setEditedUser] = useState({ username: '', password: '', role: '' });
    const [showAddForm, setShowAddForm] = useState(false);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await UserService.getUsers();
            console.log('Fetched users:', response.data);
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewUser({
            ...newUser,
            [name]: value
        });
    };

    const handleEditInputChange = (e) => {
        const { name, value } = e.target;
        setEditedUser({
            ...editedUser,
            [name]: value
        });
    };

    const addUser = async () => {
        try {
            await UserService.addUser(newUser);
            fetchUsers();
            setNewUser({ username: '', password: '', role: '' });
            setShowAddForm(false); // Hide the form after adding a user
        } catch (error) {
            console.error('Error adding user:', error);
        }
    };

    const editUser = async (userId) => {
        if (editingUserId === userId) {
            try {
                await UserService.updateUser(userId, editedUser);
                fetchUsers();
                setEditingUserId(null);
            } catch (error) {
                console.error('Error updating user:', error);
            }
        } else {
            const userToEdit = users.find(user => user.id === userId);
            setEditingUserId(userId);
            setEditedUser(userToEdit);
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h2>User List</h2>

            <button onClick={() => setShowAddForm(!showAddForm)} style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer', marginBottom: '20px' }}>
                {showAddForm ? 'Hide Add Form' : 'Add New User'}
            </button>

            {showAddForm && (
                <form onSubmit={(e) => { e.preventDefault(); addUser(); }} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Username:
                        <input
                            type="text"
                            name="username"
                            value={newUser.username}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Password:
                        <input
                            type="password"
                            name="password"
                            value={newUser.password}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Role:
                        <input
                            type="text"
                            name="role"
                            value={newUser.role}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>Add User</button>
                </form>
            )}

            <table border="1" cellPadding="5" cellSpacing="0" style={{ margin: 'auto', width: '80%', maxWidth: '600px' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Password</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>
                                {editingUserId === user.id ? (
                                    <input
                                        type="text"
                                        name="username"
                                        value={editedUser.username}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    user.username
                                )}
                            </td>
                            <td>
                                {editingUserId === user.id ? (
                                    <input
                                        type="password"
                                        name="password"
                                        value={editedUser.password}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    '*****'
                                )}
                            </td>
                            <td>
                                {editingUserId === user.id ? (
                                    <input
                                        type="text"
                                        name="role"
                                        value={editedUser.role}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    user.role
                                )}
                            </td>
                            <td>
                                {editingUserId === user.id ? (
                                    <>
                                        <button onClick={() => editUser(user.id)}>Save</button>
                                        <button onClick={() => setEditingUserId(null)}>Cancel</button>
                                    </>
                                ) : (
                                    <button onClick={() => editUser(user.id)}>Edit</button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserList;



