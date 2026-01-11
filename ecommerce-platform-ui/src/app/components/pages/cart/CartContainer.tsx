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
import * as React from 'react';
import { Box, Container, Typography, Paper, Button, Divider, Checkbox, FormControlLabel, Pagination } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PaymentsIcon from '@mui/icons-material/Payments';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import { CurrencyBitcoin } from '@mui/icons-material';

import Header from '../../Header';
import Copyright from '../../footer/Copyright';
import CartItemCard from './CartItemCard';
import { getCartItems } from '../../../api/CartItemsRequest';
import { useAppSelector } from '../../../hooks';
import { getTranslation } from '../../../../i18n/i18n';
import { PaginatedDTO } from '../../../../types/PaginatedDTO';

export interface CartItem {
  id: number;
  productId: number;
  name: string;
  price: string;
  quantity: number;
}

const CartContainer = () => {
  const jwt = useAppSelector(state => state.jwt);
  const username = useAppSelector(state => state.username.sub);
  const lang = useAppSelector(state => state.lang.lang);
  const navigate = useNavigate();

  const [pagination, setPagination] = React.useState<PaginatedDTO<CartItem>>({
    data: [],
    currentPage: 0,
    totalPages: 0,
    totalItems: 0,
    totalPrice: 0,
    pageSize: 5,
    hasNext: false,
    hasPrevious: false,
  });

  const [total, setTotal] = React.useState<number>(0);

  const fetchCartItems = async (page = 0) => {
    const items = await getCartItems(jwt.token, username, page, pagination.pageSize);
    if ('status' in items && items.status === 401) {
      navigate("/login");
      return;
    }
    setPagination(items);
  };

  React.useEffect(() => {
    fetchCartItems();
  }, [jwt.token, username, navigate]);

  React.useEffect(() => {
    setTotal(pagination.totalPrice);
  }, [pagination.data]);

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
      <Header />
      <Container maxWidth="lg" sx={{ flex: 1, py: 4 }}>
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" fontWeight={700} sx={{ display: 'flex', alignItems: 'center', gap: 2, color: 'primary.main' }}>
            <ShoppingCartIcon fontSize="large" />
            {getTranslation(lang, "my_cart")}
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 3 }}>
          {/* Cart Items */}
          <Paper elevation={0} sx={{ flex: { xs: 1, md: 2 }, borderRadius: 2, bgcolor: 'background.paper', overflow: 'hidden' }}>
            <Box sx={{ height: { xs: 'auto', md: '60vh' }, overflowY: 'auto', p: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
              {pagination.data.length > 0 ? (
                pagination.data.map(cartItem => (
                  <CartItemCard
                    key={cartItem.id}
                    id={cartItem.id}
                    productId={cartItem.productId}
                    title={cartItem.name}
                    price={cartItem.price}
                    quantity={cartItem.quantity}
                    onUpdate={(id, newQuantity) => {
                      setPagination(prev => ({
                        ...prev,
                        data: prev.data.map(item => item.id === id ? { ...item, quantity: newQuantity } : item),
                      }));
                    }}
                  />
                ))
              ) : (
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '50vh', gap: 2 }}>
                  <Typography variant="h5" color="text.secondary" sx={{ fontWeight: 500 }}>
                    {getTranslation(lang, "cart_empty")}
                  </Typography>
                </Box>
              )}
            </Box>

            {pagination.totalPages > 1 && (
              <Box display="flex" justifyContent="center" mt={2} pb={2}>
                <Pagination
                  count={pagination.totalPages}
                  page={pagination.currentPage + 1}
                  onChange={(e, value) => fetchCartItems(value - 1)}
                  color="primary"
                />
              </Box>
            )}
          </Paper>

          {/* Order Summary */}
          <Paper elevation={0} sx={{ flex: 1, borderRadius: 2, bgcolor: 'background.paper', p: 3, height: 'fit-content' }}>
            <Typography variant="h5" fontWeight={700} mb={3}>{getTranslation(lang, "order_summary")}</Typography>

            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography>{getTranslation(lang, "products_price")}</Typography>
                <Typography fontWeight={600}>{total.toFixed(2)}€</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography>{getTranslation(lang, "delivery_price")}</Typography>
                <Typography fontWeight={600}>0.00€</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography>{getTranslation(lang, "discount")}</Typography>
                <Typography fontWeight={600}>0%</Typography>
              </Box>
              <Divider sx={{ my: 2 }} />
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="h6" fontWeight={700}>{getTranslation(lang, "total")}</Typography>
                <Typography variant="h6" fontWeight={700} color="primary.main">{total.toFixed(2)}€</Typography>
              </Box>
            </Box>

            <Typography variant="h6" fontWeight={600} mb={2}>{getTranslation(lang, "payment_method")}</Typography>
            <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 2, mb: 3 }}>
              {[
                { icon: <PaymentsIcon />, label: getTranslation(lang, "cash") },
                { icon: <CreditCardIcon />, label: getTranslation(lang, "card") },
                { icon: <CurrencyBitcoin />, label: getTranslation(lang, "crypto") },
              ].map((method, idx) => (
                <Paper key={idx} elevation={0} sx={{
                  p: 2, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1,
                  cursor: 'pointer', transition: '0.2s', border: 1, borderColor: 'divider',
                  '&:hover': { borderColor: 'primary.main', bgcolor: 'primary.light', '& .MuiSvgIcon-root': { color: 'primary.main' } }
                }}>
                  {method.icon}
                  <Typography variant="body2" fontWeight={500}>{method.label}</Typography>
                </Paper>
              ))}
            </Box>

            <FormControlLabel control={<Checkbox defaultChecked />} label={getTranslation(lang, "remember_payment_method")} sx={{ mb: 3 }} />

            <Button variant="contained" fullWidth size="large" sx={{ py: 1.5, fontSize: '1.1rem', fontWeight: 600, textTransform: 'none' }}>
              {getTranslation(lang, "proceed_to_checkout")}
            </Button>
          </Paper>
        </Box>
      </Container>
      <Copyright />
    </Box>
  );
};

export default CartContainer;