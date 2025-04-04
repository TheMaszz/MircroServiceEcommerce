class Paginate {
  total!: number;
  limit!: number;
  page!: number;
}

export class ResponseModel<T = any> {
  data!: T | T[];
  response_code!: string;
  response_datetime!: Date;
  response_desc!: string;
  response_ref!: string;
  paginate!: Paginate;
  total_count!: any[];
  status!: number;
}
