import React, { useEffect, useState } from 'react';
import ProductService from '../services/ProductService';

const ProductList = ({ products, updateProductList }) => {
    const [newProduct, setNewProduct] = useState({ name: '', price: 0, inventory: 0 });
    const [editingProductId, setEditingProductId] = useState(null);
    const [editedProduct, setEditedProduct] = useState({ name: '', price: 0, inventory: 0 });
    const [showAddForm, setShowAddForm] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewProduct({
            ...newProduct,
            [name]: value
        });
    };

    const handleEditInputChange = (e) => {
        const { name, value } = e.target;
        setEditedProduct({
            ...editedProduct,
            [name]: value
        });
    };

    const addProduct = async () => {
        try {
            await ProductService.addProduct(newProduct);
            updateProductList(); // Call the refresh function passed as a prop
            setNewProduct({ name: '', price: 0, inventory: 0 });
            setShowAddForm(false); // Hide the form after adding a product
        } catch (error) {
            console.error('Error adding product:', error);
        }
    };

    const editProduct = async (productId) => {
        if (editingProductId === productId) {
            try {
                await ProductService.updateProduct(productId, editedProduct);
                updateProductList(); // Call the refresh function passed as a prop
                setEditingProductId(null);
            } catch (error) {
                console.error('Error updating product:', error);
            }
        } else {
            const productToEdit = products.find(product => product.id === productId);
            setEditingProductId(productId);
            setEditedProduct(productToEdit);
        }
    };

    const deleteProduct = async (productId) => {
        try {
            const response = await ProductService.deleteProduct(productId);
            if (response.status === 204 || response.status === 200) {
                updateProductList(); // Call the refresh function passed as a prop
            } else {
                throw new Error(`Failed to delete product. Status code: ${response.status}`);
            }
        } catch (error) {
            console.error('Error deleting product:', error);
            if (error.response && error.response.status === 400) {
                setErrorMessage(error.response.data.message || 'An error occurred while deleting the product.');
            } else {
                setErrorMessage('An unexpected error occurred.');
            }
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h2>Product List</h2>

            {errorMessage && (
                <div style={{ color: 'red', marginBottom: '20px' }}>{errorMessage}</div>
            )}

            <button onClick={() => setShowAddForm(!showAddForm)} style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer', marginBottom: '20px' }}>
                {showAddForm ? 'Hide Add Form' : 'Add New Product'}
            </button>

            {showAddForm && (
                <form onSubmit={(e) => { e.preventDefault(); addProduct(); }} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Name:
                        <input
                            type="text"
                            name="name"
                            value={newProduct.name}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Price:
                        <input
                            type="number"
                            name="price"
                            value={newProduct.price}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <label style={{ marginBottom: '10px', textAlign: 'left', width: '300px' }}>
                        Inventory:
                        <input
                            type="number"
                            name="inventory"
                            value={newProduct.inventory}
                            onChange={handleInputChange}
                            style={{ width: '100%', padding: '5px' }}
                        />
                    </label>
                    <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>Add Product</button>
                </form>
            )}

            <table border="1" cellPadding="5" cellSpacing="0" style={{ margin: 'auto', width: '80%', maxWidth: '600px' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Inventory</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(product => (
                        <tr key={product.id}>
                            <td>{product.id}</td>
                            <td>
                                {editingProductId === product.id ? (
                                    <input
                                        type="text"
                                        name="name"
                                        value={editedProduct.name}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    product.name
                                )}
                            </td>
                            <td>
                                {editingProductId === product.id ? (
                                    <input
                                        type="number"
                                        name="price"
                                        value={editedProduct.price}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    `$${product.price.toFixed(2)}`
                                )}
                            </td>
                            <td>
                                {editingProductId === product.id ? (
                                    <input
                                        type="number"
                                        name="inventory"
                                        value={editedProduct.inventory}
                                        onChange={handleEditInputChange}
                                        style={{ width: '100%' }}
                                    />
                                ) : (
                                    product.inventory
                                )}
                            </td>
                            <td>
                                {editingProductId === product.id ? (
                                    <>
                                        <button onClick={() => editProduct(product.id)}>Save</button>
                                        <button onClick={() => setEditingProductId(null)}>Cancel</button>
                                    </>
                                ) : (
                                    <>
                                        <button onClick={() => editProduct(product.id)}>Edit</button>
                                        <button onClick={() => deleteProduct(product.id)}>Delete</button>
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

export default ProductList;