export interface PaginatedDTO<T> {
  data: T[];
  currentPage: number;
  totalPages: number;
  totalItems: number;
  totalPrice: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
