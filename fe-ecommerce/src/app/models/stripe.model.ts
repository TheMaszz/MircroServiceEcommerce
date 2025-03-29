export interface StripeResponse {
  status: string;
  message: string;
  sessionId: string;
  sessionUrl: string;
}

export interface StripeRequest {
    amount: number;
    quantity: number;
    name: string;
    currency: string;
}
