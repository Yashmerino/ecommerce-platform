import { clearCart } from '../app/api/CartRequest';
import * as AuthInterceptor from '../app/api/AuthInterceptor';

jest.mock('../app/api/AuthInterceptor');

const mockAuthenticatedDelete = AuthInterceptor.authenticatedDelete as jest.MockedFunction<typeof AuthInterceptor.authenticatedDelete>;

describe('CartRequest', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('clearCart', () => {
        it('clears cart successfully', async () => {
            const mockResponse = {
                ok: true,
                json: jest.fn().mockResolvedValue({ message: 'Cart cleared' })
            } as any;

            mockAuthenticatedDelete.mockResolvedValue(mockResponse);

            const result = await clearCart();

            expect(mockAuthenticatedDelete).toHaveBeenCalledWith(
                expect.stringContaining('/api/cart/clear')
            );
            expect(result).toEqual({ message: 'Cart cleared' });
        });

        it('handles error response', async () => {
            const mockResponse = {
                ok: false,
                status: 400
            } as any;

            mockAuthenticatedDelete.mockResolvedValue(mockResponse);

            const result = await clearCart();

            expect(result.ok).toBe(false);
            expect(result.status).toBe(400);
        });
    });
});
