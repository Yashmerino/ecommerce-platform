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
import { API_BASE_URL } from "../../env-config";
import { authenticatedPost } from "./AuthInterceptor";

export interface PaymentRequest {
  orderId: number;
  amount: number;
  stripeToken: string;
}

const createIdempotencyKey = (): string => {
  const secureCrypto = globalThis.crypto;
  if (typeof secureCrypto?.randomUUID === "function") {
    return secureCrypto.randomUUID();
  }
  if (!secureCrypto?.getRandomValues) {
    throw new Error("A cryptographically secure random number generator is required");
  }

  const bytes = secureCrypto.getRandomValues(new Uint8Array(16));
  bytes[6] = (bytes[6] & 0x0f) | 0x40;
  bytes[8] = (bytes[8] & 0x3f) | 0x80;
  const hex = Array.from(bytes, (byte) => byte.toString(16).padStart(2, "0")).join("");
  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
};

export const processPayment = async (orderId: number, paymentData: PaymentRequest): Promise<any> => {
  const storageKey = `payment-idempotency:${orderId}`;
  let idempotencyKey = sessionStorage.getItem(storageKey);
  if (!idempotencyKey) {
    idempotencyKey = createIdempotencyKey();
    sessionStorage.setItem(storageKey, idempotencyKey);
  }

  const response = await authenticatedPost(
    `${API_BASE_URL}/api/payment/${orderId}`,
    paymentData,
    { headers: { "Idempotency-Key": idempotencyKey } }
  );

  if (response.ok) {
    sessionStorage.removeItem(storageKey);
  }

  if (!response.ok) {
    return response;
  }

  return response.json();
};
