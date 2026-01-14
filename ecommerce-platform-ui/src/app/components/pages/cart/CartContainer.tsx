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
import { STRIPE_PUBLISHABLE_KEY } from '../../../../env-config.ts';
import { PaginatedDTO } from '../../../../types/PaginatedDTO';

export interface CartItem {
  id: number;
  productId: number;
  name: string;
  price: string;
  quantity: number;
}

const CartContainer = () => {
  const MIN_TOTAL = 0.5;
  const jwt = useAppSelector(state => state.jwt);
  const username = useAppSelector(state => state.username.sub);
  const lang = useAppSelector(state => state.lang.lang);
  const navigate = useNavigate();

  // Stripe instance and Elements
  const stripePromise = React.useMemo(() => {
    const stripe = (window as any).Stripe;
    if (stripe) {
      return Promise.resolve(stripe(STRIPE_PUBLISHABLE_KEY));
    }
    return Promise.reject(new Error('Stripe not loaded'));
  }, []);

  const [stripe, setStripe] = React.useState<any>(null);
  const [elements, setElements] = React.useState<any>(null);
  const [cardElement, setCardElement] = React.useState<any>(null);
  const isMountedRef = React.useRef(false);

  // Initialize Stripe Elements
  React.useEffect(() => {
    stripePromise.then((stripeInstance) => {
      setStripe(stripeInstance);
      const elementsInstance = stripeInstance.elements();
      setElements(elementsInstance);
      
      const cardElementInstance = elementsInstance.create('card', {
        style: {
          base: {
            fontSize: '16px',
            color: '#424242',
            '::placeholder': {
              color: '#aaa',
            },
          },
          invalid: {
            color: '#d32f2f',
          },
        },
      });
      setCardElement(cardElementInstance);
    }).catch(err => console.error('Stripe initialization error:', err));
  }, [stripePromise]);

  // Mount CardElement when it's created
  React.useEffect(() => {
    if (cardElement && !isMountedRef.current) {
      const cardElementContainer = document.getElementById('card-element');
      if (cardElementContainer) {
        // Clear any existing Stripe elements first
        cardElementContainer.innerHTML = '';
        
        // Use a small timeout to ensure DOM is ready
        const timeout = setTimeout(() => {
          try {
            cardElement.mount('#card-element');
            isMountedRef.current = true;
            console.log('CardElement mounted successfully');
          } catch (err) {
            console.error('Failed to mount CardElement:', err);
            isMountedRef.current = false;
          }
        }, 100);
        
        return () => clearTimeout(timeout);
      }
    }
  }, [cardElement]);

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

  const [cardHolder, setCardHolder] = React.useState<string>('');

  const [isLoading, setIsLoading] = React.useState<boolean>(false);
  const [error, setError] = React.useState<string | null>(null);
  const [success, setSuccess] = React.useState<string | null>(null);

  const handleCheckout = async () => {
    try {
      setIsLoading(true);
      setError(null);
      setSuccess(null);

      if (total < MIN_TOTAL) {
        setError(getTranslation(lang, "minimum_order_amount") || "Order total must be at least 0.50€.");
        setIsLoading(false);
        return;
      }

      // Validate cardholder name
      if (!cardHolder || !cardHolder.trim()) {
        setError(getTranslation(lang, "fill_card_details") || "Please enter cardholder name");
        setIsLoading(false);
        return;
      }

      if (!stripe || !elements || !cardElement) {
        setError("Payment form not ready. Please refresh the page and try again.");
        setIsLoading(false);
        console.error('Stripe not ready:', { stripe: !!stripe, elements: !!elements, cardElement: !!cardElement });
        return;
      }

      // Verify CardElement is mounted in DOM
      const cardElementContainer = document.getElementById('card-element');
      if (!cardElementContainer || cardElementContainer.children.length === 0) {
        setError("Payment form not fully loaded. Please wait a moment and try again.");
        setIsLoading(false);
        console.error('CardElement not mounted in DOM');
        return;
      }

      // Step 1: Create Stripe Payment Method using CardElement
      let stripePaymentMethodId = '';
      try {
        console.log('Creating payment method with cardElement:', cardElement);
        const { paymentMethod, error } = await stripe.createPaymentMethod({
          type: 'card',
          card: cardElement,
          billing_details: {
            name: cardHolder,
          },
        });

        if (error) {
          console.error('Payment method error:', error);
          setError(error.message || getTranslation(lang, "payment_method_creation_failed") || "Failed to create payment method");
          setIsLoading(false);
          return;
        }

        stripePaymentMethodId = paymentMethod.id;
      } catch (stripeError) {
        setError(getTranslation(lang, "stripe_error") || "Stripe error occurred");
        console.error('Stripe error:', stripeError);
        setIsLoading(false);
        return;
      }

      // Step 2: Create Order
      const orderResponse = await createOrder({
        totalAmount: total,
        status: 'CREATED'
      });

      if (!orderResponse.id) {
        setError(getTranslation(lang, "order_creation_failed") || "Failed to create order");
        setIsLoading(false);
        return;
      }

      const orderId = orderResponse.id;

      // Step 3: Process Payment with real Stripe Payment Method ID
      const paymentResponse = await processPayment(orderId, {
        orderId: orderId,
        amount: total,
        stripeToken: stripePaymentMethodId,
      });

      if (paymentResponse.status !== 200) {
        setError(getTranslation(lang, "payment_failed") || "Payment processing failed");
        setIsLoading(false);
        return;
      }

      // Step 4: Clear Cart after successful payment
      await clearCart();

      setSuccess(getTranslation(lang, "order_placed_successfully") || "Order placed successfully!");
      
      // Reset form
      setCardHolder('');
      if (cardElement) {
        cardElement.clear();
      }

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
    const items = await getCartItems(username, page, pagination.pageSize);
    setPagination(items);
  };

  React.useEffect(() => {
    fetchCartItems();
  }, [jwt.token, username, navigate]);

  React.useEffect(() => {
    setTotal(pagination.totalPrice);
  }, [pagination.data]);

  // Cleanup mounted element on unmount
  React.useEffect(() => {
    return () => {
      isMountedRef.current = false;
    };
  }, []);

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
                    onUpdate={async (id, newQuantity) => {
                      if (newQuantity <= 0) {
                        await fetchCartItems(pagination.currentPage);
                        return;
                      }

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
                  onChange={(_e, value) => fetchCartItems(value - 1)}
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
                value={cardHolder}
                onChange={(e) => setCardHolder(e.target.value)}
                variant="outlined"
                size="small"
              />
              
              <Box
                id="card-element"
                sx={{
                  border: '1px solid',
                  borderColor: 'divider',
                  borderRadius: 1,
                  p: 2,
                  bgcolor: 'background.paper',
                  minHeight: '40px'
                }}
              />
            </Box>
            {total < MIN_TOTAL && (
              <Alert severity="warning" sx={{ mb: 2 }}>
                {getTranslation(lang, "minimum_order_amount") || "Order total must be at least 0.50€ to checkout."}
              </Alert>
            )}
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
            <Button 
              variant="contained" 
              fullWidth 
              size="large" 
              sx={{ py: 1.5, fontSize: '1.1rem', fontWeight: 600, textTransform: 'none' }}
              onClick={handleCheckout}
              disabled={isLoading || pagination.data.length === 0 || total < MIN_TOTAL}
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