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
import { Box, Container, Typography, Paper, Button, Divider, Checkbox, FormControlLabel, Pagination, TextField, CircularProgress, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import CreditCardIcon from '@mui/icons-material/CreditCard';

import Header from '../../Header';
import Copyright from '../../footer/Copyright';
import CartItemCard from './CartItemCard';
import { getCartItems } from '../../../api/CartItemsRequest';
import { createOrder } from '../../../api/OrderRequest';
import { processPayment } from '../../../api/PaymentRequest';
import { clearCart } from '../../../api/CartRequest';
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

  const [cardDetails, setCardDetails] = React.useState({
    cardHolder: '',
    cardNumber: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
  });

  const [saveCard, setSaveCard] = React.useState<boolean>(true);
  const [isLoading, setIsLoading] = React.useState<boolean>(false);
  const [error, setError] = React.useState<string | null>(null);
  const [success, setSuccess] = React.useState<string | null>(null);

  const handleCheckout = async () => {
    try {
      setIsLoading(true);
      setError(null);
      setSuccess(null);

      // Validate card details
      if (!cardDetails.cardHolder || !cardDetails.cardNumber || !cardDetails.expiryMonth || !cardDetails.expiryYear || !cardDetails.cvv) {
        setError(getTranslation(lang, "fill_card_details") || "Please fill in all card details");
        setIsLoading(false);
        return;
      }

      // Step 1: Create Order
      const orderResponse = await createOrder(jwt.token, {
        totalAmount: total,
        status: 'CREATED'
      });

      if ('status' in orderResponse && orderResponse.status === 401) {
        navigate("/login");
        return;
      }

      if (!orderResponse.id) {
        setError(getTranslation(lang, "order_creation_failed") || "Failed to create order");
        setIsLoading(false);
        return;
      }

      const orderId = orderResponse.id;

      // Step 2: Process Payment with Stripe Test Token
      const stripeTestToken = `${cardDetails.cardNumber.replace(/\s/g, '')}-${cardDetails.expiryMonth}-${cardDetails.expiryYear}`; // Stripe test token format
      
      const paymentResponse = await processPayment(jwt.token, orderId, {
        orderId: orderId,
        amount: total,
        stripeToken: stripeTestToken,
      });

      if (paymentResponse.status === 401) {
        navigate("/login");
        return;
      }

      if (paymentResponse.status !== 200) {
        setError(getTranslation(lang, "payment_failed") || "Payment processing failed");
        setIsLoading(false);
        return;
      }

      // Step 3: Clear Cart after successful payment
      const clearCartResponse = await clearCart(jwt.token);
      if (clearCartResponse.status === 401) {
        navigate("/login");
        return;
      }

      setSuccess(getTranslation(lang, "order_placed_successfully") || "Order placed successfully!");
      
      // Reset form
      setCardDetails({
        cardHolder: '',
        cardNumber: '',
        expiryMonth: '',
        expiryYear: '',
        cvv: '',
      });

      // Reset pagination
      setPagination({
        data: [],
        currentPage: 0,
        totalPages: 0,
        totalItems: 0,
        totalPrice: 0,
        pageSize: 5,
        hasNext: false,
        hasPrevious: false,
      });
    } catch (err) {
      setError(getTranslation(lang, "checkout_error") || "An error occurred during checkout");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

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
                        totalPrice: prev.data.reduce((acc, item) => {
                          if (item.id === id) {
                            return acc + parseFloat(item.price) * newQuantity;
                          }
                          return acc + parseFloat(item.price) * item.quantity;
                        }, 0),
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

            
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mb: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2, border: 1, borderRadius: 1}}>
                <CreditCardIcon />
                <Typography fontWeight={600}>{getTranslation(lang, "card")}</Typography>
              </Box>
              
              <TextField
                fullWidth
                label={getTranslation(lang, "cardholder_name") || "Cardholder Name"}
                value={cardDetails.cardHolder}
                onChange={(e) => setCardDetails({ ...cardDetails, cardHolder: e.target.value })}
                variant="outlined"
                size="small"
              />
              
              <TextField
                fullWidth
                label={getTranslation(lang, "card_number") || "Card Number"}
                value={cardDetails.cardNumber}
                onChange={(e) => {
                  const value = e.target.value.replace(/\s/g, '');
                  if (/^\d*$/.test(value) && value.length <= 16) {
                    const formatted = value.replace(/(\d{4})/g, '$1 ').trim();
                    setCardDetails({ ...cardDetails, cardNumber: formatted });
                  }
                }}
                placeholder="1234 5678 9012 3456"
                variant="outlined"
                size="small"
              />
              
              <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                <TextField
                  label={getTranslation(lang, "expiry_date") || "Expiry (MM/YY)"}
                  value={`${cardDetails.expiryMonth}${cardDetails.expiryYear ? '/' + cardDetails.expiryYear : ''}`}
                  onChange={(e) => {
                    let value = e.target.value.replace(/\D/g, '');
                    if (value.length <= 4) {
                      const month = value.slice(0, 2);
                      const year = value.slice(2, 4);
                      setCardDetails({ ...cardDetails, expiryMonth: month, expiryYear: year });
                    }
                  }}
                  placeholder="MM/YY"
                  variant="outlined"
                  size="small"
                />
                
                <TextField
                  label={getTranslation(lang, "cvv") || "CVV"}
                  value={cardDetails.cvv}
                  onChange={(e) => {
                    const value = e.target.value;
                    if (/^\d*$/.test(value) && value.length <= 3) {
                      setCardDetails({ ...cardDetails, cvv: value });
                    }
                  }}
                  placeholder="123"
                  variant="outlined"
                  size="small"
                  type="password"
                />
              </Box>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

            <FormControlLabel 
              control={<Checkbox checked={saveCard} onChange={(e) => setSaveCard(e.target.checked)} />} 
              label={getTranslation(lang, "save_card_details") || "Save card details for next time"} 
              sx={{ mb: 3 }} 
            />

            <Button 
              variant="contained" 
              fullWidth 
              size="large" 
              sx={{ py: 1.5, fontSize: '1.1rem', fontWeight: 600, textTransform: 'none' }}
              onClick={handleCheckout}
              disabled={isLoading || pagination.data.length === 0}
            >
              {isLoading ? <CircularProgress size={24} sx={{ mr: 1 }} /> : null}
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