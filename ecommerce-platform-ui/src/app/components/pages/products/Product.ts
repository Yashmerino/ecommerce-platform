import { Category } from "./AddProductPage";

/**
 * Product's type.
 */
export default interface Product {
    id: number,
    name: string,
    price: string,
    categories: Category[],
    description: string
}