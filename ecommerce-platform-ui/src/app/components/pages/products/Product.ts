import { Category } from "./AddProductPage";

/**
 * Product's type.
 */
export default interface Product {
    objectID: number,
    name: string,
    price: string,
    categories: Category[],
    description: string
}