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
import { useAppSelector } from '../../../hooks';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import { Box } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import { addProductToCart, getProductPhoto } from '../../../api/ProductRequest';
import { getTranslation } from '../../../../i18n/i18n';
import NoPhoto from "../../../../img/no_photo.jpg";
import { useNavigate } from 'react-router-dom';

interface ISearchHit {
    objectID: number,
    name: string,
    price: number
}

const SearchHit = ({ objectID, name, price }: ISearchHit) => {
    const [isSuccess, setIsSuccess] = React.useState<boolean>(false);
    const [photo, setPhoto] = React.useState(NoPhoto);
    const lang = useAppSelector(state => state.lang.lang);
    const jwt = useAppSelector(state => state.jwt);
    const navigate = useNavigate();

    const handleAlertClick = () => {
        setIsSuccess(false);
    };

    const handleAddProduct = async () => {
        setIsSuccess(false);

        const response = await addProductToCart(jwt.token, objectID, 1);

        if (response.status) {
            if (response.status == 401) {
                navigate("/login");
            }
        }

        if (response.status == 200) {
            setIsSuccess(true);
        }
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

    return (
        <>
            {isSuccess && (
                <Snackbar 
                    open={isSuccess} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert 
                        onClose={handleAlertClick} 
                        severity="success" 
                        id="alert-success"
                        sx={{ 
                            width: '100%',
                            borderRadius: 2
                        }}
                    >
                        {getTranslation(lang, "product_added_successfully")}
                    </Alert>
                </Snackbar>
            )}
            <Box
                sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 2,
                    p: 2.5,
                    borderRadius: 2,
                    backgroundColor: 'background.paper',
                    boxShadow: 2,
                    border: 1,
                    borderColor: 'divider',
                    transition: 'all 0.3s ease-in-out',
                    '&:hover': {
                        boxShadow: 4,
                        transform: 'translateY(-4px)',
                        borderColor: 'primary.main',
                        backgroundColor: 'background.default'
                    }
                }}
            >
                    <Box
                    sx={{
                        width: '100px',
                        height: '100px',
                        borderRadius: 2,
                        overflow: 'hidden',
                        flexShrink: 0,
                        boxShadow: 1,
                        border: 1,
                        borderColor: 'divider',
                    }}
                >
                    <img 
                        width="100%" 
                        height="100%" 
                        className="card-image" 
                        src={photo} 
                        alt={getTranslation(lang, "product_image")}
                        data-testid={"card-image-" + objectID}
                        style={{
                            objectFit: 'cover',
                            transition: 'transform 0.3s ease-in-out'
                        }}
                    />
                </Box>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography 
                        variant="h6" 
                        sx={{ 
                            fontWeight: 600,
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            whiteSpace: "nowrap",
                            mb: 1,
                            color: 'text.primary'
                        }}
                    >
                        {name}
                    </Typography>
                    <Typography 
                        variant="h5" 
                        color="primary.main"
                        sx={{ 
                            fontWeight: 700,
                            mt: 0.5
                        }}
                    >
                        {price + "€"}
                    </Typography>
                </Box>
                <IconButton 
                    color="primary" 
                    data-testid="add-icon" 
                    aria-label={getTranslation(lang, "add_to_cart")}
                    onClick={handleAddProduct}
                    sx={{
                        backgroundColor: 'background.paper',
                        boxShadow: 2,
                        p: 1.5,
                        border: 1,
                        borderColor: 'primary.main',
                        '&:hover': {
                            backgroundColor: 'primary.main',
                            color: 'background.paper',
                            transform: 'scale(1.1)'
                        },
                        transition: 'all 0.2s ease-in-out'
                    }}
                >
                    <AddShoppingCartIcon />
                </IconButton>
            </Box>
        </>
    );
}

export default SearchHit;