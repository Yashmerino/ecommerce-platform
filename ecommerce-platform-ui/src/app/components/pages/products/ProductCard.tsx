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
import { Box, IconButton } from '@mui/material';
import { useSnackbar } from '../../SnackbarProvider';
import { addProductToCart, getProductPhoto } from '../../../api/ProductRequest';
import { useAppSelector } from '../../../hooks';
import { useNavigate } from 'react-router-dom';
import { getTranslation } from '../../../../i18n/i18n';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import NoPhoto from "../../../../img/no_photo.jpg";

interface CategoryProp {
  id: number,
  name: string
}

interface ProductCardProps {
  id: number,
  title: string,
  price: string,
  categories: CategoryProp[],
  description: string,
}

const ProductCard = ({ id, title, price, categories, description }: ProductCardProps) => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();

  const roles = useAppSelector(state => state.info.info.roles);
  const lang = useAppSelector(state => state.lang.lang);
  const [photo, setPhoto] = React.useState(NoPhoto);

  const jwt = useAppSelector(state => state.jwt);

  const handleAddProduct = async () => {
    const response = await addProductToCart(jwt.token, id, 1);

    if (response.status == 200) {
      showSnackbar(getTranslation(lang, "product_added_to_cart_successfully"), "success");
    }
  }

  const handleEditProduct = async () => {
    // @ts-ignore 
    if (roles[0].name == "USER") {
      return;
    }

    navigate("/product/edit", {
      state: {
        id: id,
        title: title,
        categories: categories,
        price: price,
        description: description
      }
    })
  }

  React.useEffect(() => {
    const getProductPhotoRequest = async () => {
      const photoBlob = await getProductPhoto(id);

      if (photoBlob.size > 0) {
        setPhoto(URL.createObjectURL(photoBlob));
      }
    }

    getProductPhotoRequest();
  }, []);

  return (
    <Box
        onClick={handleEditProduct}
        className={roles[0].name == "SELLER" ? "my-product-card" : ""}
        sx={{
          backgroundColor: 'background.paper',
          borderRadius: 2,
          boxShadow: 1,
          overflow: 'hidden',
          transition: 'all 0.3s ease-in-out',
          '&:hover': {
            boxShadow: 6,
            transform: 'translateY(-4px)',
          },
        }}
      >
        <Box
          sx={{
            position: 'relative',
            width: { xs: '280px', sm: '320px' },
            height: '200px',
            overflow: 'hidden',
          }}
          id={title + '-' + id}
        >
          <img
            width="100%"
            height="100%"
            src={photo}
            data-testid={"card-image-" + id}
            alt="product-photo"
            style={{
              objectFit: "cover",
              transition: 'transform 0.3s ease-in-out',
            }}
          />
          {roles[0].name == "USER" && (
            <IconButton
              color="primary"
              aria-label="add to shopping cart"
              onClick={(e) => {
                e.stopPropagation();
                handleAddProduct();
              }}
              id={"add-product-" + id}
              sx={{
                position: 'absolute',
                right: 8,
                bottom: 8,
                backgroundColor: 'background.paper',
                '&:hover': {
                  backgroundColor: 'primary.light',
                  color: 'background.paper',
                },
                boxShadow: 2,
              }}
            >
              <AddShoppingCartIcon />
            </IconButton>
          )}
        </Box>
        <Box
          sx={{
            p: 2,
            display: 'flex',
            flexDirection: 'column',
            gap: 1,
          }}
        >
          <Typography
            variant="caption"
            color="primary"
            sx={{
              textTransform: 'uppercase',
              letterSpacing: 0.5,
            }}
          >
            {categories && categories.length > 0 ? categories[0].name : getTranslation(lang, "no_category")}
          </Typography>
          <Typography
            variant="h6"
            sx={{
              fontWeight: 600,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
            }}
          >
            {title}
          </Typography>
          <Typography
            variant="h5"
            color="primary"
            sx={{
              fontWeight: 700,
            }}
          >
            {price + "€"}
          </Typography>
        </Box>
      </Box>
  );
}

export default ProductCard;