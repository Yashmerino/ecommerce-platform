import { processPayment, PaymentRequest } from '../app/api/PaymentRequest';
import * as AuthInterceptor from '../app/api/AuthInterceptor';

jest.mock('../app/api/AuthInterceptor');

const mockAuthenticatedPost = AuthInterceptor.authenticatedPost as jest.MockedFunction<typeof AuthInterceptor.authenticatedPost>;

describe('PaymentRequest', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('processPayment', () => {
        it('processes payment successfully', async () => {
            const orderId = 1;
            const paymentData: PaymentRequest = {
                orderId: 1,
                amount: 99.99,
                stripeToken: 'tok_visa'
            };

            const mockResponse = {
                ok: true,
                json: jest.fn().mockResolvedValue({ 
                    success: true,
                    paymentId: 100
                })
            } as any;

            mockAuthenticatedPost.mockResolvedValue(mockResponse);

            const result = await processPayment(orderId, paymentData);

            expect(mockAuthenticatedPost).toHaveBeenCalledWith(
                expect.stringContaining('/api/payment/1'),
                paymentData
            );
            expect(result).toEqual({ success: true, paymentId: 100 });
        });

        it('handles payment failure', async () => {
            const orderId = 1;
            const paymentData: PaymentRequest = {
                orderId: 1,
                amount: 99.99,
                stripeToken: 'tok_chargeDeclined'
            };

            const mockResponse = {
                ok: false,
                status: 402
            } as any;

            mockAuthenticatedPost.mockResolvedValue(mockResponse);

            const result = await processPayment(orderId, paymentData);

            expect(result.ok).toBe(false);
            expect(result.status).toBe(402);
        });
    });
});
