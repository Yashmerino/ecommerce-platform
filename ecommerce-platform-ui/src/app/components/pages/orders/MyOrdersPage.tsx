import * as React from 'react';
import {
    Box,
    Container,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Chip,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Alert,
    Pagination,
    TextField,
    CircularProgress
} from '@mui/material';
import Header from '../../Header';
import Copyright from '../../footer/Copyright';
import { useAppSelector } from '../../../hooks';
import { useNavigate } from 'react-router-dom';
import { getUserOrders, OrderWithPayment } from '../../../api/OrderRequest';
import { processPayment } from '../../../api/PaymentRequest';
import { getTranslation } from '../../../../i18n/i18n';
import { STRIPE_PUBLISHABLE_KEY } from '../../../../env-config';
import ReceiptIcon from '@mui/icons-material/Receipt';
import RefreshIcon from '@mui/icons-material/Refresh';

const MyOrdersPage = () => {
    const jwt = useAppSelector(state => state.jwt.token);
    const lang = useAppSelector(state => state.lang.lang);
    const navigate = useNavigate();

    const [orders, setOrders] = React.useState<OrderWithPayment[]>([]);
    const [page, setPage] = React.useState(0);
    const [totalPages, setTotalPages] = React.useState(0);
    const [pageSize] = React.useState(10);
    const [retryDialogOpen, setRetryDialogOpen] = React.useState(false);
    const [selectedOrder, setSelectedOrder] = React.useState<OrderWithPayment | null>(null);
    const [retryError, setRetryError] = React.useState<string>('');
    const [retrySuccess, setRetrySuccess] = React.useState(false);
    const [isProcessing, setIsProcessing] = React.useState(false);

    // Stripe setup
    const [stripe, setStripe] = React.useState<any>(null);
    const [cardElement, setCardElement] = React.useState<any>(null);
    const [cardHolder, setCardHolder] = React.useState<string>('');
    const isMountedRef = React.useRef(false);

    // Initialize Stripe
    React.useEffect(() => {
        const stripeObj = (window as any).Stripe;
        if (stripeObj) {
            const stripeInstance = stripeObj(STRIPE_PUBLISHABLE_KEY);
            setStripe(stripeInstance);
            const elements = stripeInstance.elements();
            const card = elements.create('card', {
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
            setCardElement(card);
        }
    }, []);

    // Mount card element when dialog opens
    React.useEffect(() => {
        if (cardElement && retryDialogOpen && !isMountedRef.current) {
            const timeout = setTimeout(() => {
                const container = document.getElementById('retry-card-element');
                if (container) {
                    container.innerHTML = '';
                    try {
                        cardElement.mount('#retry-card-element');
                        isMountedRef.current = true;
                    } catch (err) {
                        console.error('Failed to mount card element:', err);
                    }
                }
            }, 100);
            return () => clearTimeout(timeout);
        }
    }, [cardElement, retryDialogOpen]);

    React.useEffect(() => {
        fetchOrders();
    }, [page]);

    const fetchOrders = async () => {
        const response = await getUserOrders(jwt, page, pageSize);

        if ('status' in response && response.status === 401) {
            navigate('/login');
            return;
        }

        if ('data' in response) {
            setOrders(response.data);
            setTotalPages(response.totalPages);
        }
    };

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setPage(value - 1);
    };

    const handleRetryClick = (order: OrderWithPayment) => {
        setSelectedOrder(order);
        setRetryDialogOpen(true);
        setRetryError('');
        setRetrySuccess(false);
        setCardHolder('');
        isMountedRef.current = false;
        if (cardElement) {
            cardElement.clear();
        }
    };

    const handleRetryPayment = async () => {
        if (!selectedOrder) return;

        setRetryError('');
        setRetrySuccess(false);
        setIsProcessing(true);

        try {
            if (!cardHolder || !cardHolder.trim()) {
                setRetryError(getTranslation(lang, 'fill_card_details') || 'Please enter cardholder name');
                setIsProcessing(false);
                return;
            }

            if (!stripe || !cardElement) {
                setRetryError('Payment form not ready. Please try again.');
                setIsProcessing(false);
                return;
            }

            const { paymentMethod, error } = await stripe.createPaymentMethod({
                type: 'card',
                card: cardElement,
                billing_details: {
                    name: cardHolder,
                },
            });

            if (error) {
                setRetryError(error.message || 'Failed to create payment method');
                setIsProcessing(false);
                return;
            }

            const response = await processPayment(jwt, selectedOrder.orderId, {
                orderId: selectedOrder.orderId,
                amount: selectedOrder.totalAmount,
                stripeToken: paymentMethod.id
            });

            if ('status' in response && response.status === 401) {
                navigate('/login');
                setIsProcessing(false);
                return;
            }

            if (response instanceof Response && !response.ok) {
                setRetryError('Payment retry failed. Please try again.');
                setIsProcessing(false);
                return;
            }

            setRetrySuccess(true);
            setIsProcessing(false);
            setTimeout(() => {
                setRetryDialogOpen(false);
                setCardHolder('');
                isMountedRef.current = false;
                fetchOrders();
            }, 2000);
        } catch (error) {
            setRetryError('An error occurred. Please try again.');
            console.error('Payment error:', error);
            setIsProcessing(false);
        }
    };

    const getStatusColor = (status: string): "default" | "success" | "error" | "warning" | "info" => {
        switch (status?.toUpperCase()) {
            case 'SUCCEEDED':
            case 'COMPLETED':
            case 'PAID':
                return 'success';
            case 'PENDING':
                return 'warning';
            case 'FAILED':
            case 'CANCELLED':
                return 'error';
            default:
                return 'default';
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleString('ro-RO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatAmount = (amount: number) => {
        return new Intl.NumberFormat('ro-RO', {
            style: 'currency',
            currency: 'EUR'
        }).format(amount);
    };

    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
            <Header />
            <Container maxWidth="lg" sx={{ flex: 1, py: 4 }}>
                <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 2 }}>
                    <ReceiptIcon fontSize="large" color="primary" />
                    <Typography variant="h4" fontWeight={700} color="primary.main">
                        {getTranslation(lang, 'my_orders') || 'My Orders'}
                    </Typography>
                </Box>

                {orders.length === 0 ? (
                    <Paper elevation={0} sx={{ p: 8, textAlign: 'center', borderRadius: 2 }}>
                        <Typography variant="h6" color="text.secondary">
                            {getTranslation(lang, 'no_orders_found') || 'No orders found'}
                        </Typography>
                    </Paper>
                ) : (
                    <TableContainer component={Paper} elevation={2}>
                        <Table>
                            <TableHead>
                                <TableRow sx={{ bgcolor: 'primary.main' }}>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>
                                        {getTranslation(lang, 'order_id') || 'Order ID'}
                                    </TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>
                                        {getTranslation(lang, 'date') || 'Date'}
                                    </TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>
                                        {getTranslation(lang, 'amount') || 'Amount'}
                                    </TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>
                                        {getTranslation(lang, 'order_status') || 'Order Status'}
                                    </TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>
                                        {getTranslation(lang, 'payment_status') || 'Payment Status'}
                                    </TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }} align="center">
                                        {getTranslation(lang, 'actions') || 'Actions'}
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {orders.map((order) => (
                                    <TableRow key={order.orderId} hover>
                                        <TableCell>#{order.orderId}</TableCell>
                                        <TableCell>{formatDate(order.createdAt)}</TableCell>
                                        <TableCell>{formatAmount(order.totalAmount)}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={order.orderStatus}
                                                color={getStatusColor(order.orderStatus)}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            {order.paymentStatus ? (
                                                <Chip
                                                    label={order.paymentStatus}
                                                    color={getStatusColor(order.paymentStatus)}
                                                    size="small"
                                                />
                                            ) : (
                                                <Typography variant="body2" color="text.secondary">
                                                    {getTranslation(lang, 'no_payment') || 'No payment'}
                                                </Typography>
                                            )}
                                        </TableCell>
                                        <TableCell align="center">
                                            {order.paymentStatus === 'FAILED' && (
                                                <Button
                                                    variant="outlined"
                                                    color="primary"
                                                    size="small"
                                                    startIcon={<RefreshIcon />}
                                                    onClick={() => handleRetryClick(order)}
                                                >
                                                    {getTranslation(lang, 'retry_payment') || 'Retry Payment'}
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {totalPages > 1 && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                        <Pagination
                            count={totalPages}
                            page={page + 1}
                            onChange={handlePageChange}
                            color="primary"
                            size="large"
                            showFirstButton
                            showLastButton
                        />
                    </Box>
                )}
            </Container>
            <Copyright />

            <Dialog open={retryDialogOpen} onClose={() => !isProcessing && setRetryDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle>
                    {getTranslation(lang, 'retry_payment') || 'Retry Payment'}
                </DialogTitle>
                <DialogContent>
                    {retrySuccess ? (
                        <Alert severity="success">
                            {getTranslation(lang, 'payment_retry_success') || 'Payment retry successful! Refreshing orders...'}
                        </Alert>
                    ) : (
                        <>
                            {retryError && (
                                <Alert severity="error" sx={{ mb: 2 }}>
                                    {retryError}
                                </Alert>
                            )}
                            {selectedOrder && (
                                <Box sx={{ 
                                    bgcolor: 'background.default', 
                                    p: 2, 
                                    borderRadius: 1, 
                                    mb: 3,
                                    border: 1,
                                    borderColor: 'divider'
                                }}>
                                    <Typography variant="body2">
                                        <strong>{getTranslation(lang, 'order_id') || 'Order ID'}:</strong> #{selectedOrder.orderId}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>{getTranslation(lang, 'amount') || 'Amount'}:</strong> {formatAmount(selectedOrder.totalAmount)}
                                    </Typography>
                                </Box>
                            )}

                            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
                                {getTranslation(lang, 'cardholder_name') || 'Cardholder Name'}
                            </Typography>
                            <TextField
                                fullWidth
                                value={cardHolder}
                                onChange={(e) => setCardHolder(e.target.value)}
                                placeholder="John Doe"
                                disabled={isProcessing}
                                sx={{ mb: 2 }}
                            />

                            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
                                {getTranslation(lang, 'card_details') || 'Card Details'}
                            </Typography>
                            <Box
                                id="retry-card-element"
                                sx={{
                                    border: '1px solid',
                                    borderColor: 'divider',
                                    borderRadius: 1,
                                    p: 2,
                                    bgcolor: isProcessing ? 'action.disabledBackground' : 'background.paper'
                                }}
                            />
                        </>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setRetryDialogOpen(false)} disabled={isProcessing}>
                        {getTranslation(lang, 'cancel') || 'Cancel'}
                    </Button>
                    {!retrySuccess && (
                        <Button 
                            onClick={handleRetryPayment} 
                            variant="contained" 
                            color="primary"
                            disabled={isProcessing}
                            startIcon={isProcessing ? <CircularProgress size={20} /> : null}
                        >
                            {isProcessing 
                                ? (getTranslation(lang, 'processing') || 'Processing...') 
                                : (getTranslation(lang, 'confirm_retry') || 'Confirm Retry')
                            }
                        </Button>
                    )}
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default MyOrdersPage;
