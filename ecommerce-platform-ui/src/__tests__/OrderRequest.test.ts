import { createOrder, getUserOrders, CreateOrderRequest } from '../app/api/OrderRequest';
import * as AuthInterceptor from '../app/api/AuthInterceptor';

jest.mock('../app/api/AuthInterceptor');

const mockAuthenticatedPost = AuthInterceptor.authenticatedPost as jest.MockedFunction<typeof AuthInterceptor.authenticatedPost>;
const mockAuthenticatedGet = AuthInterceptor.authenticatedGet as jest.MockedFunction<typeof AuthInterceptor.authenticatedGet>;

describe('OrderRequest', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('createOrder', () => {
        it('creates order successfully', async () => {
            const orderData: CreateOrderRequest = {
                totalAmount: 99.99,
                status: 'PENDING'
            };

            const mockResponse = {
                ok: true,
                json: jest.fn().mockResolvedValue({ id: 1, status: 'PENDING' })
            } as any;

            mockAuthenticatedPost.mockResolvedValue(mockResponse);

            const result = await createOrder(orderData);

            expect(mockAuthenticatedPost).toHaveBeenCalledWith(
                expect.stringContaining('/api/order'),
                orderData
            );
            expect(result).toEqual({ id: 1, status: 'PENDING' });
        });

        it('handles error response', async () => {
            const orderData: CreateOrderRequest = {
                totalAmount: 99.99,
                status: 'PENDING'
            };

            const mockResponse = {
                ok: false,
                status: 400
            } as any;

            mockAuthenticatedPost.mockResolvedValue(mockResponse);

            const result = await createOrder(orderData);

            expect(result.ok).toBe(false);
        });
    });

    describe('getUserOrders', () => {
        it('fetches user orders successfully', async () => {
            const mockOrdersResponse = {
                data: [
                    {
                        orderId: 1,
                        totalAmount: 99.99,
                        orderStatus: 'COMPLETED',
                        createdAt: '2024-01-01',
                        paymentId: 100,
                        paymentAmount: 99.99,
                        paymentStatus: 'SUCCEEDED',
                        paymentCreatedAt: '2024-01-01'
                    }
                ],
                currentPage: 0,
                totalPages: 1,
                totalItems: 1,
                pageSize: 10,
                hasNext: false,
                hasPrevious: false
            };

            const mockResponse = {
                ok: true,
                json: jest.fn().mockResolvedValue(mockOrdersResponse)
            } as any;

            mockAuthenticatedGet.mockResolvedValue(mockResponse);

            const result = await getUserOrders(0, 10);

            expect(mockAuthenticatedGet).toHaveBeenCalledWith(
                expect.stringContaining('/api/order/my-orders?page=0&size=10')
            );
            expect(result).toEqual(mockOrdersResponse);
        });

        it('uses default pagination values', async () => {
            const mockResponse = {
                ok: true,
                json: jest.fn().mockResolvedValue({ data: [] })
            } as any;

            mockAuthenticatedGet.mockResolvedValue(mockResponse);

            await getUserOrders();

            expect(mockAuthenticatedGet).toHaveBeenCalledWith(
                expect.stringContaining('page=0&size=10')
            );
        });

        it('handles error response', async () => {
            const mockResponse = {
                ok: false,
                status: 500
            } as any;

            mockAuthenticatedGet.mockResolvedValue(mockResponse);

            const result = await getUserOrders();

            expect(result).toEqual(mockResponse);
        });
    });
});
