export interface StripeResponse {
  status: string;
  message: string;
  sessionId: string;
  sessionUrl: string;
}

export interface CheckOutDTO {
  productRequests: StripeRequest[],
  ids: OrderPaymentDTO[]
}
class StripeRequest {
    amount!: number;
    quantity!: number;
    name!: string;
    currency!: string;
}

class OrderPaymentDTO {
  orderId!: number;
  paymentStatusId!: number;
}
