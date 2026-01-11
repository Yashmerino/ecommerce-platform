package com.yashmerino.ecommerce.model;

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yashmerino.ecommerce.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity for cart that holds products.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "carts")
@Table(name = "carts")
@JsonIgnoreProperties(value = { "intValue" })
public class Cart extends BaseEntity {

    /**
     * Cart's items.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private Set<CartItem> items = new HashSet<>();

    /**
     * User of the cart.
     */
    @JsonIgnore
    @OneToOne(mappedBy = "cart")
    private User user;

    /**
     * Adds an item to the cart.
     *
     * @param item is the item to add.
     */
    public void addItem(final CartItem item) {
        this.items.add(item);
    }

    /**
     * Adds item to the cart.
     *
     * @param items is the list of items to add.
     */
    public void addItems(final CartItem... items) {
        for (var item : items) {
            this.addItem(item);
        }
    }

    /**
     * Removes an item from the cart.
     *
     * @param itemId is the item's id to remove.
     */
    public void removeItem(final Long itemId) {
        this.items.removeIf(item -> item.getId().equals(itemId));
    }

    /**
     * Removes items from the card.
     *
     * @param itemsIds is the list of items' ids to remove.
     */
    public void removeItems(final Long... itemsIds) {
        for (var itemId : itemsIds) {
            this.removeItem(itemId);
        }
    }
}
