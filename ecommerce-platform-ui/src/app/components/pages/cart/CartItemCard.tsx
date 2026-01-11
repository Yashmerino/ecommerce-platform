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
import { deleteCartItem, changeQuantity } from '../../../api/CartItemsRequest';
import QuantityInput from '../../QuantityInput';
import { useSnackbar } from '../../SnackbarProvider';
import { getProductPhoto } from '../../../api/ProductRequest';
import { Box, Paper } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import { getTranslation } from '../../../../i18n/i18n';
import NoPhoto from "../../../../img/no_photo.jpg";
import { useNavigate } from 'react-router';

interface CartItemProps {
    id: number,
    productId: number,
    title: string,
    price: string,
    quantity: number,
    onUpdate: (id: number, newQuantity: number) => void 
}

const CartItemCard = ({ id, productId, title, price, quantity, onUpdate }: CartItemProps) => {
  const [photo, setPhoto] = React.useState(NoPhoto);
  const navigate = useNavigate();
  const lang = useAppSelector(state => state.lang.lang);
  const jwt = useAppSelector(state => state.jwt);
  const { showSnackbar } = useSnackbar();

  const handleDeleteProduct = async () => {
    const response = await deleteCartItem(jwt.token, id);

    if (response.status) {
      if (response.status == 401) {
        navigate("/login");
        return;
      }
    }

    if (response.status == 200) {
      showSnackbar(getTranslation(lang, "cartitem_deleted_successfully"), "success");
      onUpdate(id, 0);
    }
  }

  const handleSaveProduct = async () => {
    const updatedQuantity = (document.getElementById(`quantity-input-${id}`) as HTMLInputElement).value;
    const response = await changeQuantity(jwt.token, id, parseInt(updatedQuantity));

    if (response.status) {
      if (response.status == 401) {
        navigate("/login");
        return;
      }
    }

    onUpdate(id, parseInt(updatedQuantity));
  }

  React.useEffect(() => {
    const getProductPhotoRequest = async () => {
      const photoBlob = await getProductPhoto(productId);

      if ((photoBlob as unknown as Response).status) {
        if ((photoBlob as unknown as Response).status == 401) {
          navigate("/login");
        }
      }

      if (photoBlob.size > 0) {
        setPhoto(URL.createObjectURL(photoBlob));
      }
    }

    getProductPhotoRequest();
  }, []);

  return (
    <>
      <Paper
        elevation={0}
        sx={{
          p: 2,
          borderRadius: 2,
          border: 1,
          borderColor: 'divider',
          transition: '0.2s',
          '&:hover': {
            boxShadow: 2,
            borderColor: 'primary.main',
          },
        }}
      >
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: { xs: 1, sm: 2, md: 3 },
            flexWrap: { xs: 'wrap', sm: 'nowrap' },
          }}
        >
          {/* Product Image */}
          <Box
            sx={{
              width: { xs: 60, sm: 80, md: 100 },
              height: { xs: 60, sm: 80, md: 100 },
              borderRadius: 1,
              overflow: 'hidden',
              flexShrink: 0,
            }}
          >
            <img
              width="100%"
              height="100%"
              className="card-image"
              src={photo}
              alt="cart-item"
              data-testid={`card-image-${id}`}
              style={{
                objectFit: 'cover',
                width: '100%',
                height: '100%',
              }}
            />
          </Box>

          {/* Product Details */}
          <Box sx={{ flex: 1, minWidth: 0 }}>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 500,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                mb: 1,
              }}
            >
              {title}
            </Typography>
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 2,
                flexWrap: { xs: 'wrap', sm: 'nowrap' },
              }}
            >
              <QuantityInput
                id={id}
                defaultValue={quantity}
                handleSaveProduct={handleSaveProduct}
              />
              <Typography
                variant="h6"
                sx={{
                  fontWeight: 600,
                  color: 'primary.main',
                  minWidth: 80,
                  textAlign: 'right',
                }}
              >
                {price + "€"}
              </Typography>
            </Box>
          </Box>

          {/* Delete Button */}
          <IconButton
            color="error"
            aria-label="delete from shopping cart"
            data-testid="delete-icon"
            onClick={handleDeleteProduct}
            sx={{
              p: 1,
              borderRadius: 1,
              '&:hover': {
                bgcolor: 'error.light',
                '& .MuiSvgIcon-root': {
                  color: 'error.main',
                },
              },
            }}
          >
            <DeleteIcon />
          </IconButton>
        </Box>
      </Paper>
    </>
  );
}

export default CartItemCard;