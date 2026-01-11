/*
 * MIT License
 *
 * Copyright (c) 2023 Artiom Bozieac
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import React from 'react';
import Typography from '@mui/material/Typography';
import Product from './Product';
import { useAppSelector } from '../../../hooks';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import { Category } from './AddProductPage';
import { Box } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import { deleteProduct, getProductPhoto } from '../../../api/ProductRequest';
import { getTranslation } from '../../../../i18n/i18n';
import NoPhoto from "../../../../img/no_photo.jpg";
import { useNavigate } from 'react-router-dom';

const MyProductCard = ({ objectID, name, price, categories, description }: Product) => {
    const [isDeleted, setIsDeleted] = React.useState<boolean>(false);
    const [photo, setPhoto] = React.useState(NoPhoto);
    const navigate = useNavigate();
    const lang = useAppSelector(state => state.lang.lang);

    const jwt = useAppSelector(state => state.jwt);

    const handleAlertClick = () => {
        setIsDeleted(false);
    };

    const handleDeleteProduct = async (event: any) => {
        event.stopPropagation();
        setIsDeleted(false);

        const response = await deleteProduct(jwt.token, objectID);

        if (response.status) {
            if (response.status == 401) {
                navigate("/login");
            }
        }

        if (response.status == 200) {
            setIsDeleted(true);
        }

        location.reload();
    }

    React.useEffect(() => {
        const getProductPhotoRequest = async () => {
            const photoBlob = await getProductPhoto(objectID);

            if (photoBlob.size > 0) {
                setPhoto(URL.createObjectURL(photoBlob));
            }
        }

        getProductPhotoRequest();
    }, []);

    const handleEditProduct = () => {
        navigate("/product/edit", {
            state: {
                id: objectID,
                title: name,
                categories: categories,
                price: price,
                description: description
            }
        })
    }

    return (<>
        {isDeleted && (
            <Snackbar 
                open={isDeleted} 
                autoHideDuration={2000} 
                onClose={handleAlertClick}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert onClose={handleAlertClick} id="alert-success" severity="success" sx={{ width: '100%' }}>
                    {getTranslation(lang, "cartitem_deleted_successfully")}
                </Alert>
            </Snackbar>
        )}
        <Box
            onClick={handleEditProduct}
            sx={{
                bgcolor: 'background.paper',
                borderRadius: 2,
                p: 2,
                cursor: 'pointer',
                transition: 'all 0.2s ease-in-out',
                '&:hover': {
                    transform: 'translateY(-2px)',
                    boxShadow: 3,
                },
                display: 'flex',
                alignItems: 'center',
                gap: 2
            }}
        >
            <Box
                sx={{
                    width: 80,
                    height: 80,
                    borderRadius: 2,
                    overflow: 'hidden',
                    flexShrink: 0
                }}
            >
                <img 
                    style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover'
                    }}
                    src={photo} 
                    alt="product-image" 
                    data-testid={"card-image-" + objectID}
                />
            </Box>
            
            <Box sx={{ flex: 1, minWidth: 0 }}>
                <Typography 
                    variant="h6" 
                    sx={{ 
                        fontWeight: 500,
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap"
                    }}
                >
                    {name}
                </Typography>
                <Typography 
                    variant="body2" 
                    color="text.secondary"
                    sx={{
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap"
                    }}
                >
                    {categories?.map(cat => cat.name).join(", ")}
                </Typography>
            </Box>
            
            <Typography 
                variant="h6" 
                sx={{ 
                    fontWeight: 600,
                    color: 'primary.main',
                    minWidth: 80,
                    textAlign: 'right'
                }}
            >
                {price + "€"}
            </Typography>
            
            <IconButton 
                color="error" 
                data-testid="delete-icon" 
                aria-label="delete" 
                onClick={(e) => handleDeleteProduct(e)}
                sx={{
                    border: "1px solid",
                    transition: 'all 0.2s',
                    '&:hover': {
                        bgcolor: 'error.main',
                        color: 'white',
                        borderColor: 'error.main'
                    }
                }}
            >
                <DeleteIcon />
            </IconButton>
        </Box>
    </>
    );
}

export default MyProductCard;